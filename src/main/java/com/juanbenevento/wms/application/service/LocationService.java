package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.CreateLocationCommand;
import com.juanbenevento.wms.application.ports.in.CreateLocationUseCase;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService implements CreateLocationUseCase {

    private final LocationRepositoryPort locationRepository;

    @Override
    public Location createLocation(CreateLocationCommand command) {
        // 1. Validar si ya existe (Regla de Negocio)
        if (locationRepository.findByCode(command.locationCode()).isPresent()) {
            throw new IllegalArgumentException("La ubicación " + command.locationCode() + " ya existe.");
        }

        // 2. Crear modelo de dominio (inicia vacío de carga)
        Location newLocation = new Location(
                command.locationCode(),
                command.zoneType(),
                command.maxWeight(),
                command.maxVolume()
        );

        // 3. Guardar
        return locationRepository.save(newLocation);
    }
}