package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.StatsClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StatsClientRepository extends JpaRepository<StatsClient, Long> {
    
    Optional<StatsClient> findByClientIdAndDateDebutAndDateFin(Long clientId, LocalDate dateDebut, LocalDate dateFin);
}
