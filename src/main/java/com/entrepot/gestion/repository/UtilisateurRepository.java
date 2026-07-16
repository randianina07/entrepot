package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRoleCode(String email);
    
    boolean existsByEmail(String email);

    /**
     * Clients (role CLIENT) ayant au moins un contrat non termine,
     * c'est-a-dire sans date de fin ou avec une date de fin dans le futur.
     */
    @Query("SELECT DISTINCT u FROM Utilisateur u WHERE u.role.code = 'CLIENT' " +
           "AND EXISTS (SELECT c FROM Contrat c WHERE c.utilisateur = u " +
           "AND (c.dateFin IS NULL OR c.dateFin > CURRENT_DATE))")
    List<Utilisateur> findClientsAvecContratActif();
}
