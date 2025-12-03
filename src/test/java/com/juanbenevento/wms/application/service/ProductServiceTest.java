package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.CreateProductCommand;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.Dimensions;
import com.juanbenevento.wms.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldThrowError_WhenUpdatingDimensionsWithExistingStock() {
        String sku = "TV-01";
        // Producto existente (Peso 10kg)
        Product existing = new Product(UUID.randomUUID(), sku, "TV", "Desc", new Dimensions(10.0, 10.0, 10.0, 10.0));

        // Comando de actualización (Intentamos cambiar peso a 20kg)
        CreateProductCommand updateCmd = new CreateProductCommand(sku, "TV", "Desc", 10.0, 10.0, 10.0, 20.0);

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(existing));
        // Simulamos que SÍ hay stock físico
        when(productRepository.existsInInventory(sku)).thenReturn(true);

        // Acción y Verificación
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            productService.updateProduct(sku, updateCmd);
        });

        assertTrue(ex.getMessage().contains("existe stock físico"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldAllowUpdate_WhenNoStockExists() {
        String sku = "TV-01";
        Product existing = new Product(UUID.randomUUID(), sku, "TV", "Desc", new Dimensions(10.0, 10.0, 10.0, 10.0));
        // Cambio de peso
        CreateProductCommand updateCmd = new CreateProductCommand(sku, "TV", "Desc", 10.0, 10.0, 10.0, 20.0);

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(existing));
        // Simulamos que NO hay stock
        when(productRepository.existsInInventory(sku)).thenReturn(false);

        productService.updateProduct(sku, updateCmd);

        // Debería guardar
        verify(productRepository).save(any(Product.class));
    }
}