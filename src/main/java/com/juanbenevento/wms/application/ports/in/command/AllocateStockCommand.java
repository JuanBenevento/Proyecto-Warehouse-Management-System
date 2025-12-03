package com.juanbenevento.wms.application.ports.in.command;

public record AllocateStockCommand(String sku, Double quantity) {
}
