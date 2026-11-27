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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link ProductService}, validando CRUD completo, paginación,
 * borrado masivo y manejo de excepciones de dominio.
 */
class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    @Test
    void findAllWithQueryUsesSearchPaginated() {
        Product p = sampleProduct(1L, "Paracetamol", "PARA-1");
        PageRequest pageable = PageRequest.of(0, 5);
        when(repository.searchPaginated("para", null, pageable))
                .thenReturn(new PageImpl<>(List.of(p), pageable, 1));

        Page<ProductResponse> result = service.findAll("para", null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Paracetamol", result.getContent().get(0).name());
        verify(repository).searchPaginated("para", null, pageable);
    }

    @Test
    void findByIdThrowsResourceNotFoundExceptionWhenMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(999L));
    }

    @Test
    void createThrowsDuplicateResourceExceptionWhenCodeExists() {
        when(repository.existsByCodeIgnoreCase("PARA-1")).thenReturn(true);
        ProductRequest req = new ProductRequest(
                "Paracetamol", "PARA-1", "Desc", null, "Analgésicos",
                new BigDecimal("45.00"), 10, false, true, "NONE", null, null, null,
                LocalDateTime.now().plusMonths(6)
        );

        assertThrows(DuplicateResourceException.class, () -> service.create(req));
        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void deleteBatchDeletesAllSelectedProducts() {
        Product p1 = sampleProduct(1L, "Producto A", "A-1");
        Product p2 = sampleProduct(2L, "Producto B", "B-2");
        when(repository.findAllById(anyIterable())).thenReturn(List.of(p1, p2));

        ProductBatchDeleteResponse response = service.deleteBatch(List.of(1L, 2L));

        assertEquals(2, response.deletedCount());
        assertEquals(List.of(1L, 2L), response.deletedIds());
        verify(repository).deleteAllInBatch(List.of(p1, p2));
    }

    @Test
    void deleteBatchThrowsInvalidOperationExceptionWhenEmpty() {
        assertThrows(InvalidOperationException.class, () -> service.deleteBatch(List.of()));
    }

    @Test
    void deleteBatchThrowsBusinessConflictExceptionOnIntegrityViolation() {
        Product p1 = sampleProduct(1L, "Producto A", "A-1");
        when(repository.findAllById(anyIterable())).thenReturn(List.of(p1));
        doThrow(new DataIntegrityViolationException("FK constraint")).when(repository).flush();

        assertThrows(BusinessConflictException.class, () -> service.deleteBatch(List.of(1L)));
    }

    @Test
    void updateBatchUpdatesSelectedProductsCategoryAndStockDelta() {
        Product p1 = sampleProduct(1L, "Producto A", "A-1");
        Product p2 = sampleProduct(2L, "Producto B", "B-2");
        when(repository.findAllById(anyIterable())).thenReturn(List.of(p1, p2));

        ProductBatchUpdateRequest req = new ProductBatchUpdateRequest(
                List.of(1L, 2L), "Vitaminas", null, null, 10, null, false
        );
        ProductBatchUpdateResponse response = service.updateBatch(req);

        assertEquals(2, response.updatedCount());
        assertEquals("Vitaminas", p1.getCategory());
        assertEquals(30, p1.getStock());
        assertFalse(p2.isVisibleInSuggestions());
        verify(repository).saveAll(List.of(p1, p2));
    }

    @Test
    void updateBatchThrowsInvalidOperationWhenNoFieldsProvided() {
        ProductBatchUpdateRequest req = new ProductBatchUpdateRequest(
                List.of(1L), null, null, null, null, null, null
        );
        assertThrows(InvalidOperationException.class, () -> service.updateBatch(req));
    }

    @Test
    void updateBatchThrowsInvalidOperationWhenStockBecomesNegative() {
        Product p1 = sampleProduct(1L, "Producto A", "A-1");
        when(repository.findAllById(anyIterable())).thenReturn(List.of(p1));
        ProductBatchUpdateRequest req = new ProductBatchUpdateRequest(
                List.of(1L), null, null, null, -50, null, null
        );
        assertThrows(InvalidOperationException.class, () -> service.updateBatch(req));
    }

    private Product sampleProduct(Long id, String name, String code) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setCode(code);
        p.setCategory("Analgésicos");
        p.setPrice(new BigDecimal("50.00"));
        p.setStock(20);
        p.setPromotionType("NONE");
        return p;
    }
}
