<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/LigneMouvementRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.LigneMouvement;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.LigneMouvement;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/LigneMouvementRepository.java
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
