package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.helper.PathVariableParser;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/** FavoritesService
 *  Service for UserBlogPostFavorites BusinessLogic
 */
@Service
@AllArgsConstructor
public class FavoritesService {
    private final FavoriteRepository favouritesRepository;
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final ServiceValidator serviceValidator;
    private final EntityModelTransformer transformer;
    private final PathVariableParser pathVarParser;

    /**
     * Returns the favorite blogPostIds of the Request-JWT-User as Set of BlogPostIds
     * @param request http.request
     * @return Set<Long> BlogPostIds
     * @throws Exception handle exception
     */
    public Set<Long> getFavoritesByCreator(HttpServletRequest request) throws Exception {
        return serviceValidator.getValidUserFavoriteBlogPostIdsByRequestAsSet(request, favouritesRepository);
    }

    /**
     * DEVELOPMENT -> FOR DUMMY POSTS
     * Saves a Favorite without any validation.
     * @param creator ApplicationUserEntity
     * @param blogPost BlogPostEntity
     */
    public void addFavoriteDEV(ApplicationUserEntity creator, BlogPostEntity blogPost) {
        if(creator == null || blogPost == null) return;
        var favoriteEntity = new FavoritesEntity(null, creator, blogPost);
        favouritesRepository.save(favoriteEntity);
    }

    /**
     * Saves a new FavoriteBlogPost to the DB for the Request-JWT-User.
     * Validates, that not favorite is already in the DB.
     * If yes, it throws an exception.
     * Returns the FavoritesModel.
     * @param request http.request
     * @param blogPostId String blogPostId
     * @return FavoritesModel addedFavorite
     * @throws Exception handle exception
     */
    public FavoritesModel addFavorite(HttpServletRequest request, String blogPostId) throws Exception {
        Long validBlogPostId = pathVarParser.parseLong(blogPostId);
        var requestUser = getValidRequestUser(request);
        var blogPost = serviceValidator.getValidBlogPostById(blogPostRepository, validBlogPostId);
        checkIsFavoriteAlreadyInDb(blogPost.getId(), requestUser.getId());
        var addedFavoriteEntity = favouritesRepository.save(new FavoritesEntity(null, requestUser, blogPost));

        return transformer.favoritesEntityToModel(addedFavoriteEntity);
    }

    /**
     * Deletes a Favorite of the Request-JWT-User from the DB.
     * Only the favorite-owner can delete a favorite. (Admins can't delete a favorite of another user!)
     * @param request http.request
     * @param blogPostId String blogPostId
     * @throws Exception handle exception
     */
    public void removeFavorite(HttpServletRequest request, String blogPostId) throws Exception {
        Long validBlogPostId = pathVarParser.parseLong(blogPostId);
        var delFavorite = serviceValidator.getValidFavoriteByBlogPostIdAndRequestUser(
                request,
                userRepository,
                favouritesRepository,
                validBlogPostId
        );

        favouritesRepository.deleteById(delFavorite.getId());
    }

    /**
     * Checks, whether the blogPost is already a favorite of the requestUser.
     * If yes, this method throws an exception.
     * @param blogPostId Long blogPostId
     * @param creatorId Long creatorId
     */
    private void checkIsFavoriteAlreadyInDb(Long blogPostId, Long creatorId) {
        var existingFavoriteEntity = favouritesRepository.findByBlogPost_IdAndCreator_Id(blogPostId, creatorId);
        if(existingFavoriteEntity != null)
            throw new DuplicateKeyException("This blog is already a favorite!");
    }

    /**
     * HelperMethod for getting a validRequestUser
     * @param request http.request
     * @return ApplicationUserEntity
     * @throws Exception handle exception
     */
    private ApplicationUserEntity getValidRequestUser(HttpServletRequest request) throws Exception {
        return serviceValidator.getValidDbUserFromRequest(request, userRepository);
    }
}
