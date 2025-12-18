package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.domain.model.ZoneType;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataLocationRepository extends JpaRepository<LocationEntity, String> {

    Optional<LocationEntity> findByLocationCode(String locationCode);

    @Query("""
        SELECT l FROM LocationEntity l
        WHERE l.zoneType = :zone
        AND (l.maxWeight - l.currentWeight) >= :requiredWeight
        AND (l.maxVolume - l.currentVolume) >= :requiredVolume
        ORDER BY l.locationCode ASC
    """)
    List<LocationEntity> findCandidates(
            @Param("zone") ZoneType zone,
            @Param("requiredWeight") Double requiredWeight,
            @Param("requiredVolume") Double requiredVolume
    );
}