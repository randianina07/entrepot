package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.FluxEntreesSorties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FluxEntreesSortiesRepository extends JpaRepository<FluxEntreesSorties, Long> {
    
    List<FluxEntreesSorties> findByDateBetween(LocalDate debut, LocalDate fin);
}
