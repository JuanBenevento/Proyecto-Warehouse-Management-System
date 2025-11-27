# 📋 Especificación de Requerimientos Funcionales (WMS)

## 1. Backlog del Producto (Épicas y Historias de Usuario)

Este documento detalla el comportamiento funcional esperado del sistema.

### ÉPICA 1: Gestión de Maestros y Topología (Master Data)
Definición de las reglas físicas y lógicas del almacén.

#### 📝 US-MD-01: Alta de Producto con Perfil Logístico
**Como** Gerente de Inventario,
**Quiero** registrar nuevos productos definiendo sus dimensiones, peso y restricciones de almacenamiento,
**Para** que el sistema pueda calcular automáticamente dónde guardarlos.

* **Criterio de Aceptación 1:** El sistema debe obligar a ingresar Alto, Ancho, Profundidad y Peso.
* **Criterio de Aceptación 2:** Si el peso > 20kg, marcar flag `heavy_load` automáticamente.
* **Criterio de Aceptación 3:** Se debe seleccionar un `StorageCondition` (Seco, Refrigerado, Congelado, Químicos).

#### 📝 US-MD-02: Configuración del Layout del Almacén
**Como** Administrador,
**Quiero** crear ubicaciones jerárquicas (Zona > Pasillo > Rack > Nivel > Posición),
**Para** mapear digitalmente el almacén físico.

* **Criterio de Aceptación 1:** Cada ubicación debe tener un código único (ej: `Z1-P03-R02-N4`).
* **Criterio de Aceptación 2:** Validación de capacidad máxima (Volumen y Peso).

---

### ÉPICA 2: Inbound (Recepción y Entrada)
Proceso de entrada de stock al almacén.

#### 📝 US-IN-01: Recepción Ciega y Generación de LPN
**Como** Operario de Recepción,
**Quiero** ingresar la mercancía generando un código de contenedor único (LPN),
**Para** mover pallets enteros sin manipular cajas individuales.

* **Criterio de Aceptación 1:** Generación automática de ID único LPN (ej: `LPN-2024-00055`).
* **Criterio de Aceptación 2:** Captura obligatoria de Lote (`batch_number`) y Vencimiento.
* **Criterio de Aceptación 3:** Estado inicial del stock: `IN_QUALITY_CHECK` (No disponible para venta).

#### 📝 US-IN-02: Put-Away Dirigido (Estrategia de Ubicación)
**Como** Operario,
**Quiero** que el sistema me indique la ubicación óptima para guardar el pallet,
**Para** optimizar el espacio y respetar la cadena de frío.

* **Criterio de Aceptación 1:** Sugerencia basada en compatibilidad de Zona (Frío con Frío).
* **Criterio de Aceptación 2:** Validación de capacidad física disponible.
* **Criterio de Aceptación 3:** Prioridad a ubicaciones que ya contengan el mismo SKU (consolidación).

---

### ÉPICA 3: Outbound (Pedidos y Salida)
Gestión de pedidos y despacho.

#### 📝 US-OUT-01: Asignación de Stock (Hard Allocation)
**Como** Sistema,
**Quiero** reservar stock automáticamente al confirmar una orden,
**Para** evitar la sobreventa.

* **Criterio de Aceptación 1 (FEFO):** Asignar automáticamente el lote más próximo a vencer.
* **Criterio de Aceptación 2:** Cambio de estado de `AVAILABLE` a `ALLOCATED`.
* **Criterio de Aceptación 3:** Manejo de concurrencia (bloqueo optimista) para evitar asignar el mismo ítem a dos pedidos.

#### 📝 US-OUT-02: Picking con Validación
**Como** Picker,
**Quiero** escanear ubicación y producto antes de confirmar la recolección,
**Para** asegurar la exactitud del pedido.

* **Criterio de Aceptación 1:** Bloqueo si la ubicación escaneada no coincide con la tarea.
* **Criterio de Aceptación 2:** Bloqueo si el SKU escaneado es incorrecto.

---

### ÉPICA 4: Auditoría y Trazabilidad
Seguimiento de movimientos.

#### 📝 US-CORE-01: Kardex de Movimientos
**Como** Auditor,
**Quiero** consultar el historial completo de un LPN,
**Para** trazar quién lo movió y cuándo.

* **Criterio de Aceptación 1:** Registro inmutable (Log) de cada cambio de estado.
* **Criterio de Aceptación 2:** Datos obligatorios: Timestamp, Usuario, Motivo, Ubicación Anterior, Ubicación Nueva.
