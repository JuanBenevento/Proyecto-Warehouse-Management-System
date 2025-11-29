package com.juanbenevento.wms.infrastructure.adapter.out.persistence;

import com.juanbenevento.wms.domain.model.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InventoryItemEntity extends AuditableEntity{
    @Id
    @Column(name = "lpn")
    private String lpn; // La etiqueta es la Primary Key

    @Column(nullable = false)
    private String productSku;

    private Double quantity;

    private String batchNumber;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @Column(nullable = false)
    private String locationCode;

}