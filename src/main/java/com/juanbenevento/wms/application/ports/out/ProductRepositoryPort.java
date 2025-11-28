package com.juanbenevento.wms.application.ports.out;

import com.juanbenevento.wms.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Optional<Product> findBySku(String sku);
    Product save(Product product);
    List<Product> findAll();
}
