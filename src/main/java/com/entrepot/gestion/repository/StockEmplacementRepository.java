package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.StockEmplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface StockEmplacementRepository extends JpaRepository<StockEmplacement, Long> {
    
    Optional<StockEmplacement> findByEmplacementIdAndProduitId(Long emplacementId, Long produitId);
    
    @Query("SELECT SUM(se.quantite * p.volumeUnitaireM3) FROM StockEmplacement se " +
           "JOIN se.produit p WHERE se.emplacement.id = :emplacementId")
    BigDecimal sumVolumeByEmplacementId(Long emplacementId);
    
    @Query("SELECT SUM(se.quantite * p.volumeUnitaireM3) FROM StockEmplacement se " +
           "JOIN se.produit p JOIN se.emplacement e JOIN e.zone z " +
           "WHERE 1=0") // TODO: Implement proper client volume calculation when Contrat entity is available
    BigDecimal sumVolumeByClientId(Long clientId);
}
