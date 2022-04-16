package com.htwberlin.studyblog.api.repository;

import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<FavouriteEntity, Long> {
    List<FavouriteEntity> findAll();
    // List<FavouriteEntity> findAllByCreator(Long id);
    List<FavouriteEntity> findAllByCreator_Id(Long id);
}
