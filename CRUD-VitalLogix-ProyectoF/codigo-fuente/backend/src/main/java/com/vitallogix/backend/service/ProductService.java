package com.vitallogix.backend.service;

import com.vitallogix.backend.dto.ProductBatchDeleteResponse;
import com.vitallogix.backend.dto.ProductBatchUpdateRequest;
import com.vitallogix.backend.dto.ProductBatchUpdateResponse;
import com.vitallogix.backend.dto.ProductRequest;
import com.vitallogix.backend.dto.ProductResponse;
import com.vitallogix.backend.exception.BusinessConflictException;
import com.vitallogix.backend.exception.DuplicateResourceException;
import com.vitallogix.backend.exception.InvalidOperationException;
import com.vitallogix.backend.exception.ResourceNotFoundException;
import com.vitallogix.backend.model.Product;
import com.vitallogix.backend.repository.ProductRepository;
import com.vitallogix.backend.strategy.PromotionStrategy;
import com.vitallogix.backend.strategy.PromotionStrategyFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Implementación concreta del servicio de Gestión de Productos ({@link ProductServicePort}).
 * Coordina las reglas de negocio del módulo CRUD de inventario: validación de códigos únicos,
 * generación automática de SKU, aplicación de estrategias de promoción, eliminación individual
 * y eliminación masiva transaccional contra la base de datos MySQL.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Service
public class ProductService implements ProductServicePort {

    /** Repositorio JPA encargado de la persistencia de productos en MySQL. */
    private final ProductRepository repository;

