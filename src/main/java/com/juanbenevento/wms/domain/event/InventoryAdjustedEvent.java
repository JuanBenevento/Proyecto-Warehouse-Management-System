package com.juanbenevento.wms.domain.event;

import java.time.LocalDateTime;

public record InventoryAdjustedEvent(
        String lpn,
        String productSku,
        Double oldQuantity,
        Double newQuantity,
        String reason,
        String locationCode,
        LocalDateTime occurredAt
) {}