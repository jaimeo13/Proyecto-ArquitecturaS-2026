# Diagrama de Casos de Uso — VitalLogix

**Tipo:** Diagrama de Casos de Uso (UML)  
**Herramienta:** Astah UML  
**Archivo fuente:** [UseCase VitalLogix.asta](UseCase%20VitalLogix.asta)

## Descripción

Representa las funcionalidades del sistema agrupadas en cuatro módulos principales:

1. **Módulo de Acceso:** Registro, inicio de sesión y acceso como invitado (`AuthModal`).
2. **Catálogo y Carrito:** Consulta y búsqueda de productos, categorías, sugerencias personalizadas y gestión del carrito de compras.
3. **Ventas:** Validación de receta, validación de cupón ClienteAmigo, validación final de stock, registro de venta, recibo y generación de factura PDF.
4. **Administración (exclusivo Administrador):** Gestión de usuarios administrativos, historial de ventas, clientes, categorías y **Gestión de Inventario (CRUD sobre MySQL)**, incluyendo creación, edición, reabastecimiento y eliminación individual, así como **`Consultar y filtrar tabla paginada`** (`<<include>>`), **`Ver detalle de producto`** (`<<extend>>`), **`Modificar productos en masa`** (`<<extend>>`) y **`Eliminar productos seleccionados`** (`<<extend>>`), con **`Seleccionar registros múltiples`** (`<<include>>`).

Incluye cuatro actores: **Visitante**, **Invitado**, **Usuario** y **Administrador**, cada uno con distintos niveles de acceso y operaciones disponibles dentro del sistema.
