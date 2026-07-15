package com.entrepot.gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Maintenance_vehicule;

// import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance_vehicule, Long> {
	
}
