package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.ZoneType;

public record CreateLocationCommand(
        String locationCode,
        ZoneType zoneType,
        Double maxWeight,
        Double maxVolume
) {
    public CreateLocationCommand {
        if (locationCode == null || locationCode.isBlank()) throw new IllegalArgumentException("Código requerido");
        if (maxWeight <= 0) throw new IllegalArgumentException("Peso máximo inválido");
    }
}