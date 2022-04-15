package com.htwberlin.studyblog.api.repository;

import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {
    ApplicationUserEntity findByUsername(String username);
    List<ApplicationUserEntity> findAll();
    void deleteById(Long id);
}
