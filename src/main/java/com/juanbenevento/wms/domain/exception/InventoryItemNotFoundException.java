package com.juanbenevento.wms.domain.exception;

/**
 * Excepción lanzada cuando se intenta operar con un item de inventario que no existe.
 */
public class InventoryItemNotFoundException extends DomainException {
    
    private final String lpn;
    
    public InventoryItemNotFoundException(String lpnCode) {
        super(String.format("Item de inventario con LPN '%s' no encontrado", lpnCode));
        this.lpn = lpnCode;
    }
    
    public String getLpn() {
        return lpn;
    }
}

