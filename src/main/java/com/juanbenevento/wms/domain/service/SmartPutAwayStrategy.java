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
    public ZoneType determineZone(Product product) {
        String desc = product.getDescription() != null ? product.getDescription().toUpperCase() : "";
        String name = product.getName().toUpperCase();

        if (desc.contains("CONGELADO") || desc.contains("ICE") || name.contains("HELADO")) {
            return ZoneType.FROZEN_STORAGE;
        }
        if (desc.contains("REFRIGERADO") || desc.contains("FRESH") || name.contains("YOGURT")) {
            return ZoneType.COLD_STORAGE;
        }
        if (desc.contains("PELIGRO") || desc.contains("ACID") || desc.contains("QUIMICO")) {
            return ZoneType.HAZMAT;
        }

        return ZoneType.DRY_STORAGE;
    }

}