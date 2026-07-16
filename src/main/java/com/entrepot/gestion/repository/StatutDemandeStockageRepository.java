package com.entrepot.gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.StatutDemandeStockage;

public interface StatutDemandeStockageRepository extends JpaRepository<StatutDemandeStockage, Long> {

    Optional<StatutDemandeStockage> findByCode(String code);

}
