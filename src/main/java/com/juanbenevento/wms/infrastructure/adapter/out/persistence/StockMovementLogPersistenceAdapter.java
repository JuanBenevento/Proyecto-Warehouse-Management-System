package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.StockMovementLogRepositoryPort;
import com.juanbenevento.wms.domain.model.AuditLog;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.StockMovementLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class StockMovementLogPersistenceAdapter implements StockMovementLogRepositoryPort {

    private final SpringDataStockMovementLogRepository jpaRepository;

    @Override
    public void save(AuditLog log) {
        StockMovementLogEntity entity = toEntity(log);
        jpaRepository.save(entity);
    }

    @Override
    public Page<AuditLog> searchLogs(String sku, String lpn, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<StockMovementLogEntity> entities = jpaRepository.search(sku, lpn, startDate, endDate, pageable);

        return entities.map(this::toDomain);
    }

    private AuditLog toDomain(StockMovementLogEntity entity) {
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

    private StockMovementLogEntity toEntity(AuditLog log) {
        return StockMovementLogEntity.builder()
                .id(log.id())
                .timestamp(log.timestamp())
                .type(log.type())
                .sku(log.sku())
                .lpn(log.lpn())
                .quantity(log.quantity())
                .oldQuantity(log.oldQuantity())
                .newQuantity(log.newQuantity())
                .user(log.user())
                .reason(log.reason())
                .build();
    }
}

