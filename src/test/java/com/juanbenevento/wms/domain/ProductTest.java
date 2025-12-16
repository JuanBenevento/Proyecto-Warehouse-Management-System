package com.juanbenevento.wms.domain;

import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ProductTest {

    @Test
    void shouldCreateProductSuccessfully() {
        // Given (Dado)
        Dimensions dims = new Dimensions(10.0, 10.0, 10.0, 5.0);

        // When (Cuando)
        Product product = new Product(UUID.randomUUID(), "SKU-123", "Laptop", "Gaming Laptop", dims, null);

        // Then (Entonces)
        Assertions.assertNotNull(product);
        Assertions.assertEquals("SKU-123", product.getSku());
    }

    @Test
    void shouldIdentifyHeavyLoad() {
        // Producto de 50kg
        Dimensions heavyDims = new Dimensions(100.0, 100.0, 100.0, 50.0);
        Product heavyProduct = new Product(UUID.randomUUID(), "SKU-HEAVY", "Motor", "V8", heavyDims, 1L);

        Assertions.assertTrue(heavyProduct.requiresHeavyMachinery());
    }

    @Test
    void shouldThrowErrorForInvalidWeight() {
        // Intentamos crear dimensiones con peso 0
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Dimensions(10.0, 10.0, 10.0, 0.0);
        });
    }
}