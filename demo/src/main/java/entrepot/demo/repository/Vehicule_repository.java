package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Vehicule;

public interface Vehicule_repository extends JpaRepository<Vehicule, Long> {    
}