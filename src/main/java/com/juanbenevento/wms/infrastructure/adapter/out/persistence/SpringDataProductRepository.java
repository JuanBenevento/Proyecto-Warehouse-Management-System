package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findBySku(String sku);
}