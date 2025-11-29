package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.PutAwayUseCase;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryUseCase;
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

        ((PutAwayUseCase) receiveInventoryUseCase).putAwayInventory(command);

        return ResponseEntity.ok().build();
    }

    // DTO Entrada JSON
    public record ReceiveInventoryRequest(
            @NotBlank(message = "El SKU es obligatorio")
            String productSku,

            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad mínima es 1")
            Double quantity,

            @NotBlank(message = "La ubicación es obligatoria")
            String locationCode,

            @NotBlank(message = "El lote es obligatorio")
            String batchNumber,

            @NotNull(message = "La fecha de vencimiento es obligatoria")
            @Future(message = "La fecha de vencimiento debe ser futura") // ¡Regla de negocio gratis!
            LocalDate expiryDate
    ) {}

    public record PutAwayRequest(String lpn, String targetLocationCode) {}
}