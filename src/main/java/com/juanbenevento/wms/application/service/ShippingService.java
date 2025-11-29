package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.ShipStockCommand;
import com.juanbenevento.wms.application.ports.in.ShipStockUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingService implements ShipStockUseCase {
    private final InventoryRepositoryPort inventoryRepository;
    private final LocationRepositoryPort locationRepository;
    private final ProductRepositoryPort productRepository;

    @Override
    @Transactional
    public void shipStock(ShipStockCommand command) {
        List<InventoryItem> reservedItems = inventoryRepository.findReservedStock(command.sku());

        double quantityToShip = command.quantity();

        for (InventoryItem item : reservedItems) {
            if (quantityToShip <= 0) break;

            double currentQty = item.getQuantity();
            double takenQty = Math.min(currentQty, quantityToShip);

            Location location = locationRepository.findByCode(item.getLocationCode())
                    .orElseThrow(() -> new IllegalStateException("Error de integridad: La ubicación " + item.getLocationCode() + " no existe"));

            Product product = productRepository.findBySku(item.getProductSku())
                    .orElseThrow(() -> new IllegalStateException("Error de integridad: El producto " + item.getProductSku() + " no existe"));

            Double weightRelease = product.getDimensions().weight() * takenQty;
            Double volumeRelease = product.getStorageVolume() * takenQty;

            location.removeLoad(weightRelease, volumeRelease);
            locationRepository.save(location);

            if (takenQty >= currentQty) {
                item.setStatus(InventoryStatus.SHIPPED);
                item.setLocationCode("OUT_GATE");
            } else {
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