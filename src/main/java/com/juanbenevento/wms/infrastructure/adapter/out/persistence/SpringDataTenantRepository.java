package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTenantRepository extends JpaRepository<TenantEntity, String> {
}