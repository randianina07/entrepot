package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Statut_vehicule;

public interface Statut_vehicule_repository extends JpaRepository <Statut_vehicule , Long> {
	Optional<Statut_vehicule> findByCode(String code);
}
