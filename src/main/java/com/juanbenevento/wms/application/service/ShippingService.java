package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.ShipStockCommand;
import com.juanbenevento.wms.application.ports.in.usecases.ShipStockUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.event.StockShippedEvent;
import com.juanbenevento.wms.domain.model.InventoryItem;
import com.juanbenevento.wms.domain.model.InventoryStatus;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingService implements ShipStockUseCase {
    private final InventoryRepositoryPort inventoryRepository;
    private final LocationRepositoryPort locationRepository;
    private final ProductRepositoryPort productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void shipStock(ShipStockCommand command) {
        List<InventoryItem> reservedItems = inventoryRepository.findReservedStock(command.sku());

        double totalReserved = reservedItems.stream().mapToDouble(InventoryItem::getQuantity).sum();
        if (totalReserved < command.quantity()) {
            throw new IllegalArgumentException("No hay suficiente stock reservado. Reservado: " + totalReserved + ", Solicitado: " + command.quantity());
        }

        double quantityToShip = command.quantity();

        for (InventoryItem item : reservedItems) {
            if (quantityToShip <= 0) break;

            double currentQty = item.getQuantity();
            double takenQty = Math.min(currentQty, quantityToShip);

            Location location = locationRepository.findByCode(item.getLocationCode()).orElseThrow();
            Product product = productRepository.findBySku(item.getProductSku()).orElseThrow();

            Double weightRelease = product.getDimensions().weight() * takenQty;
            Double volumeRelease = product.getStorageVolume() * takenQty;

            location.removeLoad(weightRelease, volumeRelease);
            locationRepository.save(location);

            if (takenQty >= currentQty) {
                // Se lleva todo el pallet -> SHIPPED
                item.setStatus(InventoryStatus.SHIPPED);
                item.setLocationCode("OUT_GATE");
            } else {
                // Parcial -> Solo restamos cantidad
                item.setQuantity(currentQty - takenQty);
                // El estado sigue siendo RESERVED para el remanente?
                // O vuelve a AVAILABLE? Depende de tu negocio.
                // Normalmente si sobra en el pallet, lo que sobra queda disponible o sigue reservado si es para otra orden.
                // Asumiremos que queda RESERVED si no se liberó explícitamente, o AVAILABLE.
                // Para simplificar: Lo dejamos en AVAILABLE porque ya "cumplimos" la parte del pedido.
                item.setStatus(InventoryStatus.AVAILABLE);
            }
            inventoryRepository.save(item);

            eventPublisher.publishEvent(new StockShippedEvent(
                    item.getProductSku(),
                    takenQty,
                    location.getLocationCode(),
                    getCurrentUser(),
                    LocalDateTime.now()
            ));

            quantityToShip -= takenQty;
        }
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }
}