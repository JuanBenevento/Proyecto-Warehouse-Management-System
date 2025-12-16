package com.juanbenevento.wms.infrastructure.adapter.in.rest.controllers;

import com.juanbenevento.wms.application.ports.in.command.AllocateStockCommand;
import com.juanbenevento.wms.application.ports.in.usecases.AllocateStockUseCase;
import com.juanbenevento.wms.application.ports.in.command.ShipStockCommand;
import com.juanbenevento.wms.application.ports.in.usecases.ShipStockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/picking")
@RequiredArgsConstructor
@Tag(name = "4. Salidas y Despacho (Outbound)", description = "Gestión de pedidos, reserva de stock y liberación de espacio.")
public class PickingController {
    private final AllocateStockUseCase allocateStockUseCase;
    private final ShipStockUseCase shipStockUseCase;

    @Operation(summary = "1. Reservar Stock (Allocate)", description = "Busca el stock más antiguo (FEFO) y lo marca como RESERVED.")
    @PostMapping("/allocate")
    public ResponseEntity<String> allocateOrder(@RequestBody AllocateRequest request) {
        allocateStockUseCase.allocateStock(new AllocateStockCommand(
                request.sku(),
                request.quantity()
        ));
        return ResponseEntity.ok("Stock reservado exitosamente. Listo para picking.");
    }

    @Operation(summary = "2. Confirmar Despacho (Ship)", description = "Da de baja el stock reservado y libera el peso de la ubicación.")
    @PostMapping("/ship")
    public ResponseEntity<String> shipOrder(@RequestBody ShipRequest request) {
        shipStockUseCase.shipStock(new ShipStockCommand(
                request.sku(),
                request.quantity()
        ));
        return ResponseEntity.ok("Pedido despachado correctamente.");
    }

    public record AllocateRequest(
            @Schema(example = "TV-LG-65") String sku, @Schema(example = "5.0") Double quantity) {}
    public record ShipRequest(
            @Schema(example = "TV-LG-65")
            String sku,
            @Schema(example = "5.0") Double quantity
    ) {}
}