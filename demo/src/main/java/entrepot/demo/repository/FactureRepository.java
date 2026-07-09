package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.Facture;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByContrat(Contrat contrat);

}
