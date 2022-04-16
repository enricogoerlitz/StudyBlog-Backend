package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.Transformer;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.models.BlogPostRequestModel;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BlogPostService {
    private final BlogPostRepository blogPostrepository;
    private final ApplicationUserRepository userRepository;

    public List<BlogPostEntity> getBlogPosts() {
        var blogPosts = blogPostrepository.findAll();

        return blogPosts;
    }

    public BlogPostEntity addBlogpost(BlogPostRequestModel blogPost) {
        var user = userRepository.findById(blogPost.getCreatorId());
        if(user == null) return null;
        blogPost.setLastEditDate(new Date());
        blogPost.setCreationDate(new Date());

        var blogPostEntity = Transformer.blogPostRequestModelToEntity(blogPost, user.get());
        var savedBlogPost = blogPostrepository.save(blogPostEntity);
        log.info("blogpost added");
        return savedBlogPost;
    }

    public BlogPostEntity addBlogpost(BlogPostEntity blogPost) {
        var savedBlogPost = blogPostrepository.save(blogPost);
        log.info("blogpost added");
        return savedBlogPost;
    }

    public BlogPostEntity updateBlogPost(BlogPostRequestModel blogPost) {
        var user = userRepository.findById(blogPost.getCreatorId());
        if(user == null) return null;

        blogPost.setLastEditDate(new Date());
        var blogPostEntity = Transformer.blogPostRequestModelToEntity(blogPost, user.get());
        var updatedBlogpost = blogPostrepository.save(blogPostEntity);

        return updatedBlogpost;
    }

    public void deleteBlogPost(Long id) {
        blogPostrepository.deleteById(id);
    }

}
