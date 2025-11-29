package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor // Lombok nos crea el constructor automáticamente
public class LocationPersistenceAdapter implements LocationRepositoryPort {

    private final SpringDataLocationRepository jpaRepository;

    @Override
    public Location save(Location location) {
        LocationEntity entity = toEntity(location);
        LocationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Location> findByCode(String code) {
        return jpaRepository.findByLocationCode(code)
                .map(this::toDomain);
    }

    @Override
    public List<Location> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // --- MAPPERS (Traductores) ---

    private LocationEntity toEntity(Location domain) {
        return LocationEntity.builder()
                .locationCode(domain.getLocationCode())
                .zoneType(domain.getZoneType())
                .maxWeight(domain.getMaxWeight())
                .maxVolume(domain.getMaxVolume())
                .currentWeight(domain.getCurrentWeight()) // Persistimos el estado actual
                .currentVolume(domain.getCurrentVolume())
                .build();
    }

    private Location toDomain(LocationEntity entity) {
        // Reconstruimos el objeto de dominio con su estado
        return new Location(
                entity.getLocationCode(),
                entity.getZoneType(),
                entity.getMaxWeight(),
                entity.getMaxVolume(),
                entity.getCurrentWeight(),
                entity.getCurrentVolume()
        );
    }
}