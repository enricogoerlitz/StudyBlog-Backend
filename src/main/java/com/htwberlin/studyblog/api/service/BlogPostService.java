package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.ServiceValidator;
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
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final ApplicationUserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public List<BlogPostModel> getBlogPosts(HttpServletRequest request) throws Exception {
        var userFavorites = ServiceValidator.getValidUserFavoriteBlogPostsByRequestAsSet(request, favoriteRepository);
        return Transformer.blogPostEntitiesToModels(blogPostRepository.findAll(), userFavorites);
    }

    public BlogPostModel addBlogpost(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        var requestUser = getValidDbRequestUser(request);

        blogPost.setCreatorId(requestUser.getId());
        blogPost.setCreationDate(new Date());
        blogPost.setLastEditDate(new Date());

        var blogPostEntity = Transformer.blogPostModelToEntity(blogPost, requestUser);
        var savedBlogPost = blogPostRepository.save(blogPostEntity);

        return Transformer.blogPostEntityToModel(savedBlogPost, false);
    }

    public BlogPostEntity addBlogpostDEV(BlogPostEntity blogPost) {
        return blogPostRepository.save(blogPost);
    }

    public BlogPostModel updateBlogPost(HttpServletRequest request, BlogPostModel blogPost) throws Exception {
        var dbBlogPost = getValidBlogPost(blogPost.getId());
        var dbBlogPostCreator = getValidDbBlogPostCreator(dbBlogPost.getCreator().getId());
        var requestUser = getValidDbRequestUser(request);

        ServiceValidator.validateEqualUserIds(
            requestUser,
            dbBlogPostCreator,
            "You are not allowed to update this BlogPost, because you are not the Creator!"
        );

        updateTitleAndContentOfBlogPost(dbBlogPost, blogPost);

        return saveUpdatedBlogPost(dbBlogPost);
    }

    public BlogPostModel updateBlogPostByAdmin(BlogPostModel blogPost) throws Exception {
        var dbBlogPost = getValidBlogPost(blogPost.getId());
        updateTitleAndContentOfBlogPost(dbBlogPost, blogPost);

        return saveUpdatedBlogPost(dbBlogPost);
    }

    public void deleteBlogPost(HttpServletRequest request, String id) throws Exception {
        Long blogPostId = PathVariableParser.parseLong(id);
        var requestUser = getValidDbRequestUser(request);
        var dbBlogPost = getValidBlogPost(blogPostId);

        ServiceValidator.validateEqualUserIds(
            dbBlogPost.getCreator(),
            requestUser,
            "You are not allowed to delete this BlogPost, because you are not the Creator!"
        );

        deleteAllFavoritesFKs(blogPostId);
        blogPostRepository.deleteById(blogPostId);
    }

    public void deleteBlogPostByAdmin(String id) {
        Long blogPostId = PathVariableParser.parseLong(id);
        deleteAllFavoritesFKs(blogPostId);
        blogPostRepository.deleteById(blogPostId);
    }

    private BlogPostModel saveUpdatedBlogPost(BlogPostEntity blogPost) {
        blogPost.setLastEditDate(new Date());
        var updatedBlogpost = blogPostRepository.save(blogPost);

        return Transformer.blogPostEntityToModel(updatedBlogpost, false);
    }

    private void deleteAllFavoritesFKs(Long blogPostId) {
        favoriteRepository.deleteAllByBlogPost_Id(blogPostId);
    }

    private BlogPostEntity getValidBlogPost(Long id) throws Exception {
        return ServiceValidator.getValidBlogPostById(blogPostRepository, id);
    }

    private ApplicationUserEntity getValidDbBlogPostCreator(Long id) throws Exception {
        return ServiceValidator.getValidDbUserById(userRepository, id);
    }

    private ApplicationUserEntity getValidDbRequestUser(HttpServletRequest request) throws Exception {
        return ServiceValidator.getValidDbUserFromRequest(request, userRepository);
    }

    private void updateTitleAndContentOfBlogPost(BlogPostEntity updateBlogPost, BlogPostModel updater) {
        updateBlogPost.setTitle(updater.getTitle());
        updateBlogPost.setContent(updater.getContent());
    }
}
