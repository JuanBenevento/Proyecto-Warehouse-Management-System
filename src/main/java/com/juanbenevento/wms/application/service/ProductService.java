package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.CreateProductCommand;
import com.juanbenevento.wms.application.ports.in.CreateProductUseCase;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements CreateProductUseCase {
    private final ProductRepositoryPort productRepository;

    public ProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProductCommand command) {

        if (productRepository.findBySku(command.sku()).isPresent()) {
            throw new IllegalArgumentException("El producto con SKU " + command.sku() + " ya existe.");
        }

        Dimensions dims = new Dimensions(
                command.width(),
                command.height(),
                command.depth(),
                command.weight()
        );

        Product newProduct = new Product(
                UUID.randomUUID(),
                command.sku(),
                command.name(),
                command.description(),
                dims
        );

        return productRepository.save(newProduct);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}