    /**
     * Crea el servicio de productos inyectando el repositorio JPA requerido.
     *
     * @param repository repositorio de productos.
     */
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String query, String category, Pageable pageable) {
        String cleanQuery = query != null ? query.trim() : null;
        String cleanCategory = category != null ? category.trim() : null;
        boolean hasFilter = (cleanQuery != null && !cleanQuery.isEmpty())
                || (cleanCategory != null && !cleanCategory.isEmpty());

        Page<Product> page = hasFilter
                ? repository.searchPaginated(cleanQuery, cleanCategory, pageable)
                : repository.findAll(pageable);

        return page.map(this::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return toResponse(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName().trim());
        String requestedCode = normalizeCode(request.getCode());
        if (requestedCode == null) {
            product.setCode(generateUniqueCode(request.getName()));
        } else {
            if (repository.existsByCodeIgnoreCase(requestedCode)) {
                throw new DuplicateResourceException("El código de producto '" + requestedCode + "' ya existe");
            }
            product.setCode(requestedCode);
        }
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setRequiresPrescription(request.isRequiresPrescription());
        product.setVisibleInSuggestions(request.isVisibleInSuggestions());
        applyPromotionFields(product, request);
        product.setExpirationDate(request.getExpirationDate());
        return toResponse(repository.save(product));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        product.setName(request.getName().trim());
        String requestedCode = normalizeCode(request.getCode());
        if (requestedCode != null) {
            Product existing = repository.findByCodeIgnoreCase(requestedCode).orElse(null);
            if (existing != null && !existing.getId().equals(product.getId())) {
                throw new DuplicateResourceException("El código de producto '" + requestedCode + "' ya existe");
            }
            product.setCode(requestedCode);
        } else if (normalizeCode(product.getCode()) == null) {
            product.setCode(generateUniqueCode(request.getName()));
        }
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setRequiresPrescription(request.isRequiresPrescription());
        product.setVisibleInSuggestions(request.isVisibleInSuggestions());
        applyPromotionFields(product, request);
        product.setExpirationDate(request.getExpirationDate());
        return toResponse(repository.save(product));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse updateVisibility(Long id, Boolean visibleInSuggestions) {
        if (visibleInSuggestions == null) {
            throw new InvalidOperationException("Debe indicar el valor de visibilidad en sugerencias");
        }
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        product.setVisibleInSuggestions(visibleInSuggestions);
        return toResponse(repository.save(product));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse addStock(Long id, int quantity) {
        if (quantity <= 0) {
            throw new InvalidOperationException("La cantidad a reabastecer debe ser mayor a cero");
        }
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        product.setStock(product.getStock() + quantity);
        return toResponse(repository.save(product));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        try {
            repository.deleteById(id);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessConflictException(
                    "No se puede eliminar el producto porque tiene ventas o campañas asociadas"
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductBatchUpdateResponse updateBatch(ProductBatchUpdateRequest request) {
        if (request == null || request.ids() == null || request.ids().isEmpty()) {
            throw new InvalidOperationException("Debe seleccionar al menos un producto para modificar en masa");
        }

        boolean hasCategory = request.category() != null && !request.category().trim().isEmpty();
        boolean hasPrice = request.price() != null;
        boolean hasStock = request.stock() != null;
        boolean hasStockDelta = request.stockDelta() != null;
        boolean hasPrescription = request.requiresPrescription() != null;
        boolean hasSuggestions = request.visibleInSuggestions() != null;

        if (!hasCategory && !hasPrice && !hasStock && !hasStockDelta && !hasPrescription && !hasSuggestions) {
            throw new InvalidOperationException("Debe especificar al menos un campo a modificar en masa");
        }
        if (hasStock && hasStockDelta) {
            throw new InvalidOperationException("Indique stock fijo o ajuste de stock (+/-), no ambos a la vez");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(request.ids());
        List<Product> foundProducts = repository.findAllById(uniqueIds);
        if (foundProducts.size() != uniqueIds.size()) {
            Set<Long> foundIds = foundProducts.stream()
                    .map(Product::getId)
                    .collect(java.util.stream.Collectors.toSet());
            List<Long> missing = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("No se encontraron los productos con ID: " + missing);
        }

        for (Product product : foundProducts) {
            if (hasCategory) {
                product.setCategory(request.category().trim());
            }
            if (hasPrice) {
                product.setPrice(request.price());
            }
            if (hasStock) {
                product.setStock(request.stock());
            } else if (hasStockDelta) {
                int updatedStock = (product.getStock() == null ? 0 : product.getStock()) + request.stockDelta();
                if (updatedStock < 0) {
                    throw new InvalidOperationException(
                            "El ajuste de stock dejaría en negativo al producto #" + product.getId() + " (" + product.getName() + ")"
                    );
                }
                product.setStock(updatedStock);
            }
            if (hasPrescription) {
                product.setRequiresPrescription(request.requiresPrescription());
            }
            if (hasSuggestions) {
                product.setVisibleInSuggestions(request.visibleInSuggestions());
            }
        }

        repository.saveAll(foundProducts);
        repository.flush();

        List<Long> updatedIds = new ArrayList<>(uniqueIds);
        return new ProductBatchUpdateResponse(
                updatedIds.size(),
                updatedIds,
                "Se actualizaron en masa " + updatedIds.size() + " registro(s) correctamente"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductBatchDeleteResponse deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new InvalidOperationException("Debe seleccionar al menos un producto para eliminar");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(ids);
        List<Product> foundProducts = repository.findAllById(uniqueIds);
        if (foundProducts.size() != uniqueIds.size()) {
            Set<Long> foundIds = foundProducts.stream()
                    .map(Product::getId)
                    .collect(java.util.stream.Collectors.toSet());
            List<Long> missing = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("No se encontraron los productos con ID: " + missing);
        }

        try {
            repository.deleteAllInBatch(foundProducts);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessConflictException(
                    "No se pudieron eliminar los registros seleccionados porque uno o más tienen ventas o campañas asociadas"
            );
        }

        List<Long> deletedIds = new ArrayList<>(uniqueIds);
        return new ProductBatchDeleteResponse(
                deletedIds.size(),
                deletedIds,
                "Se eliminaron " + deletedIds.size() + " registro(s) correctamente"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> search(String name, Long id, String code, String category) {
        List<Product> products = new ArrayList<>();
        if (id != null) {
            repository.findById(id).ifPresent(products::add);
        } else if (code != null) {
            repository.findByCodeIgnoreCase(code).ifPresent(products::add);
        } else if (name != null) {
            products = repository.findByNameContainingIgnoreCase(name);
        } else if (category != null) {
            products = repository.findAll().stream()
                    .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                    .toList();
        }
        return products.stream().map(this::toResponse).toList();
    }

    /**
     * Convierte una entidad {@link Product} a su representación de salida {@link ProductResponse}.
     *
     * @param p entidad de dominio persistida.
     * @return DTO de respuesta del producto.
     */
    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getCode(),
                p.getDescription(),
                p.getImageUrl(),
                p.getCategory(),
                p.getPrice(),
                p.getStock(),
                p.isRequiresPrescription(),
                p.isVisibleInSuggestions(),
                p.getPromotionType(),
                p.getPromoBuyQuantity(),
                p.getPromoPayQuantity(),
                p.getPromoPercentDiscount(),
                p.getCreatedAt(),
                p.getExpirationDate()
        );
    }

    /**
     * Valida y aplica los campos de promoción utilizando el patrón Strategy ({@link PromotionStrategyFactory}).
     *
     * @param product entidad producto a configurar.
     * @param request solicitud con los parámetros promocionales.
     */
    private void applyPromotionFields(Product product, ProductRequest request) {
        String promotionType = request.getPromotionType() == null
                ? "NONE"
                : request.getPromotionType().trim().toUpperCase(Locale.ROOT);
        if (promotionType.isEmpty()) {
            promotionType = "NONE";
        }

        PromotionStrategy strategy = PromotionStrategyFactory.getInstance().getStrategy(promotionType);
        strategy.validate(request.getPromoBuyQuantity(), request.getPromoPayQuantity(), request.getPromoPercentDiscount());

        product.setPromotionType(promotionType);

        if ("BUY_X_PAY_Y".equals(promotionType)) {
            product.setPromoBuyQuantity(request.getPromoBuyQuantity());
            product.setPromoPayQuantity(request.getPromoPayQuantity());
            product.setPromoPercentDiscount(null);
        } else if ("PERCENTAGE".equals(promotionType)) {
            product.setPromoPercentDiscount(request.getPromoPercentDiscount());
            product.setPromoBuyQuantity(null);
            product.setPromoPayQuantity(null);
        } else {
            product.setPromoBuyQuantity(null);
            product.setPromoPayQuantity(null);
            product.setPromoPercentDiscount(null);
        }
    }

    /**
     * Normaliza un código de producto eliminando espacios y convirtiendo a mayúsculas.
     *
     * @param code código de entrada.
     * @return código normalizado o {@code null} si está vacío.
     */
    private String normalizeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Genera un código SKU único a partir de las iniciales del nombre del producto y un sufijo aleatorio.
     *
     * @param productName nombre del producto base.
     * @return código único disponible en base de datos.
     */
    private String generateUniqueCode(String productName) {
        String base = "MED";
        if (productName != null && !productName.trim().isEmpty()) {
            String normalized = productName.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "");
            if (!normalized.isEmpty()) {
                base = normalized.substring(0, Math.min(4, normalized.length()));
            }
        }

        for (int i = 0; i < 20; i++) {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
            String candidate = base + "-" + suffix;
            if (!repository.existsByCodeIgnoreCase(candidate)) {
                return candidate;
            }
        }

        throw new InvalidOperationException("No fue posible generar un código SKU único para el producto");
    }
}
