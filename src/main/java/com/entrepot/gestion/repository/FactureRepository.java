package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.Facture;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByContrat(Contrat contrat);

}
