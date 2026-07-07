package entrepot.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Utilisateur;
import entrepot.demo.model.UtilisateurInfo;

public interface UtilisateurInfoRepository extends JpaRepository<UtilisateurInfo, Long> {

    Optional<UtilisateurInfo> findByUtilisateur(Utilisateur utilisateur);

    List<UtilisateurInfo> findAll();
}