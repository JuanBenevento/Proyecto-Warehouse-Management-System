package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita las anotaciones @Mock
class InventoryServiceTest {

    // 1. MOCKS: Simulamos los "Puertos de Salida" (No usamos base de datos real)
    @Mock
    private InventoryRepositoryPort inventoryRepository;
    @Mock
    private ProductRepositoryPort productRepository;
    @Mock
    private LocationRepositoryPort locationRepository;
    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    // 2. INJECT MOCKS: Instancia el servicio real e inyecta los mocks dentro
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldReceiveInventorySuccessfully() {
        // --- GIVEN (DADO) ---
        // Preparamos los datos de prueba
        String sku = "TV-LG-65";
        String locationCode = "A-01-01";
        ReceiveInventoryCommand command = new ReceiveInventoryCommand(
                sku, 10.0, locationCode, "BATCH-001", LocalDate.now().plusYears(1)
        );

        // Entrenamos a los Mocks: "Cuando te pregunten por este SKU, di que SÍ existe"
        // Simulamos un producto existente
        Product mockProduct = new Product(UUID.randomUUID(), sku, "TV", "Desc", new Dimensions(10.0,10.0,10.0,5.0));
        when(productRepository.findBySku(sku)).thenReturn(Optional.of(mockProduct));

        // Simulamos una ubicación existente
        Location mockLocation = new Location(locationCode, ZoneType.DRY_STORAGE, 100000.0, 100000.0);
        when(locationRepository.findByCode(locationCode)).thenReturn(Optional.of(mockLocation));

        // Simulamos que al guardar, devuelve el mismo item que le pasamos
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // --- WHEN (CUANDO) ---
        // Ejecutamos la lógica real
        InventoryItem result = inventoryService.receiveInventory(command);

        // --- THEN (ENTONCES) ---
        // Verificamos que pasó lo que esperábamos
        assertNotNull(result);
        assertNotNull(result.getLpn()); // Debe haber generado un LPN
        assertTrue(result.getLpn().startsWith("LPN-"));
        assertEquals(InventoryStatus.IN_QUALITY_CHECK, result.getStatus()); // Regla de negocio vital
        assertEquals(10.0, result.getQuantity());

        // Verificamos que el servicio realmente llamó a 'save' una vez
        verify(inventoryRepository, times(1)).save(any(InventoryItem.class));
        verify(eventPublisher, times(1)).publishEvent(any(com.juanbenevento.wms.domain.event.StockReceivedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        // --- GIVEN ---
        String sku = "SKU-FANTASMA";
        ReceiveInventoryCommand command = new ReceiveInventoryCommand(
                sku, 5.0, "A-01", "B1", LocalDate.now()
        );

        // Entrenamos al mock para devolver VACÍO (Producto no existe)
        when(productRepository.findBySku(sku)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        // Esperamos que lance una excepción
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.receiveInventory(command);
        });

        assertEquals("Producto no existe", exception.getMessage());

        // Verificamos que NUNCA intentó guardar nada
        verify(inventoryRepository, never()).save(any());
    }
}