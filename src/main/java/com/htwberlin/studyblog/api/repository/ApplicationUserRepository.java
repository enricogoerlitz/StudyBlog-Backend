package com.htwberlin.studyblog.api.repository;

import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** ApplicationUserRepository
 *  Repository for User-DB-Interactions.
 */
@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {
    Optional<ApplicationUserEntity> findById(Long id);
    ApplicationUserEntity findByUsername(String username);
    List<ApplicationUserEntity> findAll();
    ApplicationUserEntity save(ApplicationUserEntity user);
    void deleteById(Long id);
}
