package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}