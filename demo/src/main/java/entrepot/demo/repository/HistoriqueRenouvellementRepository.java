package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.DemandeRenouvellement;
import entrepot.demo.model.HistoriqueRenouvellement;

public interface HistoriqueRenouvellementRepository extends JpaRepository<HistoriqueRenouvellement, Long> {

    List<HistoriqueRenouvellement> findByDemandeRenouvellement(DemandeRenouvellement demandeRenouvellement);

}
