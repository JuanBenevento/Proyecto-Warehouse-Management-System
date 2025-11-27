package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataLocationRepository extends JpaRepository<LocationEntity, String> {
    Optional<LocationEntity> findByLocationCode(String locationCode);
}