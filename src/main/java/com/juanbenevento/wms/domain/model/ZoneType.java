package com.juanbenevento.wms.domain.model;

public enum ZoneType {
    DRY_STORAGE,    // Seco / General
    COLD_STORAGE,   // Refrigerado (0 a 5 grados)
    FROZEN_STORAGE, // Congelado (-18 grados)
    HAZMAT,         // Materiales Peligrosos
    DOCK_DOOR       // Puerta de Muelle (Zona temporal)
}