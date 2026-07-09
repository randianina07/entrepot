<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/FluxEntreesSortiesRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.FluxEntreesSorties;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.FluxEntreesSorties;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/FluxEntreesSortiesRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FluxEntreesSortiesRepository extends JpaRepository<FluxEntreesSorties, Long> {
    
    List<FluxEntreesSorties> findByDateBetween(LocalDate debut, LocalDate fin);
}
