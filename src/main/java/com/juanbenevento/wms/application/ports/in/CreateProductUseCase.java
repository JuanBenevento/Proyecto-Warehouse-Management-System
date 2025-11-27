package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.Product;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
}
