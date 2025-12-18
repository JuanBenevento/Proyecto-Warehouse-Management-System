package com.juanbenevento.wms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Location {
    private final String locationCode;
    private final ZoneType zoneType;

    private final Double maxWeight;
    private final Double maxVolume;

    private Double currentWeight;
    private Double currentVolume;

    private Long version;

    public Location(String locationCode, ZoneType zoneType, Double maxWeight, Double maxVolume, Long version) {
        this.locationCode = locationCode;
        this.zoneType = zoneType;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.currentWeight = 0.0;
        this.currentVolume = 0.0;
        this.version = version;
    }

    // --- LOGICA DE NEGOCIO ---

    public boolean hasSpaceFor(Product product, Double quantity) {
        Double incomingWeight = product.getDimensions().weight() * quantity;
        Double incomingVolume = product.getStorageVolume() * quantity;

        return (currentWeight + incomingWeight) <= maxWeight
                && (currentVolume + incomingVolume) <= maxVolume;
    }

    public boolean hasSpaceFor(Double incomingWeight, Double incomingVolume) {
        return (this.currentWeight + incomingWeight <= this.maxWeight) &&
                (this.currentVolume + incomingVolume <= this.maxVolume);
    }

    public void addLoad(Double weight, Double volume) {
        if (!hasSpaceFor(weight, volume)) {
            throw new IllegalStateException("La ubicación " + locationCode + " no soporta esta carga. Excede capacidad.");
        }
        this.currentWeight += weight;
        this.currentVolume += volume;
    }

    public void removeLoad(Double weight, Double volume) {
        this.currentWeight -= weight;
        this.currentVolume -= volume;

        if (this.currentWeight < 0) this.currentWeight = 0.0;
        if (this.currentVolume < 0) this.currentVolume = 0.0;
    }
}