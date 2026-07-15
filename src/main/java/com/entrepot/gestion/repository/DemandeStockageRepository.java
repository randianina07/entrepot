package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.DemandeStockage;
import com.entrepot.gestion.model.Utilisateur;

public interface DemandeStockageRepository extends JpaRepository<DemandeStockage, Long> {

    List<DemandeStockage> findByUtilisateur(Utilisateur utilisateur);

}
