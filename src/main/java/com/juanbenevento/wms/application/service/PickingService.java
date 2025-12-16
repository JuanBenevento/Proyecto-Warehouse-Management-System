package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.AllocateStockCommand;
import com.juanbenevento.wms.application.ports.in.usecases.AllocateStockUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.domain.event.StockReservedEvent;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
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
public class PickingService implements AllocateStockUseCase {
    private final InventoryRepositoryPort inventoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void allocateStock(AllocateStockCommand command) {
        List<InventoryItem> availableItems = inventoryRepository.findAvailableStock(command.sku());

        double quantityNeeded = command.quantity();

        double totalAvailable = availableItems.stream().mapToDouble(InventoryItem::getQuantity).sum();
        if (totalAvailable < quantityNeeded) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + totalAvailable + ", Solicitado: " + quantityNeeded);
        }

        // 2. Algoritmo de Asignación con SPLIT (División)
        for (InventoryItem item : availableItems) {
            if (quantityNeeded <= 0) break;

            double currentQty = item.getQuantity();
            double quantityToTake = Math.min(currentQty, quantityNeeded);

            if (currentQty == quantityToTake) {
                // CASO A: Tomamos el item completo (No hace falta dividir)
                item.setStatus(InventoryStatus.RESERVED);
                inventoryRepository.save(item);
            } else {
                // CASO B: Split (División) - Tomamos solo una parte
                // 1. Reducimos la cantidad del item original (que sigue AVAILABLE)
                item.setQuantity(currentQty - quantityToTake);
                inventoryRepository.save(item);

                // 2. Creamos un NUEVO item para la parte reservada
                // Generamos un nuevo LPN temporal para la reserva (Picking LPN)
                InventoryItem reservedPart = new InventoryItem(
                        generatePickingLpn(), // Nuevo ID único
                        item.getProductSku(),
                        quantityToTake, // La cantidad que nos llevamos
                        item.getBatchNumber(),
                        item.getExpiryDate(),
                        InventoryStatus.RESERVED, // Este nace reservado
                        item.getLocationCode(),
                        null// Sigue en el mismo estante físicamente
                );
                // (Si usas AuditableEntity, los campos de auditoría se llenan solos al guardar)
                inventoryRepository.save(reservedPart);
            }

            quantityNeeded -= quantityToTake;
        }

        // 3. Publicar evento de éxito
        eventPublisher.publishEvent(new StockReservedEvent(
                command.sku(),
                command.quantity(),
                getCurrentUser(),
                LocalDateTime.now()
        ));
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    private String generatePickingLpn() {
        // Generamos un LPN especial para diferenciarlo de los pallets originales
        return "PICK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}