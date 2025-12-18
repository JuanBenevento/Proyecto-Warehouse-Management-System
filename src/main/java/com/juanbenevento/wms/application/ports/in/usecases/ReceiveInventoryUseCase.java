package com.juanbenevento.wms.application.ports.in.usecases;

import com.juanbenevento.wms.application.ports.in.command.ReceiveInventoryCommand;
import com.juanbenevento.wms.domain.model.InventoryItem;

public interface ReceiveInventoryUseCase {
    InventoryItem receiveInventory(ReceiveInventoryCommand command);
}
