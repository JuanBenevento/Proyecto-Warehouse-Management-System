package com.juanbenevento.wms.application.ports.in.usecases;

import com.juanbenevento.wms.application.ports.in.command.AllocateStockCommand;

public interface AllocateStockUseCase {
    void allocateStock(AllocateStockCommand command);
}
