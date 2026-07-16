package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Emplacement;
import com.entrepot.gestion.model.StockEmplacement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockEmplacementRepository extends JpaRepository<StockEmplacement, Long> {
    
    Optional<StockEmplacement> findByEmplacementIdAndProduitId(Long emplacementId, Long produitId);

       List<StockEmplacement> findByEmplacementIdAndProduitIdAndZoneIdOrderByIdAsc(Long emplacementId, Long produitId, Long zoneId);
    
    @Query("SELECT SUM(se.quantite * p.volumeUnitaireM3) FROM StockEmplacement se " +
           "JOIN se.produit p WHERE se.emplacement.id = :emplacementId")
    BigDecimal sumVolumeByEmplacementId(@Param("emplacementId") Long emplacementId);
    
    @Query("SELECT SUM(se.quantite * p.volumeUnitaireM3) FROM StockEmplacement se " +
           "JOIN se.produit p " +
           "WHERE 1=0") 
    BigDecimal sumVolumeByClientId(@Param("clientId") Long clientId);

    @Query(value = "SELECT * FROM stocks_emplacement WHERE zone_id = ?1", nativeQuery = true)
    List<StockEmplacement> findByZoneId(Long id);

    List<StockEmplacement> findByEmplacement(Emplacement emplacement);
}
