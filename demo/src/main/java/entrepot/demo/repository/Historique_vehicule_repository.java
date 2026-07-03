package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Historique_vehicule;

public interface Historique_vehicule_repository extends JpaRepository<Historique_vehicule , Long> {
    List<Historique_vehicule> findByVehiculeId(Long vehiculeId);
}
