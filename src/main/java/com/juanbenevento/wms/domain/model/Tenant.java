package com.juanbenevento.wms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Tenant {
    private final String id;
    private final String name;
    private final TenantStatus status;
    private final String contactEmail;
    private final LocalDateTime createdAt;

}