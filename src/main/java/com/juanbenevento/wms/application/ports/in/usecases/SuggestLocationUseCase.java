package com.juanbenevento.wms.application.ports.in.usecases;

public interface SuggestLocationUseCase {
    String suggestBestLocation(String sku, Double quantity);
}