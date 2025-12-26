package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.AllocateStockCommand;
import com.juanbenevento.wms.application.ports.in.usecases.AllocateStockUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.event.StockReservedEvent;
import com.juanbenevento.wms.domain.exception.DomainException;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import com.juanbenevento.wms.domain.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PickingService implements AllocateStockUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final LocationRepositoryPort locationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void allocateStock(AllocateStockCommand command) {
        List<InventoryItem> availableItems = inventoryRepository.findAvailableStock(command.sku());

        double quantityNeeded = command.quantity();
        double totalAvailable = availableItems.stream().mapToDouble(InventoryItem::getQuantity).sum();

        if (totalAvailable < quantityNeeded) {
            throw new DomainException("Stock insuficiente. Disponible: " + totalAvailable + ", Solicitado: " + quantityNeeded);
        }

        for (InventoryItem item : availableItems) {
            if (quantityNeeded <= 0) break;

            // Cargamos la Location para mantener consistencia
            Location location = locationRepository.findByCode(item.getLocationCode()).orElseThrow();

            double currentQty = item.getQuantity();
            double quantityToTake = Math.min(currentQty, quantityNeeded);

            if (currentQty == quantityToTake) {
                // CASO A: Tomamos todo el pallet
                item.setStatus(InventoryStatus.RESERVED);
                inventoryRepository.save(item);
            } else {
                // CASO B: Split (División)
                // 1. Liberamos temporalmente el item de la ubicación
                location.releaseLoad(item);

                // 2. Reducimos el item original
                item.setQuantity(currentQty - quantityToTake);
                System.out.println("DEBUG TEST: Item seteado a: " + item.getQuantity()); // <--- ¿QUÉ IMPRIME ESTO?
                location.consolidateLoad(item); // Reingresamos el remanente
                inventoryRepository.save(item);

                // 3. Crear nuevo item RESERVADO
                InventoryItem reservedPart = new InventoryItem(
                        generatePickingLpn(),
                        item.getProductSku(),
                        item.getProduct(),
                        quantityToTake,
                        item.getBatchNumber(),
                        item.getExpiryDate(),
                        InventoryStatus.RESERVED,
                        item.getLocationCode(), // <--- Correcto: Misma ubicación
                        null
                );
                // Ingresamos la parte reservada a la ubicación
                location.consolidateLoad(reservedPart);

                inventoryRepository.save(reservedPart);
                locationRepository.save(location);
            }
            quantityNeeded -= quantityToTake;
        }

        eventPublisher.publishEvent(new StockReservedEvent(
                command.sku(), command.quantity(), getCurrentUser(), LocalDateTime.now()
        ));
    }

    private String generatePickingLpn() {
        return "PICK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }
}