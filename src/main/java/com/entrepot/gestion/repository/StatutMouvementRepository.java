<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/StatutMouvementRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.StatutMouvement;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.StatutMouvement;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/StatutMouvementRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutMouvementRepository extends JpaRepository<StatutMouvement, Long> {
    
    Optional<StatutMouvement> findByCode(String code);
}
