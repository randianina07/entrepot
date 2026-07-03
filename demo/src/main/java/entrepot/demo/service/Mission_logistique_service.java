package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Chauffeurs;
import entrepot.demo.model.Mission_logistique;
import entrepot.demo.model.Statuts_mission;
import entrepot.demo.model.Vehicule;
import entrepot.demo.repository.Chauffeur_repository;
import entrepot.demo.repository.Mission_logistique_repository;
import entrepot.demo.repository.Statuts_mission_repository;
import entrepot.demo.repository.Vehicule_repository;

@Service
public class Mission_logistique_service {
    private final Mission_logistique_repository mission_vehicule_repository;
    private final Vehicule_repository vehicule_repository;
    private final Chauffeur_repository chauffeur_repository;
    private final Statuts_mission_repository statuts_mission_repository;

    public Mission_logistique_service(Mission_logistique_repository mission_vehicule_repository,
            Vehicule_repository vehicule_repository, Chauffeur_repository chauffeur_repository,
            Statuts_mission_repository statuts_mission_repository) {
        this.mission_vehicule_repository = mission_vehicule_repository;
        this.vehicule_repository = vehicule_repository;
        this.chauffeur_repository = chauffeur_repository;
        this.statuts_mission_repository = statuts_mission_repository;
    }

    public List<Mission_logistique> listMission_logistique() {
        return mission_vehicule_repository.findAll();
    }

    public List<Mission_logistique> missionsEnCours() {
        return mission_vehicule_repository.findByStatutMissionIdIn(List.of(1, 2));
    }

    public List<Mission_logistique> historiqueMissions() {
        return mission_vehicule_repository.findByStatutMissionIdIn(List.of(3, 4));
    }

    public Mission_logistique ajouterMission(Mission_logistique mission_logistique, Long vehiculeId, Long chauffeurId,
            Long statutMissionId) {
        Vehicule vehicule = vehicule_repository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Vehicule non trouver"));
        Chauffeurs chauffeurs = chauffeur_repository.findById(chauffeurId)
                .orElseThrow(() -> new RuntimeException("Chauffeur non trouver"));
        Statuts_mission statuts_mission = statuts_mission_repository.findById(statutMissionId)
                .orElseThrow(() -> new RuntimeException("Status non trouver"));
        mission_logistique.setVehicule(vehicule);
        mission_logistique.setChauffeur(chauffeurs);
        mission_logistique.setStatutMission(statuts_mission);

        return mission_vehicule_repository.save(mission_logistique);
    }
}
