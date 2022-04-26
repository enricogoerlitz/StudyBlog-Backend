package com.htwberlin.studyblog.helper;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.helper.ObjectValidator;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class EntityModelTransformerTest implements WithAssertions {

    private final EntityModelTransformer transformer = new EntityModelTransformer(new ObjectValidator());

    private final ApplicationUserEntity entityUser = new ApplicationUserEntity(101L, "testname", "testpw", Role.STUDENT.name());
    private final ApplicationUserEntity entityUser2 = new ApplicationUserEntity(102L, "testname2", "testpw2", Role.ADMIN.name());
    private final BlogPostEntity blogPostEntity = new BlogPostEntity(101L, "title", "content_loong", new Date(), new Date(), entityUser, new ArrayList<FavoritesEntity>());
    private final BlogPostEntity blogPostEntity2 = new BlogPostEntity(102L, "title2", "content_loong2", new Date(), new Date(), entityUser2, new ArrayList<FavoritesEntity>());

    private final List<ApplicationUserEntity> entityUsers = List.of(entityUser, entityUser2);
    private final List<BlogPostEntity> blogPostEntities = List.of(blogPostEntity, blogPostEntity2);


    @Test
    @DisplayName("Should transform a EntityUser to a UserModel.")
    void entityUser_to_userModel() {
        // given
        // pass

        // when
        var modelUser = transformer.userEntityToModel(entityUser);

        // then
        assertThat(modelUser.getId().equals(entityUser.getId()));
        assertThat(modelUser.getUsername().equals(entityUser.getUsername()));
        assertThat(modelUser.getRole().equals(entityUser.getRole()));
    }

    @Test
    @DisplayName("Should transform a list of EntityUsers to a list of UserModels.")
    void list_of_entityUsers_to_list_of_userModels() {
        // given
        // pass

        // when
        var listOfUserModels = transformer.userEntitiesToModels(entityUsers);

        // then
        for(int i = 0; i < listOfUserModels.size(); i++) {
            assertThat(listOfUserModels.get(i).getId().equals(entityUsers.get(i).getId()));
            assertThat(listOfUserModels.get(i).getUsername().equals(entityUsers.get(i).getUsername()));
            assertThat(listOfUserModels.get(i).getRole().equals(entityUsers.get(i).getRole()));
        }
    }

    @Test
    @DisplayName("Should transform a BlogPostEntity to a BlogPostModel.")
    void blogPostEntity_to_blogPostModel() {
        // given
        // pass

        // when
        var blogPostModel = transformer.blogPostEntityToModel(blogPostEntity, true);

        // then
        assertThat(blogPostModel.getId().equals(blogPostEntity.getId()));
        assertThat(blogPostModel.getUsername().equals(blogPostEntity.getCreator().getUsername()));
        assertThat(blogPostModel.getCreatorId().equals(blogPostEntity.getCreator().getId()));
        assertThat(blogPostModel.getTitle().equals(blogPostEntity.getTitle()));
        assertThat(blogPostModel.getContent().equals(blogPostEntity.getContent()));
        assertThat(blogPostModel.getCreationDate().equals(blogPostEntity.getCreationDate()));
        assertThat(blogPostModel.getLastEditDate().equals(blogPostEntity.getLastEditDate()));
        assertThat(blogPostModel.isFavorite());
    }


    @Test
    @DisplayName("Should transform a list of BlogPostEntities to a list of BlogPostModels.")
    void list_of_blogPostEntities_to_list_of_blogPostModels() {
        // given
        // pass

        // when
        var listOfBlogPostModels = transformer.blogPostEntitiesToModels(blogPostEntities, new HashSet<>(List.of(1L, 5L)));

        // then
        for(int i = 0; i < listOfBlogPostModels.size(); i++) {
            assertThat(listOfBlogPostModels.get(i).getId().equals(blogPostEntities.get(i).getId()));
            assertThat(listOfBlogPostModels.get(i).getUsername().equals(blogPostEntities.get(i).getCreator().getUsername()));
            assertThat(listOfBlogPostModels.get(i).getCreatorId().equals(blogPostEntities.get(i).getCreator().getId()));
            assertThat(listOfBlogPostModels.get(i).getTitle().equals(blogPostEntities.get(i).getTitle()));
            assertThat(listOfBlogPostModels.get(i).getContent().equals(blogPostEntities.get(i).getContent()));
            assertThat(listOfBlogPostModels.get(i).getCreationDate().equals(blogPostEntities.get(i).getCreationDate()));
            assertThat(listOfBlogPostModels.get(i).getLastEditDate().equals(blogPostEntities.get(i).getLastEditDate()));
        }
    }

    @Test
    @DisplayName("Should transform a BlogPostModel to a BlogPostEntity.")
    void blogPostModel_to_blogPostEntity() {
        // given
        var blogPostModel = new BlogPostModel(101L, "title", "content", new Date(), new Date(), entityUser.getId(), "username", true);

        // when
        var blogPostEntity = transformer.blogPostModelToEntity(blogPostModel, entityUser);

        // then
        assertThat(blogPostEntity.getId().equals(blogPostModel.getId()));
        assertThat(blogPostEntity.getCreator().getUsername().equals(blogPostModel.getUsername()));
        assertThat(((Long)blogPostEntity.getCreator().getId()).equals(blogPostModel.getCreatorId()));
        assertThat(blogPostEntity.getTitle().equals(blogPostModel.getTitle()));
        assertThat(blogPostEntity.getContent().equals(blogPostModel.getContent()));
        assertThat(blogPostEntity.getCreationDate().equals(blogPostModel.getCreationDate()));
        assertThat(blogPostEntity.getLastEditDate().equals(blogPostModel.getLastEditDate()));
    }

    @Test
    @DisplayName("Should transform a EntityFavorite to a FavoriteModel.")
    void favoriteEntity_to_favoriteModel() {
        // given
        var entityFavorite = new FavoritesEntity(101L, entityUser, blogPostEntity);
        // when
        var favoriteModel = transformer.favoritesEntityToModel(entityFavorite);

        // then
        assertThat(favoriteModel.getId().equals(entityFavorite.getId()));
        assertThat(favoriteModel.getBlogPostId().equals(entityFavorite.getBlogPost().getId()));
        assertThat(favoriteModel.getCreator().getId().equals(entityFavorite.getCreator().getId()));
        assertThat(favoriteModel.getCreator().getUsername().equals(entityFavorite.getCreator().getUsername()));
        assertThat(favoriteModel.getCreator().getRole().equals(entityFavorite.getCreator().getRole()));
    }
}
