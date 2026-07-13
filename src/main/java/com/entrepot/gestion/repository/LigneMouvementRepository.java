package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.LigneMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LigneMouvementRepository extends JpaRepository<LigneMouvement, Long> {
    
    List<LigneMouvement> findByMouvement_Id(Long mouvementId);
    
    @Query("SELECT l.produit.id, SUM(l.quantite) FROM LigneMouvement l " +
           "WHERE l.mouvement.dateMouvement >= :date GROUP BY l.produit.id " +
           "ORDER BY SUM(l.quantite) DESC")
    List<Object[]> sumQuantiteByProduitGroupedByProduit(LocalDateTime date);
}
