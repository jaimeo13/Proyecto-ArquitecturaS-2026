package com.vitallogix.backend.repository;

import com.vitallogix.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link Product} en MySQL.
 * Provee operaciones CRUD estándar, paginación, búsqueda combinada por texto/categoría y validaciones de unicidad.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca productos cuyo nombre contenga la cadena indicada ignorando mayúsculas y minúsculas.
     *
     * @param name texto parcial a buscar en el nombre.
     * @return lista de productos coincidentes.
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Busca un producto por su código de negocio único (insensible a mayúsculas).
     *
     * @param code código del producto.
     * @return {@link Optional} con el producto si existe.
     */
    Optional<Product> findByCodeIgnoreCase(String code);

    /**
     * Verifica si ya existe un producto registrado con el código especificado.
     *
     * @param code código a validar.
     * @return {@code true} si el código ya está en uso.
     */
    boolean existsByCodeIgnoreCase(String code);

    /**
     * Lista los productos cuyo stock sea estrictamente mayor al valor indicado.
     *
     * @param stock umbral mínimo de inventario.
     * @return lista de productos disponibles.
     */
    List<Product> findByStockGreaterThan(Integer stock);

    /**
     * Consulta paginada con filtros opcionales por texto libre (nombre, código, descripción o categoría)
     * y por categoría exacta en MySQL.
     *
     * @param query    término de búsqueda libre opcional.
     * @param category categoría exacta opcional.
     * @param pageable configuración de paginación y ordenamiento.
     * @return página de productos coincidentes.
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:query IS NULL OR :query = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(p.code, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(p.category, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:category IS NULL OR :category = ''
                   OR LOWER(COALESCE(p.category, '')) = LOWER(:category))
            """)
    Page<Product> searchPaginated(
            @Param("query") String query,
            @Param("category") String category,
            Pageable pageable
    );
}
