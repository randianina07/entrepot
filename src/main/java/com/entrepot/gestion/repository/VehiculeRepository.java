package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByStatutVehiculeCode(String code);
    boolean existsByImmatriculation(String immatriculation);
}
