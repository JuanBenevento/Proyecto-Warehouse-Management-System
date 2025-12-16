# Warehouse Management System (WMS)

## 📌 Descripción General

Este proyecto es un **Warehouse Management System (WMS)** desarrollado como proyecto personal con el objetivo de **demostrar competencias técnicas reales en ingeniería de software**, particularmente en el diseño y construcción de sistemas empresariales backend y full stack.

El sistema modela operaciones centrales de un almacén:

* Gestión de productos
* Control de inventario
* Ubicaciones físicas con restricciones de capacidad
* Movimientos de stock (recepción, reserva, despacho)
* Auditoría mediante eventos de dominio

El foco principal del proyecto está puesto en:

* **Arquitectura limpia (Hexagonal / Clean Architecture)**
* **Reglas de negocio explícitas**
* **Separación de responsabilidades**
* **Escalabilidad y mantenibilidad**

---

## 🧠 Motivación del Proyecto

La mayoría de los proyectos junior se limitan a CRUD simples. Este WMS fue diseñado intencionalmente para:

* Simular un **sistema real de la industria**
* Aplicar principios de arquitectura utilizados en equipos profesionales
* Practicar modelado de dominio y reglas de negocio
* Servir como **prueba técnica viva** para procesos de selección laboral

---

## 🏗️ Arquitectura

El backend implementa **Arquitectura Hexagonal (Ports & Adapters)**, con una separación clara entre:

```
com.juanbenevento.wms
├── domain            # Núcleo del negocio (entidades, reglas, eventos)
├── application       # Casos de uso y puertos
└── infrastructure    # Adaptadores (REST, persistencia, seguridad)
```

### Capas

* **Domain**

  * Entidades ricas en comportamiento
  * Validaciones de negocio
  * Excepciones de dominio
  * Eventos de dominio

* **Application**

  * Casos de uso explícitos
  * Commands como DTOs (Java Records)
  * Interfaces (ports) desacopladas de la infraestructura

* **Infrastructure**

  * Controladores REST
  * Persistencia JPA
  * Seguridad (JWT)
  * Configuración técnica

Esta estructura permite:

* Reemplazar frameworks sin afectar el negocio
* Testear reglas de negocio de forma aislada
* Escalar el sistema sin degradar la mantenibilidad

---

## 🧩 Modelado de Dominio

El dominio no es anémico. Algunas reglas implementadas:

* Un producto **no puede modificar sus dimensiones** si existe stock físico
* Una ubicación **no puede exceder su capacidad** (peso / volumen)
* Productos pesados requieren maquinaria especial
* El inventario genera **eventos de dominio** ante cambios relevantes

Ejemplos de conceptos modelados:

* `Product`
* `Dimensions`
* `InventoryItem`
* `Location`
* Eventos como `StockReceivedEvent`, `InventoryAdjustedEvent`

---

## ⚙️ Stack Tecnológico

### Backend

* **Java 21**
* **Spring Boot 3**
* Spring Data JPA
* Spring Security + JWT
* PostgreSQL
* SpringDoc OpenAPI (Swagger)

### Frontend

* **Angular 20**
* Arquitectura modular por features

### DevOps / Tooling

* Maven Wrapper
* GitHub Actions (CI)
* PostgreSQL como servicio en CI

---

## 🧪 Testing

* Tests unitarios enfocados en el **dominio y reglas de negocio**
* Validaciones de invariantes críticas
* Context load con seguridad simulada

> El objetivo del testing no es la cobertura numérica, sino la **confianza en las reglas del negocio**.

---

## 🔐 Seguridad

* Autenticación basada en JWT
* Integración con Spring Security
* Separación clara entre seguridad y lógica de negocio

---

## 🚀 CI/CD

El proyecto cuenta con integración continua mediante **GitHub Actions**:

* Build automático en ramas `dev` y `main`
* Ejecución de tests
* Base de datos PostgreSQL levantada como servicio

Esto garantiza que el proyecto sea **ejecutable y verificable en cualquier entorno**.

---

## 🖥️ Frontend

El frontend está organizado por dominios funcionales:

* Inventory
* Warehouse
* Admin
* Authentication

Se priorizó la escalabilidad estructural sobre el diseño visual, dado que el foco del proyecto es **arquitectónico y de negocio**.

---

## 📈 Próximos Pasos / Roadmap

Algunas mejoras planificadas:

* Tests de integración con Testcontainers
* Diagramas C4 (Context / Container)
* Auditoría avanzada (createdBy / timestamps)
* Manejo unificado de errores (Problem Details)
* Despliegue en entorno cloud

---

## 👤 Autor

**Juan Manuel Benevento**
Técnico Universitario en Programación (UTN Mar del Plata)

Proyecto desarrollado con fines educativos y profesionales, orientado a demostrar capacidad real de inserción laboral en equipos de desarrollo de software.

---

## 📄 Licencia

Este proyecto se publica con fines demostrativos y educativos.
