package com.juanbenevento.wms.domain.service;

import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
import com.juanbenevento.wms.domain.model.ZoneType;

import java.util.List;
import java.util.Optional;

public interface PutAwayStrategy {
    ZoneType determineZone(Product product);
}