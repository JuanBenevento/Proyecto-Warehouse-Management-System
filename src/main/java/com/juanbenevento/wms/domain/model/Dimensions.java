package com.juanbenevento.wms.domain.model;

public record Dimensions(
        Double width,  // cm
        Double height, // cm
        Double depth,  // cm
        Double weight  // kg
) {
    // Constructor compacto para validaciones (Clean Code)
    public Dimensions {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("Las dimensiones deben ser mayores a 0");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0");
        }
    }

    // Logica de Negocio: Calcular volumen (cm3)
    public Double calculateVolume() {
        return width * height * depth;
    }

    // Logica de Negocio: Determinar si es carga pesada
    public boolean isHeavyLoad() {
        return weight > 20.0; // Regla de negocio: > 20kg es pesado
    }
}
