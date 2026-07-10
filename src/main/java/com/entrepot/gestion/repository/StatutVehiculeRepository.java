package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.StatutVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutVehiculeRepository extends JpaRepository<StatutVehicule, Long> {
    StatutVehicule findByCode(String code);
}
