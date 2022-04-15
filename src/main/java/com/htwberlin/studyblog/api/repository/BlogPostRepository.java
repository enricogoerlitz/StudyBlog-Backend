package com.htwberlin.studyblog.api.repository;

import com.htwberlin.studyblog.api.modelsentity.BlogPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {
    List<BlogPostEntity> findAll();
}
