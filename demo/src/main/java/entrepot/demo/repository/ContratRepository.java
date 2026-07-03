package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.Utilisateur;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByUtilisateur(Utilisateur utilisateur);

}