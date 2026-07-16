package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.RenouvellementContrat;

public interface RenouvellementContratRepository extends JpaRepository<RenouvellementContrat, Long> {

    List<RenouvellementContrat> findByContrat(Contrat contrat);

}
