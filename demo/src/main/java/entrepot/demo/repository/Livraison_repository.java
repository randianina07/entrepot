package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Livraison;

public interface Livraison_repository extends JpaRepository<Livraison, Long> {    
}


