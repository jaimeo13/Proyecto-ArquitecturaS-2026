# Entrega: CRUD en un Framework — VitalLogix (Spring Boot + MySQL 8.0 + React)

Esta carpeta contiene todos los entregables de la actividad **CRUD en un framework** basados en la arquitectura de **VitalLogix**.

## Contenido de la Entrega

| Elemento | Ruta | Descripción |
| :--- | :--- | :--- |
| **Documento PDF** | [`Documentacion_VitalLogix_CRUD.pdf`](Documentacion_VitalLogix_CRUD.pdf) | Documentación completa: Funcionalidades, Componentes (con interfaces de entrada/salida, dependencias, artefactos y diagrama UML), Clases (con atributos, funciones, servicios y diagrama UML), Secuencias (6 diagramas UML) y Caso de Uso con enlace al video. |
| **Documentación Javadoc** | [`javadoc/index.html`](javadoc/index.html) | Documentación HTML generada a partir de los comentarios Javadoc en español del código fuente Java. |
| **Diagramas UML** | [`diagramas/`](diagramas/) | Diagramas UML en alta resolución (PNG y SVG) de Componentes, Clases y 6 Secuencias, además de copias de los modelos Astah (`.asta`). |
| **Código Fuente** | [`codigo-fuente/`](codigo-fuente/) | Backend Spring Boot 4 (Java 21 + MySQL 8.0 + Javadoc), Frontend React 19 + Vite + Tailwind (`ProductCrudTable.jsx`) y despliegue Docker (`docker-compose.yml`, `Dockerfile`, `.env.example`). |

## Funcionalidades Implementadas

- **Inicio directo con tabla MySQL:** Muestra al abrir la app los registros de la tabla `products` (`id`, `code`, `name`, `category`, `price`, `stock`, `expiration_date`), con 12 productos sembrados automáticamente en el primer arranque.
- **Selección múltiple y eliminación masiva:** Casilla de verificación por fila + casilla maestra + botón superior rojo **Eliminar (N)** (`DELETE /api/products/batch`).
- **CRUD completo:** Botón superior verde **+ Nuevo Producto** y acciones por fila (**Ver detalle**, **Editar**, **Eliminar**).
- **Manejo de excepciones:** `ResourceNotFoundException` (404), `DuplicateResourceException` (409), `BusinessConflictException` (409), `InvalidOperationException` (400), validación `@Valid` (400) y violaciones de integridad de MySQL (409) centralizadas en `GlobalExceptionHandler` (`ApiError`).
- **Funcionalidades extra:** Paginación real en servidor (`Spring Data Pageable`), búsqueda en vivo y filtro por categoría, sesiones y autenticación JWT (`ADMIN`/`USER`), ventanas flotantes (modales), tarjetas KPI, notificaciones toast y módulos adicionales de farmacia (Ventas, Clientes, Categorías, Campañas).

## Ejecución Rápida con Docker + MySQL

```bash
cd codigo-fuente
cp .env.example .env
docker compose up -d --build
npm --prefix frontend install
npm --prefix frontend run dev
```

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api/products`
- Credenciales Admin por defecto: `admin1` / `admin123` (Usuario estándar: `user1` / `user123`)

## Video Demostrativo

- [Ver demostración en YouTube](https://youtu.be/D90ekEZowrc) *(versión base de VitalLogix; se actualizará con la grabación final de la vista CRUD en MySQL)*
