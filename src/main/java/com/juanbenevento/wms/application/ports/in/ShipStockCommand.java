package com.juanbenevento.wms.application.ports.in;

public record ShipStockCommand(String sku, Double quantity) {
    public ShipStockCommand {
        if (quantity <= 0) throw new IllegalArgumentException("Cantidad inválida");
    }
}