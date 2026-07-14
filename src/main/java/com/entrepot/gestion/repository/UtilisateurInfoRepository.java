package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.model.UtilisateurInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurInfoRepository extends JpaRepository<UtilisateurInfo, Long> {
    boolean existsByUtilisateurId(Long utilisateurId);

    List<UtilisateurInfo> findByUtilisateur_Role_Code(String roleCode);

    Optional<UtilisateurInfo> findByUtilisateur(Utilisateur utilisateur);
}
