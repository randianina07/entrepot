package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.TypeVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeVehiculeRepository extends JpaRepository<TypeVehicule, Long> {
}
