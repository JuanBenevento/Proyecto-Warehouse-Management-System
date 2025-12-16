package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.domain.model.InventoryStatus;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataInventoryRepository extends JpaRepository<InventoryItemEntity, String> {
    List<InventoryItemEntity> findByProductSku(String productSku);
    List<InventoryItemEntity> findByLocationCode(String locationCode);
    List<InventoryItemEntity> findByProductSkuAndStatusOrderByExpiryDateAsc(String productSku, InventoryStatus status);
}
