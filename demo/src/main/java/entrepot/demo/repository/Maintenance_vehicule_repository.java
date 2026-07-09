package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Maintenance_vehicule;

public interface Maintenance_vehicule_repository extends JpaRepository<Maintenance_vehicule, Long> {
    List<Maintenance_vehicule> findByVehiculeId(Long vehiculeId);
}
