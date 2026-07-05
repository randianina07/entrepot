package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.StockEmplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface StockEmplacementRepository extends JpaRepository<StockEmplacement, Long> {
    
    Optional<StockEmplacement> findByEmplacementIdAndProduitId(Long emplacementId, Long produitId);
    
    @Query("SELECT COALESCE(SUM(se.quantite * p.volumeUnitaireM3), 0) FROM StockEmplacement se " +
           "JOIN se.produit p WHERE se.emplacement.id = :emplacementId")
    BigDecimal sumVolumeByEmplacementId(Long emplacementId);
    
    // TODO: Implement client volume calculation when contrats relationship is added
    // @Query("SELECT COALESCE(SUM(se.quantite * p.volumeUnitaireM3), 0) FROM StockEmplacement se " +
    //        "JOIN se.produit p JOIN se.emplacement e JOIN e.zone z " +
    //        "JOIN z.contrats c WHERE c.client.id = :clientId")
    // BigDecimal sumVolumeByClientId(Long clientId);
}
