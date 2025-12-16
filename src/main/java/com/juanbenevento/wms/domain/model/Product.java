package com.juanbenevento.wms.domain.model;

import java.util.UUID;

public class Product {
    private final UUID id;
    private final String sku;
    private String name;
    private String description;
    private final Dimensions dimensions;
    private Long version;

    public Product(UUID id, String sku, String name, String description, Dimensions dimensions, Long version) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.dimensions = dimensions;
        this.version = version;
        validate();
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("El ID del producto es obligatorio");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("El SKU es obligatorio");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        if (dimensions == null) throw new IllegalArgumentException("Las dimensiones son obligatorias");
    }

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
    public Long getVersion() { return version; }
}