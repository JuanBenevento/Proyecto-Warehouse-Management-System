package com.juanbenevento.wms.application.ports.in.usecases;

import com.juanbenevento.wms.application.ports.in.command.PutAwayInventoryCommand;

public interface PutAwayUseCase {
    void putAwayInventory(PutAwayInventoryCommand command);
}