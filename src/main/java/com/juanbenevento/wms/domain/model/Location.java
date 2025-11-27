package com.juanbenevento.wms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Location {

    private final String locationCode; // ID Natural (ej: A-01-01)
    private final ZoneType zoneType;

    private final Double maxWeight;
    private final Double maxVolume;

    private Double currentWeight;
    private Double currentVolume;

    public Location(String locationCode, ZoneType zoneType, Double maxWeight, Double maxVolume) {
        this.locationCode = locationCode;
        this.zoneType = zoneType;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.currentWeight = 0.0;
        this.currentVolume = 0.0;
    }

    // --- LOGICA DE NEGOCIO ---

    // Método vital: Calcula si entra un producto nuevo
    public boolean hasSpaceFor(Product product, Double quantity) {
        Double incomingWeight = product.getDimensions().weight() * quantity;
        Double incomingVolume = product.getStorageVolume() * quantity;

        return (currentWeight + incomingWeight) <= maxWeight
                && (currentVolume + incomingVolume) <= maxVolume;
    }

    public void addLoad(Double weight, Double volume) {
        this.currentWeight += weight;
        this.currentVolume += volume;
    }
}