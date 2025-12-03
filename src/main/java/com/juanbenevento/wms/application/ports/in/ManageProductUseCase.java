package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.application.ports.in.command.CreateProductCommand;
import com.juanbenevento.wms.domain.model.Product;

import java.util.List;

public interface ManageProductUseCase {
    Product createProduct(CreateProductCommand command);
    List<Product> getAllProducts();
    Product updateProduct(String sku, CreateProductCommand command);
    void deleteProduct(String sku);
}
