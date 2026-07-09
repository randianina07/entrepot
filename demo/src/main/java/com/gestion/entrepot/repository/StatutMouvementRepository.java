package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.StatutMouvement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutMouvementRepository extends JpaRepository<StatutMouvement, Long> {
    
    Optional<StatutMouvement> findByCode(String code);
}
