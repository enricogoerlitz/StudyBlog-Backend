package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** EntityModelTransformer
 *  Static Class for transforming Entities to Models or Models to Entities.
 */
public final class EntityModelTransformer {
    /**
     * Transformed a ApplicationUserEntity to ApplicationUserModel.
     * @param entityUser ApplicationUserEntity
     * @return ApplicationUserModel
     */
    public static ApplicationUserModel userEntityToModel(ApplicationUserEntity entityUser) {
        ObjectValidator.validateNotNullObject(entityUser);

        return new ApplicationUserModel(
            entityUser.getId(),
            entityUser.getUsername(),
            entityUser.getRole()
        );
    }

    /**
     * Transformed a List<ApplicationUserEntity> to List<ApplicationUserModel>
     * @param entityUsers List<ApplicationUserEntity>
     * @return List<ApplicationUserModel>
     */
    public static List<ApplicationUserModel> userEntitiesToModels(List<ApplicationUserEntity> entityUsers) {
        if(entityUsers == null || entityUsers.size() == 0) return new ArrayList<>();
        return entityUsers.stream().map(EntityModelTransformer::userEntityToModel).toList();
    }

    /**
     * Transformed a BlogPostEntity to a BlogPostModel.
     * @param blogPostEntity BlogPostEntity
     * @param isUserFavorite boolean is favorite of the user
     * @return BlogPostModel
     */
    public static BlogPostModel blogPostEntityToModel(BlogPostEntity blogPostEntity, boolean isUserFavorite) {
        ObjectValidator.validateNotNullObject(blogPostEntity);

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

    /**
     * Transformed List<BlogPostEntity> to a List<BlogPostModel>.
     * @param blogPostEntities List<BlogPostEntity>
     * @param favoritesEntities Set<Long> Set of BlogPostIds
     * @return List<BlogPostModel>
     */
    public static List<BlogPostModel> blogPostEntitiesToModels(List<BlogPostEntity> blogPostEntities, Set<Long> favoritesEntities) {
        ObjectValidator.validateNotNullObject(blogPostEntities);
        return blogPostEntities.stream().map(blogPost -> blogPostEntityToModel(blogPost, favoritesEntities.contains(blogPost.getId()))).toList();
    }

    /**
     * Transformed FavoritesEntity to a FavoritesModel.
     * @param favoritesEntity FavoritesEntity
     * @return FavoritesModel
     */
    public static FavoritesModel favoritesEntityToModel(FavoritesEntity favoritesEntity) {
        ObjectValidator.validateNotNullObject(favoritesEntity);

        return new FavoritesModel(
            favoritesEntity.getId(),
            EntityModelTransformer.userEntityToModel(favoritesEntity.getCreator()),
            favoritesEntity.getBlogPost().getId()
        );
    }

    /**
     * Transformed a BlogPostModel to a BlogPostEntity.
     * @param blogPostRequest BlogPostModel
     * @param user ApplicationUserEntity
     * @return BlogPostEntity
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
