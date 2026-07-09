package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.RenouvellementContrat;

public interface RenouvellementContratRepository extends JpaRepository<RenouvellementContrat, Long> {

    List<RenouvellementContrat> findByContrat(Contrat contrat);

}
