package com.vitallogix.backend.service;

import com.vitallogix.backend.dto.ProductBatchDeleteResponse;
import com.vitallogix.backend.dto.ProductBatchUpdateRequest;
import com.vitallogix.backend.dto.ProductBatchUpdateResponse;
import com.vitallogix.backend.dto.ProductRequest;
import com.vitallogix.backend.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz de salida (Puerto de Servicio) del componente de Gestión de Productos (CRUD).
 * Define el contrato de negocio para listar, buscar, paginar, crear, actualizar, reabastecer
 * y eliminar (individual o masivamente) productos en MySQL, aplicando el principio DIP de SOLID.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public interface ProductServicePort {

    /**
     * Lista productos de forma paginada aplicando opcionalmente búsqueda de texto y filtro de categoría.
     *
     * @param query    término de búsqueda opcional (nombre, código, descripción o categoría).
     * @param category filtro de categoría exacta opcional.
     * @param pageable parámetros de página, tamaño y ordenamiento.
     * @return página de productos en formato {@link ProductResponse}.
     */
    Page<ProductResponse> findAll(String query, String category, Pageable pageable);

    /**
     * Recupera un producto específico por su identificador único.
     *
     * @param id identificador del producto.
     * @return datos del producto encontrado.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException si el producto no existe.
     */
    ProductResponse findById(Long id);

    /**
     * Registra un nuevo producto en la base de datos MySQL.
     *
     * @param request datos validados del nuevo producto.
     * @return producto persistido con su identificador generado.
     * @throws com.vitallogix.backend.exception.DuplicateResourceException si el código ya existe.
     */
    ProductResponse create(ProductRequest request);

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id      identificador del producto a modificar.
     * @param request nuevos datos del producto.
     * @return producto actualizado.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException  si el producto no existe.
     * @throws com.vitallogix.backend.exception.DuplicateResourceException si el nuevo código pertenece a otro producto.
     */
    ProductResponse update(Long id, ProductRequest request);

    /**
     * Actualiza la visibilidad del producto en el motor de sugerencias.
     *
     * @param id                   identificador del producto.
     * @param visibleInSuggestions nuevo valor booleano de visibilidad.
     * @return producto actualizado.
     * @throws com.vitallogix.backend.exception.InvalidOperationException si el valor es nulo.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException si el producto no existe.
     */
    ProductResponse updateVisibility(Long id, Boolean visibleInSuggestions);

    /**
     * Incrementa el stock disponible de un producto existente.
     *
     * @param id       identificador del producto.
     * @param quantity unidades positivas a sumar al inventario.
     * @return producto con stock actualizado.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException si el producto no existe.
     */
    ProductResponse addStock(Long id, int quantity);

    /**
     * Elimina un producto individual de la base de datos MySQL.
     *
     * @param id identificador del producto a eliminar.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException si el producto no existe.
     * @throws com.vitallogix.backend.exception.BusinessConflictException si tiene ventas asociadas.
     */
    void delete(Long id);

    /**
     * Modifica en lote los campos especificados para todos los productos seleccionados en la tabla administrativa.
     *
     * @param request identificadores seleccionados y campos opcionales a aplicar en masa.
     * @return resumen con la cantidad e IDs modificados.
     * @throws com.vitallogix.backend.exception.InvalidOperationException si no hay IDs, no se indicó ningún cambio o resulta en stock negativo.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException si alguno de los IDs no existe.
     */
    ProductBatchUpdateResponse updateBatch(ProductBatchUpdateRequest request);

    /**
     * Elimina en lote todos los productos cuyos identificadores estén en la lista recibida.
     *
     * @param ids lista de identificadores seleccionados en la tabla CRUD.
     * @return resumen con la cantidad e IDs eliminados.
     * @throws com.vitallogix.backend.exception.InvalidOperationException si la lista es nula o vacía.
     * @throws com.vitallogix.backend.exception.ResourceNotFoundException si alguno de los IDs no existe.
     * @throws com.vitallogix.backend.exception.BusinessConflictException si alguno tiene ventas asociadas.
     */
    ProductBatchDeleteResponse deleteBatch(List<Long> ids);

    /**
     * Busca productos sin paginación por orden de prioridad: id, código, nombre o categoría.
     *
     * @param name     nombre parcial opcional.
     * @param id       identificador exacto opcional.
     * @param code     código exacto opcional.
     * @param category categoría exacta opcional.
     * @return lista de productos coincidentes.
     */
    List<ProductResponse> search(String name, Long id, String code, String category);
}
