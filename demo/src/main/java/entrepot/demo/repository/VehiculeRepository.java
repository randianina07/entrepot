package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {    
}