package com.juanbenevento.wms.domain.event;

import java.time.LocalDateTime;

public record StockReceivedEvent(
        String lpn,
        String sku,
        Double quantity,
        String locationCode,
        LocalDateTime occurredAt
) {}