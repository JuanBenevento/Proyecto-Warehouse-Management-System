package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.PutAwayUseCase; // <--- Importante
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.event.StockReceivedEvent;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
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
        Product product = productRepository.findBySku(command.productSku())
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));

        Location location = locationRepository.findByCode(command.locationCode())
                .orElseThrow(() -> new IllegalArgumentException("Ubicación no existe"));

        Double totalWeight = product.getDimensions().weight() * command.quantity();
        Double totalVolume = product.getStorageVolume() * command.quantity();

        location.addLoad(totalWeight, totalVolume);
        locationRepository.save(location);

        InventoryItem newItem = new InventoryItem(
                generateLpn(),
                command.productSku(),
                command.quantity(),
                command.batchNumber(),
                command.expiryDate(),
                InventoryStatus.IN_QUALITY_CHECK,
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

        return savedItem;
    }

    @Override
    @Transactional
    public void putAwayInventory(PutAwayInventoryCommand command) {
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para LPN: " + command.lpn()));

        if (locationRepository.findByCode(command.targetLocationCode()).isEmpty()) {
            throw new IllegalArgumentException("La ubicación destino no existe");
        }

        item.moveTo(command.targetLocationCode());
        item.approveQualityCheck();

        inventoryRepository.save(item);
    }

    private String generateLpn() {
        return "LPN-" + System.currentTimeMillis();
    }
}