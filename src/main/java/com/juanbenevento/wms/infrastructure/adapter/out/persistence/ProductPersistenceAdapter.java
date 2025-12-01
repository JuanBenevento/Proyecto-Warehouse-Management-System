package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {
    private final SpringDataProductRepository jpaRepository;
    private final SpringDataInventoryRepository inventoryRepository;

    @Override
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);

        ProductEntity savedEntity = jpaRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku)
                .map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String sku) {
        jpaRepository.findBySku(sku).ifPresent(jpaRepository::delete);
    }

    @Override
    public boolean existsInInventory(String sku) {
        return !inventoryRepository.findByProductSku(sku).isEmpty();
    }

    // --- MAPPERS (Traductores) ---

    private ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .width(product.getDimensions().width())
                .height(product.getDimensions().height())
                .depth(product.getDimensions().depth())
                .weight(product.getDimensions().weight())
                .build();
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