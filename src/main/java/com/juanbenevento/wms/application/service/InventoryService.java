package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.PutAwayUseCase;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.event.StockReceivedEvent;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService implements ReceiveInventoryUseCase, PutAwayUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final ProductRepositoryPort productRepository;
    private final LocationRepositoryPort locationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public InventoryItem receiveInventory(ReceiveInventoryCommand command) {
        // 1. Validar que el producto exista
        if (productRepository.findBySku(command.productSku()).isEmpty()) {
            throw new IllegalArgumentException("El producto " + command.productSku() + " no existe.");
        }

        // 2. Validar que la ubicación exista
        if (locationRepository.findByCode(command.locationCode()).isEmpty()) {
            throw new IllegalArgumentException("La ubicación " + command.locationCode() + " no existe.");
        }

        // 3. Generar LPN (License Plate Number) Único
        // En un sistema real usaríamos una secuencia de DB, aquí usamos UUID acortado para simular
        String lpn = "LPN-" + System.currentTimeMillis();

        // 4. Crear el objeto de Dominio
        InventoryItem newItem = new InventoryItem(
                lpn,
                command.productSku(),
                command.quantity(),
                command.batchNumber(),
                command.expiryDate(),
                InventoryStatus.IN_QUALITY_CHECK, // Regla de Negocio: Todo entra en Cuarentena
                command.locationCode()
        );

        InventoryItem savedItem = inventoryRepository.save(newItem);

        StockReceivedEvent event = new StockReceivedEvent(
                savedItem.getLpn(),
                savedItem.getProductSku(),
                savedItem.getQuantity(),
                savedItem.getLocationCode(),
                LocalDateTime.now()
        );

        eventPublisher.publishEvent(event);

        // 5. Guardar
        return savedItem;
    }

    @Override
    public void putAwayInventory(PutAwayInventoryCommand command) {
        // 1. Buscar el inventario por LPN
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para LPN: " + command.lpn()));

        // 2. Buscar la ubicación destino
        // (En un futuro aquí validaríamos si cabe o si la zona es correcta)
        if (locationRepository.findByCode(command.targetLocationCode()).isEmpty()) {
            throw new IllegalArgumentException("La ubicación destino no existe");
        }

        // 3. Actualizar la ubicación física
        item.moveTo(command.targetLocationCode());

        // 4. Aprobar Calidad (Cambiar estado a AVAILABLE)
        // Esto hace que el producto ya sea visible para ventas
        item.approveQualityCheck();

        // 5. Guardar cambios
        inventoryRepository.save(item);
    }
}