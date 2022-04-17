package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.helper.Transformer;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.utilities.PathVariableParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * TODO: change return null to throw new Exception
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BlogPostService {
    private final BlogPostRepository blogPostrepository;
    private final ApplicationUserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public List<BlogPostModel> getBlogPosts(HttpServletRequest request) {
        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) return null;
        var userFavorites = favoriteRepository.findAllByCreator_Username(requestUser.getUsername());
        if(userFavorites == null) return null;

        return Transformer.blogPostEntitiesToModels(blogPostrepository.findAll(), userFavorites);
    }

    public BlogPostModel addBlogpost(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) return null;
        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null)
            throw new Exception("Could not find the creator of the blogpost");

        blogPost.setCreatorId(dbUser.getId());
        blogPost.setCreationDate(new Date());
        blogPost.setLastEditDate(new Date());

        var blogPostEntity = Transformer.blogPostModelToEntity(blogPost, dbUser);
        var savedBlogPost = blogPostrepository.save(blogPostEntity);

        log.info("blogpost added");
        return Transformer.blogPostEntityToModel(savedBlogPost, false);
    }

    public BlogPostModel addBlogpost(BlogPostEntity blogPost) {
        return Transformer.blogPostEntityToModel(blogPostrepository.save(blogPost), false);
    }

    public BlogPostEntity addBlogpostDEV(BlogPostEntity blogPost) {
        return blogPostrepository.save(blogPost);
    }

    public BlogPostModel updateBlogPost(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        // TODO: source out
        var dbBlogPost = blogPostrepository.findById(blogPost.getId());
        if(dbBlogPost.isEmpty()) throw new Exception("BlogPost not found!");
        var verifiedDbBlogPost = dbBlogPost.get();

        // TODO: source out
        var blogPostCreator = userRepository.findById(verifiedDbBlogPost.getCreator().getId());
        if(blogPostCreator.isEmpty()) throw new Exception("User of the BlogPost not found!");
        var verifiedBlogPostCreator = blogPostCreator.get();

        // TODO: source out
        var executingUser = ApplicationJWT.getUserFromRequestCookie(request);
        var dbExecutingUser = userRepository.findByUsername(executingUser.getUsername());

        // TODO: source out
        if(dbExecutingUser == null || verifiedBlogPostCreator.getId() != dbExecutingUser.getId())
            throw new AuthorizationServiceException("You are not allowed to update this ressource (Blogpost).");

        verifiedDbBlogPost.setTitle(blogPost.getTitle());
        verifiedDbBlogPost.setContent(blogPost.getContent());

        return saveUpdatedBlogPost(verifiedDbBlogPost);
    }

    public BlogPostModel updateBlogPostByAdmin(BlogPostModel blogPost) throws Exception {
        // TODO: outsource
        var dbBlogPost = blogPostrepository.findById(blogPost.getId());
        if(dbBlogPost.isEmpty()) throw new Exception("BlogPost not found!");
        var verifiedDbBlogPost = dbBlogPost.get();

        verifiedDbBlogPost.setTitle(blogPost.getTitle());
        verifiedDbBlogPost.setContent(blogPost.getContent());

        return saveUpdatedBlogPost(blogPost, verifiedDbBlogPost.getCreator());
    }

    public void deleteBlogPost(HttpServletRequest request, String id) {
        Long blogPostId = PathVariableParser.parseLong(id);

        // TODO: outsource logic
        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) throw new AuthorizationServiceException("User has no valid JWT");

        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null) throw new AuthorizationServiceException("User not found in DB.");

        var blogPost = blogPostrepository.findById(blogPostId);
        if(blogPost.isEmpty()) return;

        // TODO this method in JWT.isSameUser(authUserId | authUser, reqUserId | reqUser)
        if(blogPost.get().getCreator().getId() != dbUser.getId())
            throw new AuthorizationServiceException("User is not the creator and unauthorized to delete this blogpost!");

        deleteAllFavoritesFKs(blogPostId);
        blogPostrepository.deleteById(blogPostId);
    }
    public void deleteBlogPostByAdmin(String id) {
        Long blogPostId = PathVariableParser.parseLong(id);
        deleteAllFavoritesFKs(blogPostId);
        blogPostrepository.deleteById(blogPostId);
    }

    private BlogPostModel saveUpdatedBlogPost(BlogPostModel blogPost, ApplicationUserEntity user) {
        return saveUpdatedBlogPost(Transformer.blogPostModelToEntity(blogPost, user));
    }

    private BlogPostModel saveUpdatedBlogPost(BlogPostEntity blogPost) {
        blogPost.setLastEditDate(new Date());
        var updatedBlogpost = blogPostrepository.save(blogPost);

        return Transformer.blogPostEntityToModel(updatedBlogpost, false);
    }

    private void deleteAllFavoritesFKs(Long blogPostId) {
        favoriteRepository.deleteAllByBlogPost_Id(blogPostId);
    }
}
