package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.PutAwayInventoryCommand;
import com.juanbenevento.wms.application.ports.in.PutAwayUseCase;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryCommand;
import com.juanbenevento.wms.application.ports.in.ReceiveInventoryUseCase;
import com.juanbenevento.wms.domain.model.InventoryItem;
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
    public ResponseEntity<InventoryItem> receiveInventory(@RequestBody ReceiveInventoryRequest request) {
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

    // DTO Entrada JSON
    public record ReceiveInventoryRequest(
            String productSku,
            Double quantity,
            String locationCode,
            String batchNumber,
            LocalDate expiryDate
    ) {}

    @PutMapping("/put-away")
    public ResponseEntity<Void> putAway(@RequestBody PutAwayRequest request) {
        PutAwayInventoryCommand command = new PutAwayInventoryCommand(
                request.lpn(),
                request.targetLocationCode()
        );

        // Como implementamos la interfaz en el servicio, podemos castear o inyectar la interfaz nueva
        // Truco rápido: Si InventoryService implementa ambas interfaces, puedes usar 'receiveInventoryUseCase'
        // casteado si no quieres inyectar otro campo, pero lo ideal es inyectar:
        // private final PutAwayUseCase putAwayUseCase;

        ((PutAwayUseCase) receiveInventoryUseCase).putAwayInventory(command);
        // NOTA: Para hacerlo limpio, agrega 'private final PutAwayUseCase putAwayUseCase;' arriba
        // y Spring inyectará el mismo servicio automáticamente.

        return ResponseEntity.ok().build();
    }

    public record PutAwayRequest(String lpn, String targetLocationCode) {}
}