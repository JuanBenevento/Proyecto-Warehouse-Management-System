package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.ShipStockCommand;
import com.juanbenevento.wms.application.ports.in.ShipStockUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingService implements ShipStockUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    @Override
    @Transactional // Transacción atómica: O salen todos, o no sale ninguno
    public void shipStock(ShipStockCommand command) {

        // 1. Buscar SOLO items que ya estén reservados (Previamente hicimos el Picking)
        List<InventoryItem> reservedItems = inventoryRepository.findReservedStock(command.sku());

        double quantityToShip = command.quantity();

        for (InventoryItem item : reservedItems) {
            if (quantityToShip <= 0) break;

            double currentQty = item.getQuantity();
            double takenQty = Math.min(currentQty, quantityToShip);

            // 2. Lógica de Negocio: Actualizar Estado
            // Si nos llevamos todo el lote del pallet
            if (takenQty >= currentQty) {
                item.setStatus(InventoryStatus.SHIPPED); // Ya no cuenta como activo
                item.setLocationCode("OUT_GATE"); // Ubicación virtual de salida
                // Opcional: item.setQuantity(0.0); si quieres vaciarlo
            } else {
                // Si es un despacho parcial (raro en reservados, pero posible)
                // Aquí deberíamos dividir el item (split), pero para MVP simplificamos:
                // Solo permitimos despachar pallets completos en este ejemplo.
                item.setStatus(InventoryStatus.SHIPPED);
                item.setLocationCode("OUT_GATE");
            }

            inventoryRepository.save(item);
            quantityToShip -= takenQty;
        }

        if (quantityToShip > 0) {
            throw new IllegalStateException("Error: Intentas despachar más de lo que tenías reservado.");
        }
    }
}