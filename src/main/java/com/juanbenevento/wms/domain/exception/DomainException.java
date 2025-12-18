package com.juanbenevento.wms.domain.exception;

/**
 * Excepción base para todas las excepciones de dominio.
 * 
 * Diferencia entre excepciones de dominio y excepciones técnicas:
 * - DomainException: Violaciones de reglas de negocio
 * - RuntimeException: Errores técnicos del sistema
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}


