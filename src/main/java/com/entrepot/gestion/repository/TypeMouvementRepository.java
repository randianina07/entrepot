package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeMouvementRepository extends JpaRepository<TypeMouvement, Long> {
    
    Optional<TypeMouvement> findByCode(String code);
}
