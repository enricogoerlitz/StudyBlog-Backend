package com.htwberlin.studyblog.api.repository;

import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoritesEntity, Long> {
    List<FavoritesEntity> findAll();
    // List<FavouriteEntity> findAllByCreator(Long id);
    List<FavoritesEntity> findAllByCreator_Id(Long id);
    List<FavoritesEntity> findAllByCreator_Username(String username);
    void deleteAllByBlogPost_Id(Long id);
    FavoritesEntity findByBlogPost_IdAndCreator_Id(Long blogPostId, Long creatorId);
}
