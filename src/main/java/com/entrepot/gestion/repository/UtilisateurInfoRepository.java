package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.UtilisateurInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurInfoRepository extends JpaRepository<UtilisateurInfo, Long> {
    boolean existsByUtilisateurId(Long utilisateurId);

    List<UtilisateurInfo> findByUtilisateur_Role_Code(String roleCode);

    Optional<UtilisateurInfo> findByUtilisateurId(Long utilisateurId);

    @Query("SELECT DISTINCT ui.secteur FROM UtilisateurInfo ui " +
           "WHERE ui.utilisateur.role.code = 'CLIENT' AND ui.secteur IS NOT NULL AND ui.secteur <> '' " +
           "ORDER BY ui.secteur")
    List<String> findDistinctSecteurs();

    @Query("SELECT ui FROM UtilisateurInfo ui WHERE ui.utilisateur.role.code = 'CLIENT' " +
           "AND (:nom IS NULL OR :nom = '' OR LOWER(ui.nom) LIKE LOWER(CONCAT('%',:nom,'%'))) " +
           "AND (:prenom IS NULL OR :prenom = '' OR LOWER(ui.prenom) LIKE LOWER(CONCAT('%',:prenom,'%'))) " +
           "AND (:email IS NULL OR :email = '' OR LOWER(ui.utilisateur.email) LIKE LOWER(CONCAT('%',:email,'%'))) " +
           "AND (:secteur IS NULL OR :secteur = '' OR ui.secteur = :secteur) " +
           "AND (COALESCE(:actif, ui.utilisateur.actif) = ui.utilisateur.actif) " +
           "AND (COALESCE(:dateDebut, ui.utilisateur.dateCreation) <= ui.utilisateur.dateCreation) " +
           "AND (COALESCE(:dateFin, ui.utilisateur.dateCreation) >= ui.utilisateur.dateCreation) " +
           "ORDER BY LOWER(ui.nom) ASC")
    List<UtilisateurInfo> searchClients(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("email") String email,
            @Param("secteur") String secteur,
            @Param("actif") Boolean actif,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            @Param("tri") String tri);
}
