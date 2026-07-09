package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Etage;

public interface Etage_repository extends JpaRepository<Etage, Long> {
    
}
