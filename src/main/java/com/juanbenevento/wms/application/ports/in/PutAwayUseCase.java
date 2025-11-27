package com.juanbenevento.wms.application.ports.in;

public interface PutAwayUseCase {
    void putAwayInventory(PutAwayInventoryCommand command);
}