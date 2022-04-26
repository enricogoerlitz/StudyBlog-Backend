package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.helper.PathVariableParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/** BlogPostService
 *  Service for BlogPost BusinessLogic
 */
@Service
@RequiredArgsConstructor
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final ApplicationUserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ServiceValidator serviceValidator;
    private final PathVariableParser pathVarParser;
    private final EntityModelTransformer transformer;

    /**
     * Fetched all BlogPosts.
     * By an error, this method throws an exception.
     * @param request http.request
     * @return List<BlogPostModel> BlogPosts with favorites-list
     * @throws Exception handle exception
     */
    public List<BlogPostModel> getBlogPosts(HttpServletRequest request) throws Exception {
        var userFavorites = serviceValidator.getValidUserFavoriteBlogPostIdsByRequestAsSet(request, favoriteRepository);
        return transformer.blogPostEntitiesToModels(blogPostRepository.findAll(), userFavorites);
    }

    /**
     * Adds a passed blogPost with a creator, which is the current Request-JWT-Token-User.
     * Sets the Request-User as creator.
     * Sets the current date to creationDate and lastEditDate.
     * By an error, this method throws an exception.
     * @param request http.request
     * @param blogPost http.response
     * @return BlogPostModel created BlogPostModel
     * @throws Exception handle exception
     */
    public BlogPostModel addBlogpost(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        var requestUser = getValidDbRequestUser(request, Role.STUDENT, Role.ADMIN);

        blogPost.setCreatorId(requestUser.getId());
        blogPost.setCreationDate(new Date());
        blogPost.setLastEditDate(new Date());

        var blogPostEntity = transformer.blogPostModelToEntity(blogPost, requestUser);
        var savedBlogPost = blogPostRepository.save(blogPostEntity);

        return transformer.blogPostEntityToModel(savedBlogPost, false);
    }

    /**
     * DEVELOPMENT -> FOR DUMMY POSTS
     * Saves a BlogPost without any validation.
     * @param blogPost BlogPostEntity newBlogPost
     * @return BlogPostEntity createdBlogPost
     */
    public BlogPostEntity addBlogPostDEV(BlogPostEntity blogPost) {
        return blogPostRepository.save(blogPost);
    }

    /**
     * Updates a BlogPost.
     * Only if the Request-JWT-User is the creator, this blogpost will be updated.
     * The title and the content will passed with the data and changed.
     * The lastEditDate will set by the server.
     * By an error, this method throws an exception.
     * @param request http.request
     * @param blogPost BlogPostModel updatedBlogPost
     * @return BlogPostModel updatedBlogPost
     * @throws Exception handle exception
     */
    public BlogPostModel updateBlogPost(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        var dbBlogPost = getValidBlogPost(blogPost.getId());
        var dbBlogPostCreator = getValidDbBlogPostCreator(dbBlogPost.getCreator().getId());
        var requestUser = getValidDbRequestUser(request, Role.STUDENT, Role.ADMIN);

        serviceValidator.validateEqualUserIds(
            requestUser,
            dbBlogPostCreator,
            "You are not allowed to update this BlogPost, because you are not the Creator!"
        );

        updateTitleAndContentOfBlogPost(dbBlogPost, blogPost);

        return saveUpdatedBlogPost(dbBlogPost);
    }

    /**
     * This route is for admin-users only.
     * Admins can update every BlogPost.
     * The title and the content will passed with the data and changed.
     * The lastEditDate will set by the server.
     * By an error, this method throws an exception.
     * @param blogPost BlogPostModel updatedBlogPost
     * @return BlogPostModel
     * @throws Exception handle exception
     */
    public BlogPostModel updateBlogPostByAdmin(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        serviceValidator.validateIsUserInRole(getValidDbRequestUser(request), Role.ADMIN);
        var dbBlogPost = getValidBlogPost(blogPost.getId());
        updateTitleAndContentOfBlogPost(dbBlogPost, blogPost);

        return saveUpdatedBlogPost(dbBlogPost);
    }

    /**
     * Deletes a blogPost.
     * Only if the Request-JWT-User is the creator, this blogpost will be deleted.
     * All references in th DB will be deleted too.
     * By an error, this method throws an exception.
     * @param request http.request
     * @param id String blogPostId
     * @throws Exception handled exception
     */
    public void deleteBlogPost(HttpServletRequest request, String id) throws Exception {
        Long blogPostId = pathVarParser.parseLong(id);
        var requestUser = getValidDbRequestUser(request);
        var dbBlogPost = getValidBlogPost(blogPostId);

        serviceValidator.validateEqualUserIds(
            dbBlogPost.getCreator(),
            requestUser,
            "You are not allowed to delete this BlogPost, because you are not the Creator!"
        );

        deleteAllFavoritesFKs(blogPostId);
        blogPostRepository.deleteById(blogPostId);
    }

    /**
     * This route is for admin-users only.
     * Admins can delete every BlogPost.
     * All references in th DB will be deleted too.
     * By an error, this method throws an exception.
     * @param id String
     */
    public void deleteBlogPostByAdmin(HttpServletRequest request, String id) throws Exception {
        serviceValidator.validateIsUserInRole(getValidDbRequestUser(request), Role.ADMIN);
        Long blogPostId = pathVarParser.parseLong(id);
        deleteAllFavoritesFKs(blogPostId);
        blogPostRepository.deleteById(blogPostId);
    }

    /**
     * HelperMethod for saving updatedBlogPosts.
     * Sets the lastEditDate.
     * @param blogPost BlogPostEntity
     * @return BlogPostModel updatedBlogPostModel
     */
    private BlogPostModel saveUpdatedBlogPost(BlogPostEntity blogPost) {
        blogPost.setLastEditDate(new Date());
        var updatedBlogpost = blogPostRepository.save(blogPost);

        return transformer.blogPostEntityToModel(updatedBlogpost, false);
    }

    /**
     * HelperMethod for deleting DB-References to favorites by blogPostId.
     * @param blogPostId Long blogPostId
     */
    private void deleteAllFavoritesFKs(Long blogPostId) {
        favoriteRepository.deleteAllByBlogPost_Id(blogPostId);
    }

    /**
     * HelperMethod to get a validDbBlogPost by id.
     * @param id Long blogPostId
     * @return BlogPostEntity
     * @throws Exception handle exception
     */
    private BlogPostEntity getValidBlogPost(Long id) throws Exception {
        return serviceValidator.getValidBlogPostById(blogPostRepository, id);
    }

    /**
     * HelperMethod to get a validDbBlogPostCreator by creatorId.
     * @param id Long creatorId
     * @return ApplicationUserEntity
     * @throws Exception handle exception
     */
    private ApplicationUserEntity getValidDbBlogPostCreator(Long id) throws Exception {
        return serviceValidator.getValidDbUserById(userRepository, id);
    }

    /**
     * HelperMethod to get a validDbRequestUser by Request-JWT-User.
     * @param request http.request
     * @return ApplicationUserEntity
     * @throws Exception handle exception
     */
    private ApplicationUserEntity getValidDbRequestUser(HttpServletRequest request, Role... authenticatedRoles) throws Exception {
        return serviceValidator.getValidDbUserFromRequest(request, userRepository, authenticatedRoles);
    }

    /**
     * Updates the title and content of the updateBlogPost.
     * @param updateBlogPost BlogPostEntity updateBlogPost
     * @param updater BlogPostModel updaterModel
     */
    private void updateTitleAndContentOfBlogPost(BlogPostEntity updateBlogPost, BlogPostModel updater) {
        updateBlogPost.setTitle(updater.getTitle());
        updateBlogPost.setContent(updater.getContent());
    }
}
