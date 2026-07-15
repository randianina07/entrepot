package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Livraison;
import com.entrepot.gestion.model.StatutVehicule;
import com.entrepot.gestion.model.TypeVehicule;
import com.entrepot.gestion.model.Vehicule;
import com.entrepot.gestion.repository.Livraison_repository;
import com.entrepot.gestion.repository.StatutVehiculeRepository;
import com.entrepot.gestion.repository.TypeVehiculeRepository;
import com.entrepot.gestion.repository.VehiculeRepository;



@Service
public class Vehicule_service {

    private final VehiculeRepository repository;
    private final Livraison_repository livraison_repository;
    private final TypeVehiculeRepository type_vehicule_repository;
    private final StatutVehiculeRepository statut_vehicule_repository;

    public Vehicule_service(VehiculeRepository repository,
            Livraison_repository livraison_repository,
            TypeVehiculeRepository type_vehicule_repository,
            StatutVehiculeRepository statut_vehicule_repository) {

        this.repository = repository;
        this.livraison_repository = livraison_repository;
        this.type_vehicule_repository = type_vehicule_repository;
        this.statut_vehicule_repository = statut_vehicule_repository;
    }

    // Retourner tous les véhicules
    public List<Vehicule> listeVehicules() {
        return repository.findAll();
    }

    public Vehicule findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
    }

    // Ajouter un véhicule
    public Vehicule ajouterVehicule(Vehicule vehicule, Long typeVehiculeId, Long statutId) {

        TypeVehicule type = type_vehicule_repository.findById(typeVehiculeId)
                .orElseThrow(() -> new RuntimeException("Type véhicule introuvable"));

        StatutVehicule statut = statut_vehicule_repository.findById(statutId)
                .orElseThrow(() -> new RuntimeException("Statut véhicule introuvable"));

        vehicule.setTypeVehicule(type);
        vehicule.setStatutVehicule(statut);

        return repository.save(vehicule);
    }

    public Vehicule modifierVehicule(Long id, Vehicule vehicule, Long typeVehiculeId, Long statutId) {

        Vehicule vehiculeExistant = findById(id);

        TypeVehicule type = type_vehicule_repository.findById(typeVehiculeId)
                .orElseThrow(() -> new RuntimeException("Type véhicule introuvable"));

        StatutVehicule statut = statut_vehicule_repository.findById(statutId)
                .orElseThrow(() -> new RuntimeException("Statut véhicule introuvable"));

        vehiculeExistant.setImmatriculation(vehicule.getImmatriculation());
        vehiculeExistant.setMarque(vehicule.getMarque());
        vehiculeExistant.setModele(vehicule.getModele());
        vehiculeExistant.setAnnee(vehicule.getAnnee());
        vehiculeExistant.setCapaciteVolumeM3(vehicule.getCapaciteVolumeM3());
        vehiculeExistant.setCapaciteChargeKg(vehicule.getCapaciteChargeKg());
        vehiculeExistant.setKilometrageActuel(vehicule.getKilometrageActuel());
        vehiculeExistant.setTypeVehicule(type);
        vehiculeExistant.setStatutVehicule(statut);

        return repository.save(vehiculeExistant);
    }

    public void supprimerVehicule(Long id) {
        Vehicule vehicule = findById(id);
        repository.delete(vehicule);
    }

    public List<Livraison> findallLivraisons() {
        return livraison_repository.findAll();
    }

    public List<TypeVehicule> finType_vehicules() {
        return type_vehicule_repository.findAll();
    }

    public List<StatutVehicule> finStatut_vehicules() {
        return statut_vehicule_repository.findAll();
    }
}
