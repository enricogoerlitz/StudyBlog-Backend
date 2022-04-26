package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** EntityModelTransformer
 *  Static Class for transforming Entities to Models or Models to Entities.
 */
@Service
@AllArgsConstructor
public class EntityModelTransformer {
    private final ObjectValidator objValidator;
    /**
     * Transformed a ApplicationUserEntity to ApplicationUserModel.
     * @param entityUser ApplicationUserEntity
     * @return ApplicationUserModel
     */
    public ApplicationUserModel userEntityToModel(ApplicationUserEntity entityUser) {
        objValidator.validateNotNullObject(entityUser);

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
    public List<ApplicationUserModel> userEntitiesToModels(List<ApplicationUserEntity> entityUsers) {
        if(entityUsers == null || entityUsers.size() == 0) return new ArrayList<>();
        return entityUsers.stream().map(this::userEntityToModel).toList();
    }

    /**
     * Transformed a BlogPostEntity to a BlogPostModel.
     * @param blogPostEntity BlogPostEntity
     * @param isUserFavorite boolean is favorite of the user
     * @return BlogPostModel
     */
    public BlogPostModel blogPostEntityToModel(BlogPostEntity blogPostEntity, boolean isUserFavorite) {
        objValidator.validateNotNullObject(blogPostEntity);

        return new BlogPostModel(
            blogPostEntity.getId(),
            blogPostEntity.getTitle(),
            blogPostEntity.getContent(),
            blogPostEntity.getCreationDate(),
            blogPostEntity.getLastEditDate(),
            blogPostEntity.getCreator().getId(),
            blogPostEntity.getCreator().getUsername(),
            isUserFavorite
        );
    }

    /**
     * Transformed List<BlogPostEntity> to a List<BlogPostModel>.
     * @param blogPostEntities List<BlogPostEntity>
     * @param favoritesEntities Set<Long> Set of BlogPostIds
     * @return List<BlogPostModel>
     */
    public List<BlogPostModel> blogPostEntitiesToModels(List<BlogPostEntity> blogPostEntities, Set<Long> favoritesEntities) {
        objValidator.validateNotNullObject(blogPostEntities);
        return blogPostEntities.stream().map(blogPost -> blogPostEntityToModel(blogPost, favoritesEntities.contains(blogPost.getId()))).toList();
    }


    /**
     * Transformed a BlogPostModel to a BlogPostEntity.
     * @param blogPostRequest BlogPostModel
     * @param user ApplicationUserEntity
     * @return BlogPostEntity
     */
    public BlogPostEntity blogPostModelToEntity(BlogPostModel blogPostRequest, ApplicationUserEntity user) {
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

    /**
     * Transformed FavoritesEntity to a FavoritesModel.
     * @param favoritesEntity FavoritesEntity
     * @return FavoritesModel
     */
    public FavoritesModel favoritesEntityToModel(FavoritesEntity favoritesEntity) {
        objValidator.validateNotNullObject(favoritesEntity);

        return new FavoritesModel(
            favoritesEntity.getId(),
            userEntityToModel(favoritesEntity.getCreator()),
            favoritesEntity.getBlogPost().getId()
        );
    }
}
