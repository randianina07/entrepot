package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.entrepot.gestion.model.Zone;

public interface Zone_repository extends JpaRepository<Zone, Long> {
    
    @Query(value = "SELECT * FROM zone WHERE type_zone_id = ?1", nativeQuery = true)
    public List<Zone> findByTypeZoneId(Long typeZoneId);

}
