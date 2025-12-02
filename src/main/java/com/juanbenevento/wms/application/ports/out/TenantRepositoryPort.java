package com.juanbenevento.wms.application.ports.out;

import com.juanbenevento.wms.domain.model.Tenant;

import java.util.List;

public interface TenantRepositoryPort {
    void save(Tenant tenant);
    boolean existsById(String id);
    List<Tenant> findAll();
}
