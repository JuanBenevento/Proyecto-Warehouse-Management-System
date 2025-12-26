package com.juanbenevento.wms.application.mapper;

import com.juanbenevento.wms.application.ports.in.dto.*;
import com.juanbenevento.wms.domain.model.*;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WmsMapper {

    // =================================================================
    // DTO MAPPERS (Domain -> API Response)
    // =================================================================

    public InventoryItemResponse toItemResponse(InventoryItem item) {
        if (item == null) return null;
        return new InventoryItemResponse(
                item.getLpn(),
                item.getProductSku(),
                (item.getProduct() != null) ? item.getProduct().getName() : "Desconocido",
                item.getQuantity(),
                (item.getStatus() != null) ? item.getStatus().name() : "N/A",
                item.getBatchNumber(),
                item.getExpiryDate(),
                item.getLocationCode()
        );
    }

    public LocationResponse toLocationResponse(Location location) {
        if (location == null) return null;

        double occupancy = (location.getMaxWeight() > 0)
                ? (location.getCurrentWeight() / location.getMaxWeight()) * 100.0
                : 0.0;

        List<InventoryItemResponse> itemResponses = (location.getItems() != null)
                ? location.getItems().stream().map(this::toItemResponse).toList()
                : Collections.emptyList();

        return new LocationResponse(
                location.getLocationCode(),
                location.getZoneType().name(),
                location.getMaxWeight(),
                location.getCurrentWeight(),
                location.getMaxWeight() - location.getCurrentWeight(),
                location.getMaxVolume(),
                location.getCurrentVolume(),
                Math.round(occupancy * 100.0) / 100.0,
                itemResponses
        );
    }

    public ProductResponse toProductResponse(Product product) {
        if (product == null) return null;
        return new ProductResponse(
                product.getId().toString(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getDimensions().width(),
                product.getDimensions().height(),
                product.getDimensions().depth(),
                product.getDimensions().weight()
        );
    }

    public TenantResponse toTenantResponse(Tenant tenant) {
        if (tenant == null) return null;
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getStatus().name(),
                tenant.getContactEmail(),
                tenant.getCreatedAt()
        );
    }

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                (user.getRole() != null) ? user.getRole().name() : "N/A",
                user.getTenantId()
        );
    }

    // =================================================================
    // PERSISTENCE MAPPERS (Domain <-> Entity)
    // =================================================================

    // --- LOCATION ---
    public LocationEntity toLocationEntity(Location domain) {
        if (domain == null) return null;
        return LocationEntity.builder()
                .locationCode(domain.getLocationCode())
                .zoneType(domain.getZoneType())
                .maxWeight(domain.getMaxWeight())
                .maxVolume(domain.getMaxVolume())
                .currentWeight(domain.getCurrentWeight())
                .currentVolume(domain.getCurrentVolume())
                .version(domain.getVersion())
                .build();
    }

    public Location toLocationDomain(LocationEntity entity, List<InventoryItem> items) {
        if (entity == null) return null;
        return new Location(
                entity.getLocationCode(),
                entity.getZoneType(),
                entity.getMaxWeight(),
                entity.getMaxVolume(),
                items,
                entity.getVersion()
        );
    }

    // --- INVENTORY ITEM ---
    public InventoryItemEntity toItemEntity(InventoryItem domain) {
        if (domain == null) return null;
        return InventoryItemEntity.builder()
                .lpn(domain.getLpn())
                .productSku(domain.getProductSku())
                .quantity(domain.getQuantity())
                .batchNumber(domain.getBatchNumber())
                .expiryDate(domain.getExpiryDate())
                .status(domain.getStatus())
                .locationCode(domain.getLocationCode())
                .version(domain.getVersion())
                .build();
    }

    public InventoryItem toItemDomain(InventoryItemEntity entity) {
        if (entity == null) return null;
        return new InventoryItem(
                entity.getLpn(),
                entity.getProductSku(),
                null,
                entity.getQuantity(),
                entity.getBatchNumber(),
                entity.getExpiryDate(),
                entity.getStatus(),
                entity.getLocationCode(),
                entity.getVersion()
        );
    }

    // --- PRODUCT  ---
    public ProductEntity toProductEntity(Product domain) {
        if (domain == null) return null;
        return ProductEntity.builder()
                .id(domain.getId())
                .sku(domain.getSku())
                .name(domain.getName())
                .description(domain.getDescription())
                .width(domain.getDimensions().width())
                .height(domain.getDimensions().height())
                .depth(domain.getDimensions().depth())
                .weight(domain.getDimensions().weight())
                .version(domain.getVersion())
                .active(true)
                .build();
    }

    public Product toProductDomain(ProductEntity entity) {
        if (entity == null) return null;
        Dimensions dims = new Dimensions(
                entity.getWidth(), entity.getHeight(), entity.getDepth(), entity.getWeight()
        );
        return new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                dims,
                entity.getVersion()
        );
    }

    // --- TENANT ---
    public TenantEntity toTenantEntity(Tenant domain) {
        if (domain == null) return null;
        return TenantEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .status(domain.getStatus())
                .contactEmail(domain.getContactEmail())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Tenant toTenantDomain(TenantEntity entity) {
        if (entity == null) return null;
        return new Tenant(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                entity.getContactEmail(),
                entity.getCreatedAt()
        );
    }

    // --- USER ---
    public UserEntity toUserEntity(User domain) {
        if (domain == null) return null;
        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .password(domain.getPassword())
                .role(domain.getRole())
                .tenantId(domain.getTenantId())
                .build();
    }

    public User toUserDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole(),
                entity.getTenantId()
        );
    }

    // --- AUDIT / STOCK MOVEMENTS ---

    public StockMovementLogEntity toAuditLogEntity(AuditLog domain) {
        if (domain == null) return null;
        return StockMovementLogEntity.builder()
                .id(domain.id())
                .timestamp(domain.timestamp())
                .type(domain.type())
                .sku(domain.sku())
                .lpn(domain.lpn())
                .quantity(domain.quantity())
                .oldQuantity(domain.oldQuantity())
                .newQuantity(domain.newQuantity())
                .user(domain.user())
                .reason(domain.reason())
                .build();
    }

    public AuditLog toAuditLogDomain(StockMovementLogEntity entity) {
        if (entity == null) return null;
        return new AuditLog(
                entity.getId(),
                entity.getTimestamp(),
                entity.getType(),
                entity.getSku(),
                entity.getLpn(),
                entity.getQuantity(),
                entity.getOldQuantity(),
                entity.getNewQuantity(),
                entity.getUser(),
                entity.getReason()
        );
    }
}