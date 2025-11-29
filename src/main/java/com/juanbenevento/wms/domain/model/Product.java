package com.juanbenevento.wms.domain.model;

import java.util.UUID;

public class Product {
    // Identificador único (generado por negocio, no por DB todavía)
    private final UUID id;
    private final String sku;
    private String name;
    private String description;

    // Composición: El producto "tiene" dimensiones
    private final Dimensions dimensions;

    public Product(UUID id, String sku, String name, String description, Dimensions dimensions) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.dimensions = dimensions;
        validate();
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("El ID del producto es obligatorio");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("El SKU es obligatorio");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (dimensions == null) throw new IllegalArgumentException("Las dimensiones son obligatorias");
    }

    // --- MÉTODOS DE NEGOCIO (RICH MODEL) ---
    public Double getStorageVolume() {
        return this.dimensions.calculateVolume();
    }

    public boolean requiresHeavyMachinery() {
        return this.dimensions.isHeavyLoad();
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Dimensions getDimensions() { return dimensions; }
}