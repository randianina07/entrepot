package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Maintenance_vehicule;
import entrepot.demo.repository.Maintenance_vehicule_repository;

@Service
public class Maintenance_vehicule_service {
    private final Maintenance_vehicule_repository maintenance_vehicule_repository;

    public Maintenance_vehicule_service(Maintenance_vehicule_repository maintenance_vehicule_repository) {
        this.maintenance_vehicule_repository = maintenance_vehicule_repository;
    }

    public List<Maintenance_vehicule> findByVehiculeId(Long vehiculeId) {
        return maintenance_vehicule_repository.findByVehiculeId(vehiculeId);
    }
}
