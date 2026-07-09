package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Livraison;
import entrepot.demo.model.Statut_vehicule;
import entrepot.demo.model.Type_vehicule;
import entrepot.demo.model.Vehicule;
import entrepot.demo.repository.Livraison_repository;
import entrepot.demo.repository.Statut_vehicule_repository;
import entrepot.demo.repository.Type_vehicule_repository;
import entrepot.demo.repository.Vehicule_repository;

@Service
public class Vehicule_service {

    private final Vehicule_repository repository;
    private final Livraison_repository livraison_repository;
    private final Type_vehicule_repository type_vehicule_repository;
    private final Statut_vehicule_repository statut_vehicule_repository;

    public Vehicule_service(Vehicule_repository repository,
            Livraison_repository livraison_repository,
            Type_vehicule_repository type_vehicule_repository,
            Statut_vehicule_repository statut_vehicule_repository) {

        this.repository = repository;
        this.livraison_repository = livraison_repository;
        this.type_vehicule_repository = type_vehicule_repository;
        this.statut_vehicule_repository = statut_vehicule_repository;
    }

    // Retourner tous les véhicules
    public List<Vehicule> listeVehicules() {
        return repository.findAll();
    }

    // Ajouter un véhicule
    public Vehicule ajouterVehicule(Vehicule vehicule, Long typeVehiculeId, Long statutId) {

        Type_vehicule type = type_vehicule_repository.findById(typeVehiculeId)
                .orElseThrow(() -> new RuntimeException("Type véhicule introuvable"));

        Statut_vehicule statut = statut_vehicule_repository.findById(statutId)
                .orElseThrow(() -> new RuntimeException("Statut véhicule introuvable"));

        vehicule.setTypeVehicule(type);
        vehicule.setStatut(statut);

        return repository.save(vehicule);
    }

    public List<Livraison> findallLivraisons() {
        return livraison_repository.findAll();
    }

    public List<Type_vehicule> finType_vehicules() {
        return type_vehicule_repository.findAll();
    }

    public List<Statut_vehicule> finStatut_vehicules() {
        return statut_vehicule_repository.findAll();
    }
}
