package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.DemandeRenouvellement;
import com.entrepot.gestion.model.HistoriqueRenouvellement;

public interface HistoriqueRenouvellementRepository extends JpaRepository<HistoriqueRenouvellement, Long> {

    List<HistoriqueRenouvellement> findByDemandeRenouvellement(DemandeRenouvellement demandeRenouvellement);
     HistoriqueRenouvellement findFirstByDemandeRenouvellementOrderByDateStatutDesc(DemandeRenouvellement demande);
}
