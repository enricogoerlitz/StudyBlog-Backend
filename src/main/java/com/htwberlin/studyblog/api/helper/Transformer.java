package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import com.htwberlin.studyblog.api.utilities.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Transformer {

    public static ApplicationUserModel userEntityToModel(ApplicationUserEntity entityUser) {
        Validator.validateNotNullObject(entityUser);

        return new ApplicationUserModel(
            entityUser.getId(),
            entityUser.getUsername(),
            entityUser.getRole()
        );
    }

    public static List<ApplicationUserModel> userEntitiesToModels(List<ApplicationUserEntity> entityUsers) {
        if(entityUsers == null || entityUsers.size() == 0) return new ArrayList<>();
        return entityUsers.stream().map(Transformer::userEntityToModel).toList();
    }

    public static BlogPostModel blogPostEntityToModel(BlogPostEntity blogPostEntity, boolean isUserFavorite) {
        Validator.validateNotNullObject(blogPostEntity);

        return new BlogPostModel(
                blogPostEntity.getId(),
                blogPostEntity.getTitle(),
                blogPostEntity.getContent(),
                blogPostEntity.getCreationDate(),
                blogPostEntity.getLastEditDate(),
                blogPostEntity.getCreator().getId(),
                isUserFavorite
        );
    }

    public static List<BlogPostModel> blogPostEntitiesToModels(List<BlogPostEntity> blogPostEntities, Set<Long> favoritesEntities) {
        Validator.validateNotNullObject(blogPostEntities);
        return blogPostEntities.stream().map(blogPost -> blogPostEntityToModel(blogPost, favoritesEntities.contains(blogPost.getId()))).toList();
    }

    public static FavoritesModel favoritesEntityToModel(FavoritesEntity favoritesEntity) {
        Validator.validateNotNullObject(favoritesEntity);

        return new FavoritesModel(
            favoritesEntity.getId(),
            Transformer.userEntityToModel(favoritesEntity.getCreator()),
            favoritesEntity.getBlogPost().getId()
        );
    }

    public static List<FavoritesModel> favoritesEntitiesToModels(List<FavoritesEntity> favoritesEntities) {
        if(favoritesEntities == null || favoritesEntities.size() == 0) return new ArrayList<>();
        return favoritesEntities.stream().map(Transformer::favoritesEntityToModel).toList();
    }

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
