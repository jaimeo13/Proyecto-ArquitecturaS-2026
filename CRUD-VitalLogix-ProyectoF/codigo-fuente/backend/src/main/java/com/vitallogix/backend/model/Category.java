package com.vitallogix.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA almacenada en MySQL (tabla categories) que clasifica los productos del inventario.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEnum type;

    @Column(nullable = true, columnDefinition = "boolean default true")
    // Controls visibility in product recommendations. Admin can toggle per category.
    // When false, products in this category won't appear in suggestion engine.
    private boolean visibleInSuggestions = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String approvedBy;

    @Column
    // Timestamp of admin approval. Null if status != ACTIVE or still pending.
    private LocalDateTime approvedAt;

    // Auto-set creation and update timestamps on entity lifecycle.
    /**
     * Ejecuta la operación {@code prePersist} del componente.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Update timestamp on every modification.
    /**
     * Ejecuta la operación {@code preUpdate} del componente.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Status enum: ACTIVE (visible), INACTIVE (hidden), PENDING_APPROVAL (custom categories awaiting admin review).
    public enum StatusEnum {
        ACTIVE,
        INACTIVE,
        PENDING_APPROVAL
    }

    // Type enum: PREDEFINED (system defaults), CUSTOM (user-created through API).
    public enum TypeEnum {
        PREDEFINED,
        CUSTOM
    }

    // Getters and Setters
    /**
     * Obtiene el valor de {@code id}.
     * @return resultado de la operación.
     */
    public Long getId() { return id; }
    /**
     * Establece el valor de {@code id}.
     * @param id parámetro de entrada de la operación.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el valor de {@code name}.
     * @return resultado de la operación.
     */
    public String getName() { return name; }
    /**
     * Establece el valor de {@code name}.
     * @param name parámetro de entrada de la operación.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene el valor de {@code description}.
     * @return resultado de la operación.
     */
    public String getDescription() { return description; }
    /**
     * Establece el valor de {@code description}.
     * @param description parámetro de entrada de la operación.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Obtiene el valor de {@code status}.
     * @return resultado de la operación.
     */
    public StatusEnum getStatus() { return status; }
    /**
     * Establece el valor de {@code status}.
     * @param status parámetro de entrada de la operación.
     */
    public void setStatus(StatusEnum status) { this.status = status; }

    /**
     * Obtiene el valor de {@code type}.
     * @return resultado de la operación.
     */
    public TypeEnum getType() { return type; }
    /**
     * Establece el valor de {@code type}.
     * @param type parámetro de entrada de la operación.
     */
    public void setType(TypeEnum type) { this.type = type; }

    /**
     * Indica el estado booleano de {@code visibleInSuggestions}.
     * @return resultado de la operación.
     */
    public boolean isVisibleInSuggestions() { return visibleInSuggestions; }
    /**
     * Establece el valor de {@code visibleInSuggestions}.
     * @param visibleInSuggestions parámetro de entrada de la operación.
     */
    public void setVisibleInSuggestions(boolean visibleInSuggestions) { this.visibleInSuggestions = visibleInSuggestions; }

    /**
     * Obtiene el valor de {@code createdAt}.
     * @return resultado de la operación.
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * Establece el valor de {@code createdAt}.
     * @param createdAt parámetro de entrada de la operación.
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Obtiene el valor de {@code updatedAt}.
     * @return resultado de la operación.
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /**
     * Establece el valor de {@code updatedAt}.
     * @param updatedAt parámetro de entrada de la operación.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Obtiene el valor de {@code createdBy}.
     * @return resultado de la operación.
     */
    public String getCreatedBy() { return createdBy; }
    /**
     * Establece el valor de {@code createdBy}.
     * @param createdBy parámetro de entrada de la operación.
     */
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    /**
     * Obtiene el valor de {@code approvedBy}.
     * @return resultado de la operación.
     */
    public String getApprovedBy() { return approvedBy; }
    /**
     * Establece el valor de {@code approvedBy}.
     * @param approvedBy parámetro de entrada de la operación.
     */
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    /**
     * Obtiene el valor de {@code approvedAt}.
     * @return resultado de la operación.
     */
    public LocalDateTime getApprovedAt() { return approvedAt; }
    /**
     * Establece el valor de {@code approvedAt}.
     * @param approvedAt parámetro de entrada de la operación.
     */
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}
