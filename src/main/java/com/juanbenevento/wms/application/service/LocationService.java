package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.CreateLocationCommand;
import com.juanbenevento.wms.application.ports.in.ManageLocationUseCase;
import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService implements ManageLocationUseCase {
    private final LocationRepositoryPort locationRepository;

    @Override
    public Location createLocation(CreateLocationCommand command) {
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

    public void deleteLocation(String code) {
        // 1. Validar que exista
        if (locationRepository.findByCode(code).isEmpty()) {
            throw new IllegalArgumentException("La ubicación no existe");
        }

        // 2. REGLA DE ORO: No borrar si tiene carga
        if (locationRepository.hasInventory(code)) {
            throw new IllegalStateException("No se puede eliminar la ubicación " + code + " porque tiene stock asociado.");
        }

        locationRepository.delete(code);
    }

    // Método para MODIFICAR (Solo permitimos cambiar tipo y capacidad, no el código)
    public Location updateLocation(String code, CreateLocationCommand command) {
        Location location = locationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("La ubicación no existe"));

        // Validar que la nueva capacidad no sea menor a la carga actual
        if (command.maxWeight() < location.getCurrentWeight()) {
            throw new IllegalArgumentException("No puedes reducir la capacidad por debajo del peso actual.");
        }

        // Actualizamos los campos permitidos (usando un nuevo objeto o setters si los tuvieras)
        // Como Location es inmutable en tu diseño actual (record o final fields), creamos una nueva instancia con los datos viejos + nuevos
        Location updated = new Location(
                code, // El código no cambia
                command.zoneType(),
                command.maxWeight(),
                command.maxVolume(),
                location.getCurrentWeight(), // Mantenemos la carga actual
                location.getCurrentVolume()
        );

        return locationRepository.save(updated);
    }
}