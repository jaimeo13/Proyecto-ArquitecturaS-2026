package com.vitallogix.backend.service;

import com.vitallogix.backend.model.Category;
import com.vitallogix.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio encargado de la creación, edición, aprobación y estado de categorías.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Get all active categories. Used in catalog views and dropdown lists.
    /**
     * Obtiene el valor de {@code activeCategories}.
     * @return resultado de la operación.
     */
    public List<Category> getActiveCategories() {
        return categoryRepository.findByStatusOrderByNameAsc(Category.StatusEnum.ACTIVE);
    }

    // Get predefined system categories (e.g., Vitamins, Pain Relief).
    // These are created by admins during system setup and cannot be modified by users.
    /**
     * Obtiene el valor de {@code predefinedCategories}.
     * @return resultado de la operación.
     */
    public List<Category> getPredefinedCategories() {
        return categoryRepository.findByTypeOrderByNameAsc(Category.TypeEnum.PREDEFINED);
    }

    // Get custom user-created categories.
    // Note: Some may still be in PENDING_APPROVAL status waiting for admin review.
    /**
     * Obtiene el valor de {@code customCategories}.
     * @return resultado de la operación.
     */
    public List<Category> getCustomCategories() {
        return categoryRepository.findByTypeOrderByNameAsc(Category.TypeEnum.CUSTOM);
    }

    // Get categories pending admin approval. Used in admin approval workflow.
    // These are custom categories awaiting verification before becoming visible to users.
    /**
     * Obtiene el valor de {@code pendingApprovals}.
     * @return resultado de la operación.
     */
    public List<Category> getPendingApprovals() {
        return categoryRepository.findPendingApprovals();
    }

    // Create predefined category (admin only, from backend setup).
    // Predefined categories are system defaults and automatically visible in suggestions.
    // Throws IllegalArgumentException if category name already exists.
    /**
     * Registra una nueva entidad mediante {@code createPredefinedCategory}.
     * @param name parámetro de entrada de la operación.
     * @param description parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category createPredefinedCategory(String name, String description) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("La categoría '" + name + "' ya existe");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setType(Category.TypeEnum.PREDEFINED);
        category.setStatus(Category.StatusEnum.ACTIVE);
        category.setVisibleInSuggestions(true);
        category.setCreatedBy("SYSTEM");

        return categoryRepository.save(category);
    }

    // Create custom category from product form.
    // Custom categories start in PENDING_APPROVAL status and are invisible in suggestions until approved.
    // If category already exists and is ACTIVE, returns the existing one to avoid duplicates.
    /**
     * Registra una nueva entidad mediante {@code createCustomCategory}.
     * @param name parámetro de entrada de la operación.
     * @param createdBy parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category createCustomCategory(String name, String createdBy) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            Category existing = categoryRepository.findByNameIgnoreCase(name).orElse(null);
            if (existing != null && existing.getStatus() == Category.StatusEnum.ACTIVE) {
                return existing;
            }
        }

        Category category = new Category();
        category.setName(name);
        category.setType(Category.TypeEnum.CUSTOM);
        category.setStatus(Category.StatusEnum.PENDING_APPROVAL);
        category.setVisibleInSuggestions(false);
        category.setCreatedBy(createdBy);

        return categoryRepository.save(category);
    }

    // Approve category and activate it. Records admin ID and approval timestamp for audit trail.
    // Makes category visible in suggestions engine as part of approval workflow.
    // Throws RuntimeException if category not found.
    /**
     * Actualiza el estado o los atributos de la entidad (approveCategory).
     * @param id parámetro de entrada de la operación.
     * @param approvedBy parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category approveCategory(Long id, String approvedBy) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setStatus(Category.StatusEnum.ACTIVE);
        category.setApprovedBy(approvedBy);
        category.setApprovedAt(LocalDateTime.now());
        category.setVisibleInSuggestions(true);

        return categoryRepository.save(category);
    }

    // Reject and delete category. Used in admin approval workflow to discard unwanted custom categories.
    // Throws RuntimeException if category not found.
    /**
     * Elimina o rechaza el registro indicado (rejectCategory).
     * @param id parámetro de entrada de la operación.
     */
    public void rejectCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoryRepository.delete(category);
    }

    // Update category name and description.
    // Validates that new name is not already in use by another category (case-insensitive check).
    // Throws IllegalArgumentException if name conflicts, RuntimeException if category not found.
    /**
     * Actualiza el estado o los atributos de la entidad (updateCategory).
     * @param id parámetro de entrada de la operación.
     * @param name parámetro de entrada de la operación.
     * @param description parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        category.setName(name);
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    /**
     * Establece el valor de {@code categorySuggestionVisibility}.
     * @param id parámetro de entrada de la operación.
     * @param visibleInSuggestions parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category setCategorySuggestionVisibility(Long id, boolean visibleInSuggestions) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setVisibleInSuggestions(visibleInSuggestions);
        return categoryRepository.save(category);
    }

    /**
     * Actualiza el estado o los atributos de la entidad (deactivateCategory).
     * @param id parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setStatus(Category.StatusEnum.INACTIVE);
        return categoryRepository.save(category);
    }

    // Activate category (change status to ACTIVE).
    /**
     * Actualiza el estado o los atributos de la entidad (activateCategory).
     * @param id parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Category activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        category.setStatus(Category.StatusEnum.ACTIVE);
        return categoryRepository.save(category);
    }

    // Delete category entirely.
    // Throws RuntimeException if category not found or if there's a constraint violation.
    /**
     * Elimina o rechaza el registro indicado (deleteCategory).
     * @param id parámetro de entrada de la operación.
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoryRepository.delete(category);
    }

    // Get category by ID. Returns Optional (empty if not found).
    /**
     * Obtiene el valor de {@code categoryById}.
     * @param id parámetro de entrada de la operación.
     * @return resultado de la operación.
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Get all categories including inactive ones. Admin use case for full inventory review.
    /**
     * Obtiene el valor de {@code allCategories}.
     * @return resultado de la operación.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
