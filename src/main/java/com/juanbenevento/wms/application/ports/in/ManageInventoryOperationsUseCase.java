package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.application.ports.in.command.InternalMoveCommand;
import com.juanbenevento.wms.application.ports.in.command.InventoryAdjustmentCommand;

public interface ManageInventoryOperationsUseCase {
    void processInternalMove(InternalMoveCommand command);
    void processInventoryAdjustment(InventoryAdjustmentCommand command);
}