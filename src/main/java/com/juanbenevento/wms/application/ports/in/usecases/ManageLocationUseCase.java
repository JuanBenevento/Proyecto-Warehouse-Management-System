package com.juanbenevento.wms.application.ports.in.usecases;

import com.juanbenevento.wms.application.ports.in.command.CreateLocationCommand;
import com.juanbenevento.wms.domain.model.Location;

import java.util.List;

public interface ManageLocationUseCase {
    List<Location> getAllLocations();
    Location createLocation(CreateLocationCommand command);
    Location updateLocation(String code, CreateLocationCommand command);
    void deleteLocation(String code);
}