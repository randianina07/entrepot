package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Chauffeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {
    
    List<Chauffeur> findByActif(Boolean actif);
    
    List<Chauffeur> findByDateExpirationPermisBefore(LocalDate date);
    
    List<Chauffeur> findByNomContainingIgnoreCase(String nom);
    
    boolean existsByNumeroPermis(String numeroPermis);
}
