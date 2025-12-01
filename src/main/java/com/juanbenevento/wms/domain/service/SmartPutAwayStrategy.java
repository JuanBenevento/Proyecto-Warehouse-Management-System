package com.juanbenevento.wms.domain.service;

import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
import com.juanbenevento.wms.domain.model.ZoneType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SmartPutAwayStrategy implements PutAwayStrategy {

    @Override
    public Optional<Location> findBestLocation(Product product, Double quantity, List<Location> availableLocations) {
        Double requiredWeight = product.getDimensions().weight() * quantity;
        Double requiredVolume = product.getStorageVolume() * quantity;

        ZoneType targetZone = determineZone(product);

        return availableLocations.stream()
                .filter(loc -> loc.getZoneType() == targetZone)
                .filter(loc -> loc.hasSpaceFor(requiredWeight, requiredVolume))
                .findFirst();
    }

    private ZoneType determineZone(Product product) {
        String desc = product.getDescription().toUpperCase();
        if (desc.contains("CONGELADO") || desc.contains("ICE")) return ZoneType.FROZEN_STORAGE;
        if (desc.contains("REFRIGERADO") || desc.contains("FRESH")) return ZoneType.COLD_STORAGE;
        if (desc.contains("PELIGRO") || desc.contains("ACID")) return ZoneType.HAZMAT;

        return ZoneType.DRY_STORAGE;
    }
}