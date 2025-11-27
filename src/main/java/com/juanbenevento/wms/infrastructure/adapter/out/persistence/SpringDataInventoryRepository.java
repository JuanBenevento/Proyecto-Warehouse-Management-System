package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SpringDataInventoryRepository extends JpaRepository<InventoryItemEntity, String> {
    List<InventoryItemEntity> findByProductSku(String productSku);
    List<InventoryItemEntity> findByLocationCode(String locationCode);
}