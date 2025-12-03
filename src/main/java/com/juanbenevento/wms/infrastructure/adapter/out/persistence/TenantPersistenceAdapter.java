package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.TenantRepositoryPort;
import com.juanbenevento.wms.domain.model.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TenantPersistenceAdapter implements TenantRepositoryPort {

    private final SpringDataTenantRepository jpaRepository;

    @Override
    public void save(Tenant tenant) {
        TenantEntity entity = toEntity(tenant);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Tenant> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // Mappers
    private TenantEntity toEntity(Tenant domain) {
        return new TenantEntity(
                domain.getId(),
                domain.getName(),
                domain.getStatus(),
                domain.getContactEmail(),
                domain.getCreatedAt()
        );
    }

    private Tenant toDomain(TenantEntity entity) {
        return new Tenant(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                entity.getContactEmail(),
                entity.getCreatedAt()
        );
    }
}