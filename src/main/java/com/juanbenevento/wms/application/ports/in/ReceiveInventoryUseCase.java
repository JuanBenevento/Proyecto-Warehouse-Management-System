package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.InventoryItem;

public interface ReceiveInventoryUseCase {
    InventoryItem receiveInventory(ReceiveInventoryCommand command);
}
