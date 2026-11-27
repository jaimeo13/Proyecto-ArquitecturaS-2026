package com.vitallogix.backend.repository;

import com.vitallogix.backend.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para consultas y persistencia de campañas en MySQL.
 *
 * @author Equipo VitalLogix (Dzib Pech, Osorio Muñoz, Solis Chen)
 * @version 2.0
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    // Find all active campaigns
    List<Campaign> findByIsActiveTrue();
    
    // Find campaigns that are active AND currently valid (between startDate and endDate)
    @Query("SELECT c FROM Campaign c WHERE c.isActive = true AND c.startDate <= :now AND c.endDate >= :now")
    List<Campaign> findActiveCampaignsAtTime(LocalDateTime now);
    
    // Find campaigns by name
    List<Campaign> findByNameContainingIgnoreCase(String name);
}
