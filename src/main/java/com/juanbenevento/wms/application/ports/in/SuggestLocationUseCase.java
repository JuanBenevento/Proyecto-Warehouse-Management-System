package com.juanbenevento.wms.application.ports.in;

public interface SuggestLocationUseCase {
    String suggestBestLocation(String sku, Double quantity);
}