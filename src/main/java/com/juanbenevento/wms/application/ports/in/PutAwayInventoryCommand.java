package com.juanbenevento.wms.application.ports.in;

public record PutAwayInventoryCommand(
        String lpn,
        String targetLocationCode
) {
    public PutAwayInventoryCommand {
        if (lpn == null || lpn.isBlank()) throw new IllegalArgumentException("LPN requerido");
        if (targetLocationCode == null || targetLocationCode.isBlank()) throw new IllegalArgumentException("Ubicación destino requerida");
    }
}