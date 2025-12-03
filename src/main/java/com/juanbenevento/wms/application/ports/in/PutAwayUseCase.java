package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.application.ports.in.command.PutAwayInventoryCommand;

public interface PutAwayUseCase {
    void putAwayInventory(PutAwayInventoryCommand command);
}