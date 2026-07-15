package com.entrepot.gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.AbonnementStockage;
import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.Utilisateur;

public interface AbonnementStockageRepository extends JpaRepository<AbonnementStockage, Long> {

    Optional<AbonnementStockage> findByContrat(Contrat contrat);

    Optional<AbonnementStockage> findByUtilisateur(Utilisateur utilisateur);

}