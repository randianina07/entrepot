package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.DemandeRenouvellement;

public interface DemandeRenouvellementRepository extends JpaRepository<DemandeRenouvellement, Long> {

    List<DemandeRenouvellement> findByContrat(Contrat contrat);

}
