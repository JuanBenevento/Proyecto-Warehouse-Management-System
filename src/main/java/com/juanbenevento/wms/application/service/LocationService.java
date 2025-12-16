package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.command.CreateLocationCommand;
import com.juanbenevento.wms.application.ports.in.usecases.ManageLocationUseCase;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService implements ManageLocationUseCase {
    private final LocationRepositoryPort locationRepository;

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location createLocation(CreateLocationCommand command) {
        if (locationRepository.findByCode(command.locationCode()).isPresent()) {
            throw new IllegalArgumentException("La ubicación " + command.locationCode() + " ya existe.");
        }

        Location newLocation = new Location(
                command.locationCode(),
                command.zoneType(),
                command.maxWeight(),
                command.maxVolume(),
                null

        );

        return locationRepository.save(newLocation);
    }

    @Override
    public void deleteLocation(String code) {
        if (locationRepository.findByCode(code).isEmpty()) {
            throw new IllegalArgumentException("La ubicación no existe");
        }

        if (locationRepository.hasInventory(code)) {
            throw new IllegalStateException("No se puede eliminar la ubicación " + code + " porque tiene stock asociado.");
        }

        locationRepository.delete(code);
    }


    @Override
    public Location updateLocation(String code, CreateLocationCommand command) {
        Location location = locationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("La ubicación no existe"));

        if (command.maxWeight() < location.getCurrentWeight()) {
            throw new IllegalArgumentException("No puedes reducir la capacidad por debajo del peso actual.");
        }

        Location updated = new Location(
                code,
                command.zoneType(),
                command.maxWeight(),
                command.maxVolume(),
                location.getCurrentWeight(),
                location.getCurrentVolume(),
                location.getVersion()
        );

        return locationRepository.save(updated);
    }
}