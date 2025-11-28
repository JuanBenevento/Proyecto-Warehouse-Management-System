package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.AllocateStockCommand;
import com.juanbenevento.wms.application.ports.in.AllocateStockUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PickingService implements AllocateStockUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    @Override
    @Transactional // ¡Vital! O todo se reserva, o nada. (Atomicidad)
    public void allocateStock(AllocateStockCommand command) {

        // 1. Buscar todo el stock disponible ordenado por FEFO
        List<InventoryItem> availableItems = inventoryRepository.findAvailableStock(command.sku());

        double quantityNeeded = command.quantity();
        double quantityFound = 0;

        // 2. Algoritmo de Asignación (Greedy Algorithm)
        for (InventoryItem item : availableItems) {
            if (quantityNeeded <= 0) break; // Ya terminamos

            double quantityToTake = Math.min(item.getQuantity(), quantityNeeded);

            // Simulación simple: Si el item tiene 10 y necesito 2,
            // en un sistema real dividiríamos el item.
            // Para este MVP, asumiremos que tomamos el item completo o lo marcamos reservado.
            // Vamos a cambiarle el estado a RESERVED.

            item.setStatus(InventoryStatus.RESERVED);
            inventoryRepository.save(item); // Guardar cambio de estado

            quantityNeeded -= quantityToTake;
            quantityFound += quantityToTake;
        }

        // 3. Validación Final
        if (quantityNeeded > 0) {
            throw new RuntimeException("Stock insuficiente. Faltaron: " + quantityNeeded);
            // Al lanzar excepción, @Transactional hace rollback y deshace las reservas anteriores.
        }
    }
}