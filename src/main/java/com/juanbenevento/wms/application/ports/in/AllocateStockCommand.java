package com.juanbenevento.wms.application.ports.in;

public record AllocateStockCommand(String sku, Double quantity) {
}
