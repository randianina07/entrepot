package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.StatutMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutMissionRepository extends JpaRepository<StatutMission, Long> {
    StatutMission findByCode(String code);
}
