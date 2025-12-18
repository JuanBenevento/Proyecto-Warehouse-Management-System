package com.juanbenevento.wms.application.ports.in.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryAdjustmentCommand(
        @NotBlank(message = "El LPN es obligatorio")
        String lpn,

        @NotNull(message = "La nueva cantidad es obligatoria")
        @Min(value = 0, message = "La cantidad no puede ser negativa")
        Double newQuantity,

        @NotBlank(message = "El motivo es obligatorio")
        String reason // Ej: "Dañado", "Perdido", "Error de conteo"
) {}