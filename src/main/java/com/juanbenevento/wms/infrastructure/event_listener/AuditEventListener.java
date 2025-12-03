package com.juanbenevento.wms.infrastructure.event_listener;

import com.juanbenevento.wms.domain.event.InventoryAdjustedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditEventListener {

    @Async
    @EventListener
    public void handleInventoryAdjustment(InventoryAdjustedEvent event) {
        double diff = event.newQuantity() - event.oldQuantity();
        String tipo = diff < 0 ? "PÉRDIDA" : "GANANCIA";

        log.warn("🚨 [AUDITORÍA DE STOCK] Ajuste detectado: {} | LPN: {} | Diferencia: {} | Motivo: {}",
                tipo, event.lpn(), diff, event.reason());

        if (diff < -10) {
            log.error("🔥 ALERTA DE SEGURIDAD: Se ajustaron muchas unidades negativas. Revisar cámaras.");
        }
    }
}