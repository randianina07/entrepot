package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Chauffeurs;

public interface Chauffeur_repository extends JpaRepository<Chauffeurs , Long> {   
}
