# Diagrama de Componentes — VitalLogix CRUD

**Tipo:** Diagrama de Componentes (UML 2.5)  
**Archivos generados:** [01_diagrama_componentes.png](uml/01_diagrama_componentes.png) | [01_diagrama_componentes.svg](uml/01_diagrama_componentes.svg)

## Descripción

Representa la descomposición modular de VitalLogix en tres capas:

1. **Capa Presentación (Frontend React 19 + Vite + Tailwind):**
   - `UI Gestión CRUD` (`ProductCrudTable.jsx`, `App.jsx`)
   - `Cliente HTTP / Sesiones` (`services/api.js`, `RootApp.jsx`, `AuthModal.jsx`)
   - `Módulos Farmacia Extra` (Ventas, Clientes, Categorías, Campañas, Facturación PDF)
2. **Capa Aplicación y Negocio (Spring Boot 4.0.5 / Java 21):**
   - `API REST Productos` (`ProductController` — interfaz `IRestProducts`)
   - `Seguridad & Auth JWT` (`AuthController`, `SecurityConfig`, `JwtService` — interfaz `IAuthJWT`)
   - `Servicio CRUD Productos` (`ProductService` implementando `ProductServicePort` bajo DIP)
   - `Manejo Global Excepciones` (`GlobalExceptionHandler`, `ApiError`, excepciones de dominio)
   - `Persistencia JPA` (`ProductRepository`, `UserRepository`, `SaleRepository`, `CategoryRepository`)
   - `Bootstrap & Ventas` (`DataInitializer`, `SaleService`, `SaleEventNotifier`, `LoyaltyObserver`)
3. **Capa Datos y Despliegue:**
   - `MySQL 8.0 Server` (esquema `vitallogix`, puerto `3306`, conector `mysql-connector-j`)
   - `Contenedores Docker` (`docker-compose.yml`, `Dockerfile`, `.env.example`, volumen persistente)
