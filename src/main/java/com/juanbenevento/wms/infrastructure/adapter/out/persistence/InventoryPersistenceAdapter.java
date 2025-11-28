package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InventoryPersistenceAdapter implements InventoryRepositoryPort {

    private final SpringDataInventoryRepository jpaRepository;

    @Override
    public InventoryItem save(InventoryItem item) {
        InventoryItemEntity entity = toEntity(item);
        InventoryItemEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<InventoryItem> findByLpn(String lpn) {
        return jpaRepository.findById(lpn)
                .map(this::toDomain);
    }

    @Override
    public List<InventoryItem> findByProduct(String sku) {
        return jpaRepository.findByProductSku(sku)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItem> findAvailableStock(String sku) {
        return jpaRepository.findByProductSkuAndStatusOrderByExpiryDateAsc(sku, InventoryStatus.AVAILABLE)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItem> findReservedStock(String sku) {
        // Reutilizamos la query de Spring Data pero filtrando por RESERVED
        return jpaRepository.findByProductSkuAndStatusOrderByExpiryDateAsc(sku, InventoryStatus.RESERVED)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // --- MAPPERS ---

    private InventoryItemEntity toEntity(InventoryItem domain) {
        return InventoryItemEntity.builder()
                .lpn(domain.getLpn())
                .productSku(domain.getProductSku())
                .quantity(domain.getQuantity())
                .batchNumber(domain.getBatchNumber())
                .expiryDate(domain.getExpiryDate())
                .status(domain.getStatus())
                .locationCode(domain.getLocationCode())
                .build();
    }

    private InventoryItem toDomain(InventoryItemEntity entity) {
        return new InventoryItem(
                entity.getLpn(),
                entity.getProductSku(),
                entity.getQuantity(),
                entity.getBatchNumber(),
                entity.getExpiryDate(),
                entity.getStatus(),
                entity.getLocationCode()
        );
    }
}