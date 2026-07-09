package com.entrepot.gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Emplacement;

public interface Emplacement_repository extends JpaRepository<Emplacement, Long> {
    
}
