package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.application.ports.out.LocationRepositoryPort;
import com.juanbenevento.wms.domain.model.Location;
import com.juanbenevento.wms.domain.model.ZoneType;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.LocationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LocationPersistenceAdapter implements LocationRepositoryPort {
    private final SpringDataLocationRepository jpaRepository;
    private final SpringDataInventoryRepository inventoryRepository;

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

    @Override
    public void delete(String locationCode) {
        jpaRepository.deleteById(locationCode);
    }

    @Override
    public boolean hasInventory(String locationCode) {
        return !inventoryRepository.findByLocationCode(locationCode).isEmpty();
    }

    @Override
    public List<Location> findAvailableLocations(ZoneType zone, Double weightNeeded, Double volumeNeeded) {
        return jpaRepository.findCandidates(zone, weightNeeded, volumeNeeded)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // --- MAPPERS ---

    private LocationEntity toEntity(Location domain) {
        return LocationEntity.builder()
                .locationCode(domain.getLocationCode())
                .zoneType(domain.getZoneType())
                .maxWeight(domain.getMaxWeight())
                .maxVolume(domain.getMaxVolume())
                .currentWeight(domain.getCurrentWeight())
                .currentVolume(domain.getCurrentVolume())
                .version(domain.getVersion()) // <--- Mapear Versión
                .build();
    }

    private Location toDomain(LocationEntity entity) {
        return new Location(
                entity.getLocationCode(),
                entity.getZoneType(),
                entity.getMaxWeight(),
                entity.getMaxVolume(),
                entity.getCurrentWeight(),
                entity.getCurrentVolume(),
                entity.getVersion() // <--- Recuperar Versión
        );
    }
}