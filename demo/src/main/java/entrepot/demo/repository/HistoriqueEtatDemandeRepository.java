package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.HistoriqueEtatDemande;

public interface HistoriqueEtatDemandeRepository extends JpaRepository<HistoriqueEtatDemande, Long> {

    List<HistoriqueEtatDemande> findByDemandeStockage(DemandeStockage demandeStockage);

}
