package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.PutAwayUseCase;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryUseCase;
import com.juanbenevento.wms.application.ports.out.InventoryRepositoryPort;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.ports.out.ProductRepositoryPort;
import com.juanbenevento.wms.domain.model.InventoryItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "3. Operaciones de Inventario (Inbound)", description = "Recepción, control de calidad y movimientos.")
public class InventoryController {

    private final ReceiveInventoryUseCase receiveInventoryUseCase;
    private final PutAwayUseCase putAwayUseCase;
    private final ProductRepositoryPort productRepo;
    private final LocationRepositoryPort locationRepo;
    private final com.juanbenevento.wms.domain.service.PutAwayStrategy strategy;
    private final InventoryRepositoryPort inventoryRepo;

    @Operation(summary = "Ver stock real", description = "Lista todos los items con su LPN y estado.")
    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        return ResponseEntity.ok(inventoryRepo.findAll());
    }

    @Operation(summary = "Recepción de Mercadería", description = "Genera LPN y valida capacidad física de la ubicación.")
    @PostMapping("/receive")
    public ResponseEntity<InventoryItem> receiveInventory(@RequestBody @Valid ReceiveInventoryRequest request) {
        ReceiveInventoryCommand command = new ReceiveInventoryCommand(
                request.productSku(), request.quantity(), request.locationCode(),
                request.batchNumber(), request.expiryDate()
        );
        return new ResponseEntity<>(receiveInventoryUseCase.receiveInventory(command), HttpStatus.CREATED);
    }

    @Operation(summary = "Confirmar Ubicación (Put-Away)", description = "Cambia estado de IN_QUALITY_CHECK a AVAILABLE.")
    @PutMapping("/put-away")
    public ResponseEntity<Void> putAway(@RequestBody PutAwayRequest request) {
        PutAwayInventoryCommand command = new PutAwayInventoryCommand(
                request.lpn(), request.targetLocationCode()
        );
        putAwayUseCase.putAwayInventory(command);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Consultar Estrategia", description = "El sistema sugiere dónde guardar según el perfil del producto.")
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
            @Schema(example = "TV-LG-65") @NotBlank
            String productSku,
            @Schema(example = "10.0") @NotNull @Min(1)
            Double quantity,
            @Schema(example = "A-01-01-1") @NotBlank
            String locationCode,
            @Schema(example = "LOTE-2025") @NotBlank
            String batchNumber,
            @Schema(example = "2030-12-31") @NotNull @Future
            LocalDate expiryDate
    ) {}

    public record PutAwayRequest(
            @Schema(example = "LPN-173...")
            String lpn,
            @Schema(example = "A-01-01-1")
            String targetLocationCode
    ) {}
}