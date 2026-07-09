package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Emplacement;

public interface Emplacement_repository extends JpaRepository<Emplacement, Long> {
    
}
