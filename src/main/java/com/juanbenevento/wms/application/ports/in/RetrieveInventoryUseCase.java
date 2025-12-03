package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.InventoryItem;
import java.util.List;

public interface RetrieveInventoryUseCase {
    List<InventoryItem> getAllInventory();
}