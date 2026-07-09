package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Statuts_mission;

public interface Statuts_mission_repository extends JpaRepository<Statuts_mission , Long> {    
	Optional<Statuts_mission> findByCode(String code);
}
