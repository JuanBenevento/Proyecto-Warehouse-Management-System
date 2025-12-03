package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.CreateProductCommand;
import com.juanbenevento.wms.application.ports.in.ManageProductUseCase;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements ManageProductUseCase {
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

    @Override
    public Product updateProduct(String sku, CreateProductCommand command) {
        Product existing = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Dimensions newDims = new Dimensions(
                command.width(), command.height(), command.depth(), command.weight()
        );

        boolean dimensionsChanged = !existing.getDimensions().equals(newDims);

        if (dimensionsChanged && productRepository.existsInInventory(sku)) {
            throw new IllegalStateException(
                    "No se pueden modificar las dimensiones/peso del producto " + sku +
                            " porque existe stock físico en el almacén. " +
                            "Esto rompería los cálculos de capacidad de las ubicaciones."
            );
        }

        Product updated = new Product(
                existing.getId(),
                sku,
                command.name(),
                command.description(),
                newDims
        );

        return productRepository.save(updated);
    }

    @Override
    public void deleteProduct(String sku) {
        if (productRepository.findBySku(sku).isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        if (productRepository.existsInInventory(sku)) {
            throw new IllegalStateException("No se puede eliminar el producto " + sku + " porque tiene stock físico.");
        }

        productRepository.delete(sku);
    }

}