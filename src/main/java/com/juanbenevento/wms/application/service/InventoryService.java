package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.mapper.WmsMapper;
import com.juanbenevento.wms.application.ports.in.command.InternalMoveCommand;
import com.juanbenevento.wms.application.ports.in.command.InventoryAdjustmentCommand;
import com.juanbenevento.wms.application.ports.in.command.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.command.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.dto.InventoryItemResponse;
import com.juanbenevento.wms.application.ports.in.usecases.*;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.event.InventoryAdjustedEvent;
import com.juanbenevento.wms.domain.event.StockReceivedEvent;
import com.juanbenevento.wms.domain.exception.DomainException;
import com.juanbenevento.wms.domain.exception.InventoryItemNotFoundException;
import com.juanbenevento.wms.domain.exception.LocationNotFoundException;
import com.juanbenevento.wms.domain.exception.ProductNotFoundException;
import com.juanbenevento.wms.domain.model.*;
import com.juanbenevento.wms.domain.service.PutAwayStrategy;
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
    private final WmsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(mapper::toItemResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String suggestBestLocation(String sku, Double quantity) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException(sku));

        Double requiredWeight = product.getDimensions().weight() * quantity;
        Double requiredVolume = product.getStorageVolume() * quantity;
        ZoneType targetZone = strategy.determineZone(product);

        List<Location> candidates = locationRepository.findAvailableLocations(targetZone, requiredWeight, requiredVolume);

        if (candidates.isEmpty()) {
            throw new DomainException(String.format("No hay espacio disponible en zona %s para %.2f kg / %.2f m³", targetZone, requiredWeight, requiredVolume));
        }
        return candidates.get(0).getLocationCode();
    }

    @Override
    @Transactional
    public InventoryItemResponse receiveInventory(ReceiveInventoryCommand command) {
        Product product = productRepository.findBySku(command.productSku())
                .orElseThrow(() -> new ProductNotFoundException(command.productSku()));

        Location location = locationRepository.findByCode(command.locationCode())
                .orElseThrow(() -> new LocationNotFoundException(command.locationCode()));

        // Crear Item (Estado inicial IN_QUALITY_CHECK)
        InventoryItem newItem = new InventoryItem(
                generateLpn(),
                command.productSku(),
                product,
                command.quantity(),
                command.batchNumber(),
                command.expiryDate(),
                InventoryStatus.IN_QUALITY_CHECK,
                command.locationCode(),
                null // Version
        );

        location.consolidateLoad(newItem);

        inventoryRepository.save(newItem);
        locationRepository.save(location);

        eventPublisher.publishEvent(new StockReceivedEvent(
                newItem.getLpn(), newItem.getProductSku(), newItem.getQuantity(),
                location.getLocationCode(), getCurrentUser(), LocalDateTime.now()
        ));

        return mapper.toItemResponse(newItem);
    }

    @Override
    @Transactional
    public void putAwayInventory(PutAwayInventoryCommand command) {
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new InventoryItemNotFoundException(command.lpn()));

        if (item.getLocationCode() != null && item.getLocationCode().equals(command.targetLocationCode())) {
            return;
        }

        Location oldLoc = locationRepository.findByCode(item.getLocationCode())
                .orElseThrow(() -> new LocationNotFoundException(item.getLocationCode(), "Origen"));
        Location newLoc = locationRepository.findByCode(command.targetLocationCode())
                .orElseThrow(() -> new LocationNotFoundException(command.targetLocationCode(), "Destino"));

        oldLoc.releaseLoad(item);
        newLoc.consolidateLoad(item);

        item.moveTo(command.targetLocationCode());
        item.approveQualityCheck();

        locationRepository.save(oldLoc);
        locationRepository.save(newLoc);
        inventoryRepository.save(item);
    }

    @Override
    @Transactional
    public void processInternalMove(InternalMoveCommand command) {
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new InventoryItemNotFoundException(command.lpn()));

        Location oldLoc = locationRepository.findByCode(item.getLocationCode())
                .orElseThrow(() -> new LocationNotFoundException(item.getLocationCode(), "Origen"));
        Location newLoc = locationRepository.findByCode(command.targetLocationCode())
                .orElseThrow(() -> new LocationNotFoundException(command.targetLocationCode(), "Destino"));

        oldLoc.releaseLoad(item);
        newLoc.consolidateLoad(item);

        item.moveTo(command.targetLocationCode());

        locationRepository.save(oldLoc);
        locationRepository.save(newLoc);
        inventoryRepository.save(item);
    }

    @Override
    @Transactional
    public void processInventoryAdjustment(InventoryAdjustmentCommand command) {
        InventoryItem item = inventoryRepository.findByLpn(command.lpn())
                .orElseThrow(() -> new InventoryItemNotFoundException(command.lpn()));

        Location location = locationRepository.findByCode(item.getLocationCode())
                .orElseThrow(() -> new LocationNotFoundException(item.getLocationCode()));

        double oldQty = item.getQuantity();
        double newQty = command.newQuantity();

        if (oldQty == newQty) return;

        // ESTRATEGIA DE AJUSTE SEGURO:
        // 1. Sacamos el item completamente de la ubicación (liberamos su peso actual)
        location.releaseLoad(item);

        // 2. Modificamos la cantidad del item
        // Nota: Asegúrate de tener setQuantity o addQuantity que permita esto en InventoryItem
        if (newQty <= 0) {
            item.setQuantity(0.0);
            item.setStatus(InventoryStatus.SHIPPED); // O un estado de "ELIMINADO"
            // No lo volvemos a consolidar en la ubicación (peso 0)
        } else {
            item.setQuantity(newQty);
            // 3. Volvemos a meter el item en la ubicación (recalcula peso con la nueva cantidad)
            location.consolidateLoad(item);
        }

        locationRepository.save(location);
        inventoryRepository.save(item);

        eventPublisher.publishEvent(new InventoryAdjustedEvent(
                item.getLpn(), item.getProductSku(), oldQty, newQty,
                command.reason(), location.getLocationCode(), getCurrentUser(), LocalDateTime.now()
        ));
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    private String generateLpn() {
        return "LPN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}