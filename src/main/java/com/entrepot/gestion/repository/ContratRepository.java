package com.entrepot.gestion.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.Utilisateur;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByUtilisateur(Utilisateur utilisateur);

    List<Contrat> findByUtilisateurIdOrderByDateDebutDesc(Long utilisateurId);

    List<Contrat> findByUtilisateurIdAndDateDebut(Long utilisateurId, LocalDate dateDebut);

}
