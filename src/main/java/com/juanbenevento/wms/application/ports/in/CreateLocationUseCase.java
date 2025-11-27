package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.Location;

public interface CreateLocationUseCase {
    Location createLocation(CreateLocationCommand command);
}