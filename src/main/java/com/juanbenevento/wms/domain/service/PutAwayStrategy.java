package com.juanbenevento.wms.domain.service;

import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface PutAwayStrategy {
    Optional<Location> findBestLocation(Product product, Double quantity, List<Location> availableLocations);
}