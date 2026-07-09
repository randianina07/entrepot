<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/StatsClientRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.StatsClient;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.StatsClient;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/StatsClientRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StatsClientRepository extends JpaRepository<StatsClient, Long> {
    
    Optional<StatsClient> findByClientIdAndDateDebutAndDateFin(Long clientId, LocalDate dateDebut, LocalDate dateFin);
}
