package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.CreateLocationCommand;
import com.juanbenevento.wms.application.ports.in.CreateLocationUseCase;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.ZoneType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {
    private final CreateLocationUseCase createLocationUseCase;
    private final LocationRepositoryPort locationRepository;

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

        Location created = createLocationUseCase.createLocation(command);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // DTO para el JSON de entrada
    public record CreateLocationRequest(
            String locationCode,
            ZoneType zoneType,
            Double maxWeight,
            Double maxVolume
    ) {}
}