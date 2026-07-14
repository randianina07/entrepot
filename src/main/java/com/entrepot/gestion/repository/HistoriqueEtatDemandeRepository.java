package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.DemandeStockage;
import com.entrepot.gestion.model.HistoriqueEtatDemande;

public interface HistoriqueEtatDemandeRepository extends JpaRepository<HistoriqueEtatDemande, Long> {

    List<HistoriqueEtatDemande> findByDemandeStockage(DemandeStockage demandeStockage);
    List<HistoriqueEtatDemande> findByDemandeStockageOrderByDateStatutDesc(
            DemandeStockage demandeStockage
    );
}
