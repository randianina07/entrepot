package com.entrepot.gestion.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.entrepot.gestion.model.TarifZone;
import com.entrepot.gestion.model.TypeZone;
import com.entrepot.gestion.model.UniteDuree;

public interface TarifZoneRepository extends JpaRepository<TarifZone, Long> {

    @Query("SELECT t FROM TarifZone t WHERE t.typeZone = :typeZone AND t.uniteDuree = :uniteDuree " +
           "AND t.dateDebutValidite <= :date AND (t.dateFinValidite IS NULL OR t.dateFinValidite >= :date)")
    Optional<TarifZone> findByTypeZoneAndUniteDureeAndDate(
            @Param("typeZone") TypeZone typeZone,
            @Param("uniteDuree") UniteDuree uniteDuree,
            @Param("date") LocalDate date);
}
