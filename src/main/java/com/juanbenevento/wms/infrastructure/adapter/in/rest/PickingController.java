package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.AllocateStockCommand;
import com.juanbenevento.wms.application.ports.in.AllocateStockUseCase;
import com.juanbenevento.wms.application.ports.in.ShipStockCommand;
import com.juanbenevento.wms.application.ports.in.ShipStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/picking")
@RequiredArgsConstructor
public class PickingController {
    private final AllocateStockUseCase allocateStockUseCase;
    private final ShipStockUseCase shipStockUseCase;

    @PostMapping("/allocate")
    public ResponseEntity<String> allocateOrder(@RequestBody AllocateRequest request) {
        allocateStockUseCase.allocateStock(new AllocateStockCommand(
                request.sku(),
                request.quantity()
        ));
        return ResponseEntity.ok("Stock reservado exitosamente. Listo para picking.");
    }

    @PostMapping("/ship")
    public ResponseEntity<String> shipOrder(@RequestBody ShipRequest request) {
        shipStockUseCase.shipStock(new ShipStockCommand(
                request.sku(),
                request.quantity()
        ));
        return ResponseEntity.ok("Pedido despachado correctamente.");
    }

    public record AllocateRequest(String sku, Double quantity) {}
    public record ShipRequest(String sku, Double quantity) {}
}