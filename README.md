# 🏭 WMS Enterprise - Sistema de Gestión de Almacenes

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?logo=spring-boot)
![Angular](https://img.shields.io/badge/Angular-17-red?logo=angular)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-purple)

> **Plataforma integral de gestión logística.** Diseñada bajo principios de **Arquitectura Hexagonal** y **DDD (Domain-Driven Design)** para garantizar un núcleo de negocio desacoplado, seguro y escalable. Simula las operaciones críticas de un Centro de Distribución moderno.

---

## 🧠 Arquitectura del Sistema

El proyecto se aleja del clásico MVC acoplado para implementar una **Arquitectura de Puertos y Adaptadores (Hexagonal)**.

* **Domain Layer (Núcleo):** Entidades puras (`Location`, `Product`) con lógica de negocio rica (validaciones físicas de peso/volumen). Sin dependencias de Frameworks.
* **Application Layer (Orquestación):** Casos de uso (`ReceiveInventory`, `PickStock`) que coordinan el flujo de datos mediante interfaces (Puertos).
* **Infrastructure Layer (Adaptadores):** Implementaciones técnicas (REST Controllers, JPA Repositories, JWT Security) que se conectan al núcleo.

### Diagrama Conceptual
*(Tu código sigue este flujo estricto)*
`Request WEB` ➔ `Controller (Adapter)` ➔ `UseCase (Port)` ➔ `Service (Application)` ➔ `Repository (Port)` ➔ `JPA (Adapter)` ➔ `DB`

---

## ✨ Funcionalidades Principales

### 📦 1. Inbound (Recepción Inteligente)
* **Generación de LPN:** Creación automática de *License Plate Numbers* para trazabilidad única de pallets.
* **Validación Física:** El sistema impide recibir mercadería si excede la capacidad volumétrica o de peso de la ubicación destino.
* **Control de Calidad:** Estado inicial `IN_QUALITY_CHECK` bloqueado para la venta hasta su aprobación.

### 🧠 2. Estrategias de Ubicación (Put-Away)
* Implementación del **Patrón Strategy** para sugerir ubicaciones.
* Algoritmo que evalúa: Zona (Frío/Seco), Compatibilidad de Producto y Espacio Disponible.

### 🚚 3. Outbound (Despacho)
* **Reserva Transaccional:** Bloqueo de stock (`RESERVED`) para evitar sobreventa.
* **Gestión de Estados:** Ciclo de vida completo: `AVAILABLE` ➔ `RESERVED` ➔ `SHIPPED`.
* **Liberación de Espacio:** Actualización automática de la capacidad de la estantería al despachar.

### 🔐 4. Seguridad & Gestión de Identidad
* **Autenticación Stateless:** Implementación manual de JWT (JSON Web Tokens).
* **RBAC (Role-Based Access Control):** Sistema de permisos granulares (`ADMIN` vs `OPERATOR`).
* **Auditoría:** Trazabilidad de creación y modificación de registros (quién y cuándo).

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología | Detalles |
| :--- | :--- | :--- |
| **Backend** | Java 21 | Records, Stream API, Optional |
| **Framework** | Spring Boot 3.4 | Spring Data JPA, Spring Security 6 |
| **Frontend** | Angular 17+ | Standalone Components, Signals, Interceptors |
| **Base de Datos** | PostgreSQL 16 | Relacional, integridad referencial |
| **UI/UX** | Bootstrap 5 | Diseño responsivo, Modales (SweetAlert2) |
| **Doc** | OpenAPI / Swagger | Documentación viva de la API |

---

## 🚀 Instalación y Ejecución

### Prerrequisitos
* Java 21 JDK
* Node.js (v18+)
* PostgreSQL

### 1. Base de Datos
Crea una base de datos vacía llamada `wms_db` en tu servidor PostgreSQL local.

### 2. Backend
```bash
git clone [https://github.com/JuanBenevento/Proyecto-Warehouse-Management-System.git](https://github.com/JuanBenevento/Proyecto-Warehouse-Management-System.git)
cd Proyecto-Warehouse-Management-System
# Configura tu usuario/pass en src/main/resources/application.properties
./mvnw spring-boot:run
```
3. Frontend 
cd wms-frontend
npm install
ng serve -o
Credenciales por defecto (al iniciar):

El sistema permite registrar el primer usuario vía API o insertarlo en DB.

Roles disponibles: ADMIN, OPERATOR.

🔮 Roadmap (Próximos Pasos)
El proyecto se encuentra en evolución constante. Las próximas mejoras planificadas son:

[ ] Migración de DB: Implementación de Flyway para versionado de esquemas.

[ ] Dockerización: Creación de docker-compose para despliegue en un click.

[ ] Observabilidad: Implementación de Logs estructurados y monitoreo (Actuator).

[ ] Multi-tenancy: Soporte para múltiples clientes en la misma instancia.

🤝 Contacto
Desarrollado por Juan Manuel Benevento Full Stack Developer
