package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.CreateLocationCommand;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.service.LocationService;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.ZoneType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "2. Topología y Ubicaciones", description = "Gestión del mapa físico (Racks, Pasillos, Zonas).")
public class LocationController {
    private final LocationRepositoryPort locationRepository;
    private final LocationService locationService;

    @Operation(summary = "Ver mapa del depósito", description = "Lista todas las ubicaciones y su estado de ocupación.")
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }

    @Operation(summary = "Crear ubicación")
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody CreateLocationRequest request) {
        CreateLocationCommand command = new CreateLocationCommand(
                request.locationCode(), request.zoneType(), request.maxWeight(), request.maxVolume()
        );
        return new ResponseEntity<>(locationService.createLocation(command), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar ubicación", description = "Solo permitido si la ubicación está 100% vacía.")
    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable String code) {
        locationService.deleteLocation(code);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Modificar ubicación")
    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Location> updateLocation(@PathVariable String code, @RequestBody CreateLocationRequest request) {
        CreateLocationCommand command = new CreateLocationCommand(
                code, request.zoneType(), request.maxWeight(), request.maxVolume()
        );
        return ResponseEntity.ok(locationService.updateLocation(code, command));
    }

    public record CreateLocationRequest(
            @Schema(example = "A-01-01-1", description = "Código de Pasillo-Rack-Nivel")
            String locationCode,
            @Schema(example = "DRY_STORAGE", description = "Zona (DRY, COLD, FROZEN, HAZMAT)")
            ZoneType zoneType,
            @Schema(example = "1000.0")
            Double maxWeight,
            @Schema(example = "2000000.0")
            Double maxVolume
    ) {}
}