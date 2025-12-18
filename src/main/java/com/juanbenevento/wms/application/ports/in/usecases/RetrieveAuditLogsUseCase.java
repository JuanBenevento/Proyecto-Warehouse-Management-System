package com.juanbenevento.wms.application.ports.in.usecases;

import com.juanbenevento.wms.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface RetrieveAuditLogsUseCase {
    Page<AuditLog> getAuditLogs(
            String sku,
            String lpn,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );
}

