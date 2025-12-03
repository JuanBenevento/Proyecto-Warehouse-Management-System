package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.application.ports.in.command.ShipStockCommand;

public interface ShipStockUseCase {
    void shipStock(ShipStockCommand command);
}
