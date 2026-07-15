package com.entrepot.gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Type_maintenance;

public interface TypeMaintenanceRepository extends JpaRepository<Type_maintenance, Long> {
}