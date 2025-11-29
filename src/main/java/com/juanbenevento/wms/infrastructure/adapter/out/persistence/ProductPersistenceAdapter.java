package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository jpaRepository;

    public ProductPersistenceAdapter(SpringDataProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);

        ProductEntity savedEntity = jpaRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku)
                .map(this::toDomain); // Si existe, lo convierte a dominio
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain) // Reutilizamos tu método toDomain
                .collect(Collectors.toList());
    }
    // --- MAPPERS ---

    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getDimensions().width(),
                product.getDimensions().height(),
                product.getDimensions().depth(),
                product.getDimensions().weight()
        );
    }

    private Product toDomain(ProductEntity entity) {
        Dimensions dims = new Dimensions(
                entity.getWidth(),
                entity.getHeight(),
                entity.getDepth(),
                entity.getWeight()
        );

        return new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                dims
        );
    }
}