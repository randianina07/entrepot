package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.Utilisateur;

public interface DemandeStockageRepository extends JpaRepository<DemandeStockage, Long> {

    List<DemandeStockage> findByUtilisateur(Utilisateur utilisateur);

}
