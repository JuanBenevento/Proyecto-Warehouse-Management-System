package com.juanbenevento.wms.application.ports.out;

import com.juanbenevento.wms.domain.model.Location;
import java.util.Optional;

public interface LocationRepositoryPort {
    Location save(Location location);
    Optional<Location> findByCode(String code);
}