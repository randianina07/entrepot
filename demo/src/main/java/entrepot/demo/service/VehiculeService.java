package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Vehicule;
import entrepot.demo.repository.VehiculeRepository;

@Service
public class VehiculeService {

    private final VehiculeRepository repository;

    public VehiculeService(VehiculeRepository repository) {
        this.repository = repository;
    }

    // Retourner tous les véhicules
    public List<Vehicule> listeVehicules() {
        return repository.findAll();
    }

    // Ajouter un véhicule
    public Vehicule ajouterVehicule(Vehicule vehicule) {
        return repository.save(vehicule);
    }
}
