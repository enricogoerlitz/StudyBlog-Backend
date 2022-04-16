package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;

public final class Transformer {
    /*
    public static ApplicationUserModel userEntityToModel(Optional<ApplicationUserEntity> userEntity) {
        if(userEntity.isEmpty()) return null;
        return userEntityToModel(userEntity.get());
    }


     */
    /*
    public static ApplicationUserModel userEntityToModel(ApplicationUserEntity userEntity) {
        if(userEntity == null) return null;
        return new ApplicationUserModel(
            userEntity.getId(),
            userEntity.getUsername(),
            userEntity.getPassword(),
            userEntity.getRole()
        );
    }

     */
/*
    public static ApplicationUserEntity userModelToEntity(ApplicationUserModel userModel) {
        if(userModel == null) return null;
        return new ApplicationUserEntity(
            userModel.getId(),
            userModel.getUsername(),
            userModel.getPassword(),
            userModel.getRole()
        );
    }
    /*

    public static List<BlogPostEntity> blockPostModelToEntityList(List<BlogPostModel> blogPosts) {
        if(blogPosts == null) return null;

        return blogPosts.stream().map(blogpost -> new BlogPostEntity(
                blogpost.getId(),
                blogpost.getTitle(),
                blogpost.getContent(),
                blogpost.getCreationDate(),
                blogpost.getLastEditDate(),
                Transformer.userModelToEntity(blogpost.getCreator())
        )).toList();
    }

    public static List<BlogPostModel> blogPostEntityToModelList(List<BlogPostEntity> blogPosts) {
        if(blogPosts == null) return null;
        return blogPosts.stream().map(blogpost -> new BlogPostModel(
                blogpost.getId(),
                blogpost.getTitle(),
                blogpost.getContent(),
                blogpost.getCreationDate(),
                blogpost.getLastEditDate(),
                Transformer.userEntityToModel(blogpost.getCreator())
        )).toList();
    }

    public static BlogPostEntity blogPostModelToEntity(BlogPostModel blogPost) {
        return new BlogPostEntity(
            blogPost.getId(),
            blogPost.getTitle(),
            blogPost.getContent(),
            blogPost.getCreationDate(),
            blogPost.getLastEditDate(),
            Transformer.userModelToEntity(blogPost.getCreator())
        );
    }

    public static BlogPostModel blogPostEntityToModel(BlogPostEntity blogPost) {
        return new BlogPostModel(
                blogPost.getId(),
                blogPost.getTitle(),
                blogPost.getContent(),
                blogPost.getCreationDate(),
                blogPost.getLastEditDate(),
                Transformer.userEntityToModel(blogPost.getCreator())
        );
    }
     */

    public static BlogPostEntity blogPostModelToEntity(BlogPostModel blogPostRequest, ApplicationUserEntity user) {
        return new BlogPostEntity(
                blogPostRequest.getId(),
                blogPostRequest.getTitle(),
                blogPostRequest.getContent(),
                blogPostRequest.getCreationDate(),
                blogPostRequest.getLastEditDate(),
                user,
                null
        );
    }

}
