package com.juanbenevento.wms.domain.event;
import java.time.LocalDateTime;

public record StockShippedEvent(
        String sku,
        Double quantity,
        String locationCode,
        String username,
        LocalDateTime occurredAt
) {}