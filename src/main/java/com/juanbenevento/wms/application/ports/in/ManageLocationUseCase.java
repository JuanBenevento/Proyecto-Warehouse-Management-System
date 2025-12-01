package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.Location;

public interface ManageLocationUseCase {
    Location createLocation(CreateLocationCommand command);
}