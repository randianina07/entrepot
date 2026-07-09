package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.DemandeRenouvellement;

public interface DemandeRenouvellementRepository extends JpaRepository<DemandeRenouvellement, Long> {

    List<DemandeRenouvellement> findByContrat(Contrat contrat);

}
