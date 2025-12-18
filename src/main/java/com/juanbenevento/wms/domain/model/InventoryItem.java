package com.juanbenevento.wms.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class InventoryItem {

    private final String lpn;
    private final String productSku;
    private Double quantity;
    private final String batchNumber;
    private final LocalDate expiryDate;
    private InventoryStatus status;
    private String locationCode;
    private Long version;


    public void moveTo(String newLocationCode) {
        this.locationCode = newLocationCode;
    }

    public void approveQualityCheck() {
        if (this.status == InventoryStatus.IN_QUALITY_CHECK) {
            this.status = InventoryStatus.AVAILABLE;
        }
    }
}