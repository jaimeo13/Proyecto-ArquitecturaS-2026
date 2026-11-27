package com.vitallogix.backend.repository;

import com.vitallogix.backend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio Spring Data JPA para consulta de ventas, historiales y métricas.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    long countByCustomer_Id(Long customerId);
    long countByAccountUsernameIgnoreCase(String accountUsername);
    
    List<Sale> findByCustomer_IdOrderBySaleDateDesc(Long customerId);
}
