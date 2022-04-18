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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoritesService {
    private final FavoriteRepository favouritesRepository;
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;

    public Set<Long> getFavoritesByCreator(HttpServletRequest request) throws Exception {
        return ServiceValidator.getValidUserFavoriteBlogPostIdsByRequestAsSet(request, favouritesRepository);
    }

    public FavoritesModel addFavoriteDEV(ApplicationUserEntity creator, BlogPostEntity blogPost) {
        if(creator == null || blogPost == null) return null;
        var favoriteEntity = new FavoritesEntity(null, creator, blogPost);
        var addedFavoriteEntity = favouritesRepository.save(favoriteEntity);

        return EntityModelTransformer.favoritesEntityToModel(addedFavoriteEntity);
    }

    public FavoritesModel addFavorite(HttpServletRequest request, String blogPostId) throws Exception {
        Long validBlogPostId = PathVariableParser.parseLong(blogPostId);
        var requestUser = getValidRequestUser(request);
        var blogPost = ServiceValidator.getValidBlogPostById(blogPostRepository, validBlogPostId);
        checkIsFavoriteAlreadyInDb(blogPost.getId(), requestUser.getId());
        var addedFavoriteEntity = favouritesRepository.save(new FavoritesEntity(null, requestUser, blogPost));

        return EntityModelTransformer.favoritesEntityToModel(addedFavoriteEntity);
    }

    public void removeFavorite(HttpServletRequest request, String blogPostId) throws Exception {
        Long validBlogPostId = PathVariableParser.parseLong(blogPostId);
        var delFavorite = ServiceValidator.getValidFavoriteByBlogPostIdAndRequestUser(
                request,
                userRepository,
                favouritesRepository,
                validBlogPostId
        );

        favouritesRepository.deleteById(delFavorite.getId());
    }

    private void checkIsFavoriteAlreadyInDb(Long blogPostId, Long creatorId) {
        var existingFavoriteEntity = favouritesRepository.findByBlogPost_IdAndCreator_Id(blogPostId, creatorId);
        if(existingFavoriteEntity != null)
            throw new DuplicateKeyException("This blog is already a favorite!");
    }

    private ApplicationUserEntity getValidRequestUser(HttpServletRequest request) throws Exception {
        return ServiceValidator.getValidDbUserFromRequest(request, userRepository);
    }
}
