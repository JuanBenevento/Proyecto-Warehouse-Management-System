package com.juanbenevento.wms.domain;

import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
import com.juanbenevento.wms.domain.model.ZoneType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void shouldAcceptLoad_WhenCapacityIsEnough() {
        // Ubicación de 100kg
        Location loc = new Location("A-01", ZoneType.DRY_STORAGE, 100.0, 100.0);

        // Agregamos 50kg
        loc.addLoad(50.0, 50.0);

        assertEquals(50.0, loc.getCurrentWeight());
    }

    @Test
    void shouldRejectLoad_WhenCapacityExceeded() {
        // Ubicación pequeña (10kg)
        Location loc = new Location("A-01", ZoneType.DRY_STORAGE, 10.0, 100.0);

        // Intentamos meter 20kg
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loc.addLoad(20.0, 50.0);
        });

        assertTrue(exception.getMessage().contains("Excede capacidad"));
    }

    @Test
    void shouldCalculateSpaceForProduct() {
        Location loc = new Location("A-01", ZoneType.DRY_STORAGE, 100.0, 100.0);
        // Llenamos con 90kg
        loc.addLoad(90.0, 90.0);

        Product heavyProduct = new Product(UUID.randomUUID(), "P1", "Heavy", "Desc", new Dimensions(2.0,2.0,2.0, 20.0)); // 20kg
        Product lightProduct = new Product(UUID.randomUUID(), "P2", "Light", "Desc", new Dimensions(2.0,2.0,2.0, 5.0));  // 5kg

        // 90 + 20 > 100 -> False
        assertFalse(loc.hasSpaceFor(heavyProduct, 1.0));

        // 90 + 5 <= 100 -> True
        assertTrue(loc.hasSpaceFor(lightProduct, 1.0));
    }
}