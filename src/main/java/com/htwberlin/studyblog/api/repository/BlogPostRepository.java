package com.htwberlin.studyblog.api.repository;

import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {
    List<BlogPostEntity> findAll();
    void deleteById(Long id);
    void deleteAllByCreator_Id(Long id);
}
