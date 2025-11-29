package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.PutAwayUseCase;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryUseCase;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.InventoryItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ReceiveInventoryUseCase receiveInventoryUseCase;
    private final PutAwayUseCase putAwayUseCase;
    private final ProductRepositoryPort productRepo;
    private final LocationRepositoryPort locationRepo;
    private final com.juanbenevento.wms.domain.service.PutAwayStrategy strategy;

    @PostMapping("/receive")
    public ResponseEntity<InventoryItem> receiveInventory(@RequestBody @Valid ReceiveInventoryRequest request) {
        ReceiveInventoryCommand command = new ReceiveInventoryCommand(
                request.productSku(),
                request.quantity(),
                request.locationCode(),
                request.batchNumber(),
                request.expiryDate()
        );

        InventoryItem created = receiveInventoryUseCase.receiveInventory(command);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/put-away")
    public ResponseEntity<Void> putAway(@RequestBody PutAwayRequest request) {
        PutAwayInventoryCommand command = new PutAwayInventoryCommand(
                request.lpn(),
                request.targetLocationCode()
        );

        putAwayUseCase.putAwayInventory(command);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/suggest-location")
    public ResponseEntity<String> suggestLocation(@RequestParam String sku, @RequestParam Double quantity) {
        var product = productRepo.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Producto no existe"));

        var allLocations = locationRepo.findAll();
        var suggestion = strategy.findBestLocation(product, quantity, allLocations);

        return suggestion
                .map(loc -> ResponseEntity.ok(loc.getLocationCode()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay espacio disponible"));
    }

    public record ReceiveInventoryRequest(
            @NotBlank(message = "El SKU es obligatorio") String productSku,
            @NotNull(message = "La cantidad es obligatoria") @Min(1) Double quantity,
            @NotBlank(message = "La ubicación es obligatoria") String locationCode,
            @NotBlank(message = "El lote es obligatorio") String batchNumber,
            @NotNull(message = "La fecha de vencimiento es obligatoria") @Future LocalDate expiryDate
    ) {}

    public record PutAwayRequest(String lpn, String targetLocationCode) {}
}