<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/TopProduitRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.TopProduit;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.TopProduit;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/TopProduitRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TopProduitRepository extends JpaRepository<TopProduit, Long> {
    
    Optional<TopProduit> findByDateSnapshotAndProduitId(LocalDate dateSnapshot, Long produitId);
}
