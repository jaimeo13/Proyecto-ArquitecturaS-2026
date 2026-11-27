package com.vitallogix.backend.controller;

import com.vitallogix.backend.dto.ProductBatchDeleteRequest;
import com.vitallogix.backend.dto.ProductBatchDeleteResponse;
import com.vitallogix.backend.dto.ProductBatchUpdateRequest;
import com.vitallogix.backend.dto.ProductBatchUpdateResponse;
import com.vitallogix.backend.dto.ProductRequest;
import com.vitallogix.backend.dto.ProductResponse;
import com.vitallogix.backend.dto.ProductVisibilityRequest;
import com.vitallogix.backend.dto.StockUpdateRequest;
import com.vitallogix.backend.service.ProductServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone la interfaz HTTP del componente CRUD de Productos ({@code /api/products}).
 * Provee endpoints para consulta paginada y filtrada, búsqueda, creación, edición, actualización
 * de stock/visibilidad, eliminación individual y eliminación masiva de registros seleccionados.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "product.tag.description")
public class ProductController {

    /** Puerto de servicio que encapsula la lógica CRUD y reglas de negocio de productos. */
    private final ProductServicePort productService;

    /**
     * Construye el controlador inyectando la abstracción {@link ProductServicePort}.
     *
     * @param productService servicio de gestión de productos.
     */
    public ProductController(ProductServicePort productService) {
        this.productService = productService;
    }

    /**
     * Lista los productos registrados en MySQL con soporte de paginación, búsqueda libre y filtro por categoría.
     *
     * @param q        término de búsqueda opcional (nombre, código, descripción o categoría).
     * @param category categoría exacta opcional.
     * @param pageable configuración de paginación y ordenamiento ({@code page}, {@code size}, {@code sort}).
     * @return página de productos encontrados.
     */
    @Operation(summary = "product.list.summary", description = "product.list.description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "product.list.ok")
    })
    @GetMapping
    public Page<ProductResponse> findAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return productService.findAll(q, category, pageable);
    }

    /**
     * Obtiene el detalle de un producto por su identificador único.
     *
     * @param id identificador del producto.
     * @return datos completos del producto.
     */
    @Operation(summary = "product.get.summary", description = "product.get.description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "product.get.ok"),
            @ApiResponse(responseCode = "404", description = "product.get.notfound")
    })
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    /**
     * Crea un nuevo producto en la base de datos MySQL (requiere rol {@code ADMIN}).
     *
     * @param request cuerpo JSON validado con los campos del producto.
     * @return producto recién creado con estado HTTP 201.
     */
    @Operation(summary = "product.create.summary", description = "product.create.description")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "product.create.ok"),
            @ApiResponse(responseCode = "400", description = "product.create.badrequest"),
            @ApiResponse(responseCode = "409", description = "Código de producto duplicado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    /**
     * Actualiza los valores de un producto existente por su identificador (requiere rol {@code ADMIN}).
     *
     * @param id      identificador del producto a editar.
     * @param request cuerpo JSON validado con los nuevos datos.
     * @return producto actualizado.
     */
    @Operation(summary = "product.update.summary", description = "product.update.description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "product.update.ok"),
            @ApiResponse(responseCode = "404", description = "product.update.notfound"),
            @ApiResponse(responseCode = "400", description = "product.update.badrequest")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    /**
     * Actualiza la visibilidad del producto en el motor de sugerencias (requiere rol {@code ADMIN}).
     *
     * @param id      identificador del producto.
     * @param request bandera booleana de visibilidad.
     * @return producto actualizado.
     */
    @Operation(summary = "product.visibility.summary", description = "product.visibility.description")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/visibility")
    public ProductResponse updateVisibility(@PathVariable Long id, @RequestBody ProductVisibilityRequest request) {
        return productService.updateVisibility(id, request.visibleInSuggestions());
    }

    /**
     * Elimina un producto individual de la base de datos MySQL (requiere rol {@code ADMIN}).
     *
     * @param id identificador del producto a eliminar.
     */
    @Operation(summary = "product.delete.summary", description = "product.delete.description")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "product.delete.ok"),
            @ApiResponse(responseCode = "404", description = "product.delete.notfound"),
            @ApiResponse(responseCode = "409", description = "Producto con ventas asociadas")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    /**
     * Modifica en masa los atributos indicados de todos los productos seleccionados mediante casillas
     * de verificación en la tabla administrativa (requiere rol {@code ADMIN}).
     *
     * @param request identificadores seleccionados y campos a actualizar en lote.
     * @return resumen con el conteo e identificadores actualizados.
     */
    @Operation(summary = "Modificar productos en masa", description = "Actualiza en lote categoría, precio, stock o banderas de los productos seleccionados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registros actualizados correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos o sin campos seleccionados"),
            @ApiResponse(responseCode = "404", description = "Uno o más productos no existen")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/batch")
    public ProductBatchUpdateResponse updateBatch(@Valid @RequestBody ProductBatchUpdateRequest request) {
        return productService.updateBatch(request);
    }

    /**
     * Elimina masivamente todos los productos seleccionados mediante sus casillas de verificación
     * en la tabla CRUD (requiere rol {@code ADMIN}).
     *
     * @param request lista de identificadores de productos a eliminar.
     * @return resumen con el conteo e identificadores eliminados.
     */
    @Operation(summary = "Eliminar productos seleccionados", description = "Elimina en lote los registros seleccionados en la tabla CRUD")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registros eliminados correctamente"),
            @ApiResponse(responseCode = "400", description = "Lista de IDs vacía o inválida"),
            @ApiResponse(responseCode = "404", description = "Uno o más productos no existen"),
            @ApiResponse(responseCode = "409", description = "Uno o más productos tienen ventas asociadas")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/batch")
    public ProductBatchDeleteResponse deleteBatch(@Valid @RequestBody ProductBatchDeleteRequest request) {
        return productService.deleteBatch(request.ids());
    }

    /**
     * Incrementa el stock de un producto existente (requiere rol {@code ADMIN}).
     *
     * @param id      identificador del producto.
     * @param request cantidad de unidades a agregar.
     * @return producto con el nuevo stock.
     */
    @Operation(summary = "product.restock.summary", description = "product.restock.description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "product.restock.ok"),
            @ApiResponse(responseCode = "404", description = "product.restock.notfound")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock")
    public ProductResponse addStock(@PathVariable Long id, @Valid @RequestBody StockUpdateRequest request) {
        return productService.addStock(id, request.quantity());
    }

    /**
     * Busca productos por orden de prioridad: identificador, código, nombre parcial o categoría.
     *
     * @param name     nombre parcial opcional.
     * @param id       identificador único opcional.
     * @param code     código único opcional.
     * @param category categoría opcional.
     * @return lista de productos coincidentes.
     */
    @GetMapping("/search")
    public List<ProductResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String category) {
        return productService.search(name, id, code, category);
    }
}
