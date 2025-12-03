package com.juanbenevento.wms.application.ports.in.command;

// Un Record es perfecto para transportar datos (DTO)
public record CreateProductCommand(
        String sku,
        String name,
        String description,
        Double width,
        Double height,
        Double depth,
        Double weight
) {
    public CreateProductCommand {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU requerido");
        if (weight == null || weight <= 0) throw new IllegalArgumentException("Peso inválido");
    }
}