package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.CreateLocationCommand;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.application.service.LocationService;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.ZoneType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationRepositoryPort locationRepository;
    private final LocationService locationService;

    @GetMapping("/getAllLocations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }

    @PostMapping("/createLocation")
    public ResponseEntity<Location> createLocation(@RequestBody CreateLocationRequest request) {
        CreateLocationCommand command = new CreateLocationCommand(
                request.locationCode(),
                request.zoneType(),
                request.maxWeight(),
                request.maxVolume()
        );

        Location created = locationService.createLocation(command);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable String code) {
        // Asumiendo que agregaste el método al servicio (o a la interfaz del caso de uso)
        // Puedes castear o inyectar LocationService directamente para este MVP
        locationService.deleteLocation(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Location> updateLocation(@PathVariable String code, @RequestBody CreateLocationRequest request) {

        CreateLocationCommand command = new CreateLocationCommand(
                code, request.zoneType(), request.maxWeight(), request.maxVolume()
        );

        Location updated = locationService.updateLocation(code, command);
        return ResponseEntity.ok(updated);
    }

    // DTO para el JSON de entrada
    public record CreateLocationRequest(
            String locationCode,
            ZoneType zoneType,
            Double maxWeight,
            Double maxVolume
    ) {}
}