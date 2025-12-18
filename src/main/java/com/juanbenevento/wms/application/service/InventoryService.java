package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.InternalMoveCommand;
import com.juanbenevento.wms.application.ports.in.command.InventoryAdjustmentCommand;
import com.juanbenevento.wms.application.ports.in.command.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.command.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.usecases.*;
import com.juanbenevento.wms.application.ports.out.*;
import com.juanbenevento.wms.domain.event.InventoryAdjustedEvent;
import com.juanbenevento.wms.domain.event.StockReceivedEvent;
import com.juanbenevento.wms.domain.model.*;
import com.juanbenevento.wms.domain.service.PutAwayStrategy; // Asegúrate de importar tu estrategia de dominio
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService implements
        ReceiveInventoryUseCase,
        PutAwayUseCase,
        ManageInventoryOperationsUseCase,
        RetrieveInventoryUseCase,
        SuggestLocationUseCase
{
    private final InventoryRepositoryPort inventoryRepository;
    private final ProductRepositoryPort productRepository;
    private final LocationRepositoryPort locationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PutAwayStrategy strategy;

    @Override
    public List<InventoryItem> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public String suggestBestLocation(String sku, Double quantity) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe: " + sku));

        Double requiredWeight = product.getDimensions().weight() * quantity;
        Double requiredVolume = product.getStorageVolume() * quantity;

        ZoneType targetZone = strategy.determineZone(product);

        List<Location> candidates = locationRepository.findAvailableLocations(targetZone, requiredWeight, requiredVolume);

        if (candidates.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("No hay espacio disponible en zona %s para %.2f kg / %.2f m³",
                            targetZone, requiredWeight, requiredVolume)
            );
        }

        return candidates.get(0).getLocationCode();
    }

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
                command.locationCode(),
                null
        );

        InventoryItem savedItem = inventoryRepository.save(newItem);

        StockReceivedEvent event = new StockReceivedEvent(
                savedItem.getLpn(),
                savedItem.getProductSku(),
                savedItem.getQuantity(),
                savedItem.getLocationCode(),
                getCurrentUser(),
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

        if (!item.getLocationCode().equals(command.targetLocationCode())) {
            Location oldLoc = locationRepository.findByCode(item.getLocationCode())
                    .orElseThrow(() -> new IllegalStateException("Ubicación origen desconocida"));
            Location newLoc = locationRepository.findByCode(command.targetLocationCode())
                    .orElseThrow(() -> new IllegalArgumentException("La ubicación destino no existe"));

            Product product = productRepository.findBySku(item.getProductSku())
                    .orElseThrow(() -> new IllegalStateException("Producto no encontrado"));

            Double weight = product.getDimensions().weight() * item.getQuantity();
            Double volume = product.getStorageVolume() * item.getQuantity();

            oldLoc.removeLoad(weight, volume);
            newLoc.addLoad(weight, volume);

            locationRepository.save(oldLoc);
            locationRepository.save(newLoc);
        }

        item.moveTo(command.targetLocationCode());
        item.approveQualityCheck();
        inventoryRepository.save(item);
    }

    @Override
    @Transactional
    public void processInternalMove(InternalMoveCommand command) {
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new IllegalArgumentException("LPN no encontrado"));

        Location oldLoc = locationRepository.findByCode(item.getLocationCode())
                .orElseThrow(() -> new IllegalStateException("Ubicación origen corrupta"));

        Location newLoc = locationRepository.findByCode(command.targetLocationCode())
                .orElseThrow(() -> new IllegalArgumentException("Ubicación destino no existe"));

        Product product = productRepository.findBySku(item.getProductSku())
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado"));

        Double totalWeight = product.getDimensions().weight() * item.getQuantity();
        Double totalVolume = product.getStorageVolume() * item.getQuantity();

        oldLoc.removeLoad(totalWeight, totalVolume);
        try {
            newLoc.addLoad(totalWeight, totalVolume);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("No se puede mover: " + e.getMessage());
        }

        item.moveTo(command.targetLocationCode());

        locationRepository.save(oldLoc);
        locationRepository.save(newLoc);
        inventoryRepository.save(item);
    }

    @Override
    @Transactional
    public void processInventoryAdjustment(InventoryAdjustmentCommand command) {
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new IllegalArgumentException("LPN no encontrado"));

        Location location = locationRepository.findByCode(item.getLocationCode())
                .orElseThrow(() -> new IllegalStateException("Ubicación no encontrada"));

        Product product = productRepository.findBySku(item.getProductSku())
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado"));

        double oldQty = item.getQuantity();
        double newQty = command.newQuantity();
        double diffQty = newQty - oldQty;

        if (diffQty == 0) return;

        double weightDiff = product.getDimensions().weight() * Math.abs(diffQty);
        double volumeDiff = product.getStorageVolume() * Math.abs(diffQty);

        if (diffQty > 0) location.addLoad(weightDiff, volumeDiff);
        else location.removeLoad(weightDiff, volumeDiff);

        item.setQuantity(newQty);
        if (newQty == 0) item.setStatus(InventoryStatus.SHIPPED);

        locationRepository.save(location);
        inventoryRepository.save(item);

        InventoryAdjustedEvent event = new InventoryAdjustedEvent(
                item.getLpn(),
                item.getProductSku(),
                oldQty,
                command.newQuantity(),
                command.reason(),
                item.getLocationCode(),
                getCurrentUser(),
                LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    private String generateLpn() {
        return "LPN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}