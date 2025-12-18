package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.usecases.RetrieveAuditLogsUseCase;
import com.juanbenevento.wms.application.ports.out.StockMovementLogRepositoryPort;
import com.juanbenevento.wms.domain.model.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService implements RetrieveAuditLogsUseCase {

    private final StockMovementLogRepositoryPort repositoryPort;

    @Override
    public Page<AuditLog> getAuditLogs(
            String sku, 
            String lpn, 
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable
    ) {
        return repositoryPort.searchLogs(sku, lpn, startDate, endDate, pageable);
    }
}

