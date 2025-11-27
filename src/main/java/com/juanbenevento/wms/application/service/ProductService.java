package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.CreateProductCommand;
import com.juanbenevento.wms.application.ports.in.CreateProductUseCase;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService implements CreateProductUseCase {

    // Inyectamos la INTERFAZ, no la implementación (Desacoplamiento total)
    private final ProductRepositoryPort productRepository;

    public ProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProductCommand command) {

        // 1. Validar regla de negocio: ¿Ya existe el SKU?
        if (productRepository.findBySku(command.sku()).isPresent()) {
            throw new IllegalArgumentException("El producto con SKU " + command.sku() + " ya existe.");
        }

        // 2. Convertir Comando -> Value Objects
        Dimensions dims = new Dimensions(
                command.width(),
                command.height(),
                command.depth(),
                command.weight()
        );

        // 3. Crear Entidad de Dominio
        Product newProduct = new Product(
                UUID.randomUUID(), // Generamos ID aquí
                command.sku(),
                command.name(),
                command.description(),
                dims
        );

        // 4. Persistir usando el Puerto de Salida
        return productRepository.save(newProduct);
    }
}