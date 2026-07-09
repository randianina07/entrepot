package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Mouvement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MouvementRepository extends JpaRepository<Mouvement, Long> {
    
    List<Mouvement> findByTypeMouvement_Sens(String sens);
    
    List<Mouvement> findByTypeMouvement_Code(String code);
    
    List<Mouvement> findByStatutMouvement_Code(String code);
    
    List<Mouvement> findByClient_Id(Long clientId);
    
    List<Mouvement> findByDateMouvementBetween(LocalDateTime debut, LocalDateTime fin);
    
    List<Mouvement> findByClient_IdAndDateMouvementBetween(Long clientId, LocalDateTime debut, LocalDateTime fin);
    
    Long countByDateMouvementBetween(LocalDateTime debut, LocalDateTime fin);
    
    List<Mouvement> findTop5ByOrderByDateMouvementDesc();
}
