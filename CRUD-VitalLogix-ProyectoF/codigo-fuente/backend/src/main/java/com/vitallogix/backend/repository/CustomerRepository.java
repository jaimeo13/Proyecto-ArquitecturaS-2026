package com.vitallogix.backend.repository;

import com.vitallogix.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para gestión de clientes y validación de códigos ClienteAmigo.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByClienteAmigoNumber(String clienteAmigoNumber);
    boolean existsByClienteAmigoNumberIgnoreCase(String clienteAmigoNumber);
    Optional<Customer> findFirstByPhoneOrderByIdAsc(String phone);
    Optional<Customer> findFirstByNameOrderByIdAsc(String name);
}
