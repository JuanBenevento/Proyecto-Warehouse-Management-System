package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.domain.model.ZoneType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Data // Crea Getters, Setters, ToString, Equals, HashCode
@NoArgsConstructor // Obligatorio para Hibernate
@AllArgsConstructor
@Builder // Nos permite crear objetos de forma fluida
public class LocationEntity {

    @Id
    @Column(name = "location_code")
    private String locationCode;

    @Enumerated(EnumType.STRING) // Guarda el texto "COLD_STORAGE" en la DB, no un número
    private ZoneType zoneType;

    private Double maxWeight;
    private Double maxVolume;

    private Double currentWeight;
    private Double currentVolume;
}