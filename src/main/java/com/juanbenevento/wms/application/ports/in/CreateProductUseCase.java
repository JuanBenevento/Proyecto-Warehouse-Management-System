package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.Product;

import java.util.List;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
    List<Product> getAllProducts();

}
