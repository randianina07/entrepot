<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/EmplacementRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.Emplacement;
========
package entrepot.demo.repositories;

>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:demo/src/main/java/entrepot/demo/repositories/EmplacementRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entrepot.demo.model.Emplacement;

@Repository
public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
    
}
