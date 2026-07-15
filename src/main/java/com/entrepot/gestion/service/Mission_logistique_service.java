package com.entrepot.gestion.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entrepot.gestion.model.Chauffeur;
import com.entrepot.gestion.model.Historique_vehicule;
import com.entrepot.gestion.model.Livraison;
import com.entrepot.gestion.model.MissionLogistique;
import com.entrepot.gestion.model.StatutMission;
import com.entrepot.gestion.model.StatutVehicule;
import com.entrepot.gestion.model.Vehicule;
import com.entrepot.gestion.repository.ChauffeurRepository;
import com.entrepot.gestion.repository.Historique_vehicule_repository;
import com.entrepot.gestion.repository.Livraison_repository;
import com.entrepot.gestion.repository.MissionLogistiqueRepository;
import com.entrepot.gestion.repository.StatutMissionRepository;
import com.entrepot.gestion.repository.StatutVehiculeRepository;
import com.entrepot.gestion.repository.VehiculeRepository;


@Service
public class Mission_logistique_service {
    private final MissionLogistiqueRepository mission_vehicule_repository;
    private final VehiculeRepository vehicule_repository;
    private final ChauffeurRepository chauffeur_repository;
    private final StatutMissionRepository statuts_mission_repository;
    private final StatutVehiculeRepository statut_vehicule_repository;
    private final Livraison_repository livraison_repository;
    private final Historique_vehicule_repository historique_vehicule_repository;

    public Mission_logistique_service(MissionLogistiqueRepository mission_vehicule_repository,
            VehiculeRepository vehicule_repository, ChauffeurRepository chauffeur_repository,
            StatutMissionRepository statuts_mission_repository,
            StatutVehiculeRepository statut_vehicule_repository,
            Livraison_repository livraison_repository,
            Historique_vehicule_repository historique_vehicule_repository) {
        this.mission_vehicule_repository = mission_vehicule_repository;
        this.vehicule_repository = vehicule_repository;
        this.chauffeur_repository = chauffeur_repository;
        this.statuts_mission_repository = statuts_mission_repository;
        this.statut_vehicule_repository = statut_vehicule_repository;
        this.livraison_repository = livraison_repository;
        this.historique_vehicule_repository = historique_vehicule_repository;
    }

    public List<MissionLogistique> listMission_logistique() {
        return mission_vehicule_repository.findAll();
    }

    public List<MissionLogistique> missionsEnCours() {
        return mission_vehicule_repository.findByStatutMissionIdIn(List.of(1, 2));
    }

    public List<MissionLogistique> historiqueMissions() {
        return mission_vehicule_repository.findByStatutMissionIdIn(List.of(3, 4));
    }

    public MissionLogistique findById(Long missionId) {
        return mission_vehicule_repository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvee"));
    }

    public List<Vehicule> vehiculesDisponibles() {
        return vehicule_repository.findAll().stream()
                .filter(v -> v.getStatutVehicule() != null && "DISPONIBLE".equals(v.getStatutVehicule().getCode()))
                .toList();
    }

    public List<Chauffeur> chauffeursDisponibles() {
        Set<Long> chauffeursActifs = new HashSet<>();
        for (MissionLogistique mission : missionsEnCours()) {
            if (mission.getChauffeur() != null) {
                chauffeursActifs.add(mission.getChauffeur().getId());
            }
        }
        return chauffeur_repository.findAll().stream()
                .filter(c -> !c.getActif())
                .filter(c -> !chauffeursActifs.contains(c.getId()))
                .toList();
    }

    public List<Livraison> livraisonsDisponiblesPourMission() {
        return livraison_repository.findByMissionStatutMissionCodeAndDateLivraisonIsNull("ANNULEE");
    }

    public List<Livraison> detailsMission(Long missionId) {
        return livraison_repository.findByMissionId(missionId);
    }

    @Transactional
    public MissionLogistique creerMission(LocalDate dateDepart, LocalTime heureDepart, LocalDate dateArrivee,
            LocalTime heureArrivee, Long vehiculeId, Long chauffeurId, List<Long> livraisonIds) {
        if (livraisonIds == null || livraisonIds.isEmpty()) {
            throw new RuntimeException("Selectionnez au moins une livraison");
        }

        Vehicule vehicule = vehicule_repository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Vehicule non trouver"));

        Chauffeur chauffeurs = chauffeur_repository.findById(chauffeurId)
                .orElseThrow(() -> new RuntimeException("Chauffeur non trouver"));
        if (chauffeurs.getActif()) {
            throw new RuntimeException("Le chauffeur est deja occupe");
        }

        LocalDateTime departPrevue = LocalDateTime.of(dateDepart, heureDepart);
        LocalDateTime arriveePrevue = LocalDateTime.of(dateArrivee, heureArrivee);
        if (arriveePrevue.isBefore(departPrevue)) {
            throw new RuntimeException("La date d'arrivee prevue doit etre apres la date de depart prevue");
        }

        List<Livraison> livraisons = livraison_repository.findAllById(livraisonIds);
        if (livraisons.size() != livraisonIds.size()) {
            throw new RuntimeException("Certaines livraisons sont introuvables");
        }

        LocalDate dateReference = null;
        double volumeCumule = 0.0;

        for (Livraison livraison : livraisons) {
            if (livraison.getDateLivraison() != null) {
                throw new RuntimeException("Une livraison deja livree ne peut pas etre reassignee");
            }

            if (livraison.getMission() != null
                    && livraison.getMission().getStatutMission() != null
                    && !"ANNULEE".equals(livraison.getMission().getStatutMission().getCode())) {
                throw new RuntimeException("Une livraison est deja affectee a une mission active");
            }

            if (livraison.getDatePrevue() == null) {
                throw new RuntimeException("Chaque livraison doit avoir une date prevue configuree");
            }

            LocalDate datePrevue = livraison.getDatePrevue().toLocalDate();
            if (dateReference == null) {
                dateReference = datePrevue;
            } else if (!dateReference.equals(datePrevue)) {
                throw new RuntimeException("Les livraisons selectionnees doivent avoir la meme date prevue");
            }

            volumeCumule += livraison.getVolumeTotal() == null ? 0.0 : livraison.getVolumeTotal();
        }

        BigDecimal volumecumuleBD = BigDecimal.valueOf(volumeCumule);

        if (volumecumuleBD.compareTo(vehicule.getCapaciteVolumeM3()) > 0) {
            throw new RuntimeException("Le volume total des livraisons depasse la capacite du vehicule");
        }

        StatutMission statutPlanifiee = statuts_mission_repository.findByCode("PLANIFIEE");

        MissionLogistique mission = new MissionLogistique();
        mission.setReferenceMission("MS-" + System.currentTimeMillis());
        mission.setDateDepartPrevue(departPrevue);
        mission.setDateArriveePrevue(arriveePrevue);
        mission.setVehicule(vehicule);
        mission.setChauffeur(chauffeurs);
        mission.setStatutMission(statutPlanifiee);

        MissionLogistique missionSauvee = mission_vehicule_repository.save(mission);
        chauffeurs.setActif(true);
        chauffeur_repository.save(chauffeurs);

        for (Livraison livraison : livraisons) {
            livraison.setMission(missionSauvee);
            livraison_repository.save(livraison);
        }

        return missionSauvee;
    }

    @Transactional
    public void commencerMission(Long missionId) {
        MissionLogistique mission = findById(missionId);
        LocalDateTime now = LocalDateTime.now();

        if (mission.getDateDepartPrevue() != null && now.isAfter(mission.getDateDepartPrevue())) {
            throw new RuntimeException("Impossible de commencer apres la date de depart prevue");
        }

        mission.setDateDepartReelle(now);
        mission.setStatutMission(getStatutMissionByCode("EN_COURS"));

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatutVehicule(getStatutVehiculeByCode("EN_MISSION"));

        for (Livraison livraison : livraison_repository.findByMissionId(missionId)) {
            if (livraison.getDateLivraison() == null) {
                livraison.setDateLivraison(now);
                livraison_repository.save(livraison);
            }
        }

        Historique_vehicule historique = historique_vehicule_repository.findByMissionId(missionId)
                .orElseGet(Historique_vehicule::new);
        historique.setMission(mission);
        historique.setVehicule(vehicule);
        historique.setDate_depart(now);
        historique.setKilometrage_Depart(vehicule.getKilometrageActuel());
        historique_vehicule_repository.save(historique);

        vehicule_repository.save(vehicule);
        mission_vehicule_repository.save(mission);
    }

    @Transactional
    public void terminerMission(Long missionId) {
        MissionLogistique mission = findById(missionId);
        LocalDateTime now = LocalDateTime.now();

        mission.setDateArriveeReelle(now);
        mission.setStatutMission(getStatutMissionByCode("TERMINEE"));

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatutVehicule(getStatutVehiculeByCode("DISPONIBLE"));
        Chauffeur chauffeur = mission.getChauffeur();
        if (chauffeur != null) {
            chauffeur.setActif(false);
            chauffeur_repository.save(chauffeur);
        }

        Historique_vehicule historique = historique_vehicule_repository.findByMissionId(missionId)
                .orElseGet(Historique_vehicule::new);
        historique.setMission(mission);
        historique.setVehicule(vehicule);
        if (historique.getDate_depart() == null) {
            historique.setDate_depart(mission.getDateDepartReelle());
        }
        historique.setDate_arrivee(now);
        historique.setKilometrage_arrivee(vehicule.getKilometrageActuel());
        historique_vehicule_repository.save(historique);

        vehicule_repository.save(vehicule);
        mission_vehicule_repository.save(mission);
    }

    @Transactional
    public void annulerMission(Long missionId) {
        MissionLogistique mission = findById(missionId);
        mission.setStatutMission(getStatutMissionByCode("ANNULEE"));

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatutVehicule(getStatutVehiculeByCode("DISPONIBLE"));
        Chauffeur chauffeur = mission.getChauffeur();
        if (chauffeur != null) {
            chauffeur.setActif(false);
            chauffeur_repository.save(chauffeur);
        }

        for (Livraison livraison : livraison_repository.findByMissionId(missionId)) {
            livraison.setDateLivraison(null);
            livraison_repository.save(livraison);
        }

        vehicule_repository.save(vehicule);
        mission_vehicule_repository.save(mission);
    }

    private StatutMission getStatutMissionByCode(String code) {
        return statuts_mission_repository.findByCode(code);
    }

    private StatutVehicule getStatutVehiculeByCode(String code) {
        return statut_vehicule_repository.findByCode(code);
    }

    public MissionLogistique ajouterMission(MissionLogistique mission_logistique, Long vehiculeId, Long chauffeurId,
            Long statutMissionId) {
        Vehicule vehicule = vehicule_repository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Vehicule non trouver"));
        Chauffeur chauffeurs = chauffeur_repository.findById(chauffeurId)
                .orElseThrow(() -> new RuntimeException("Chauffeur non trouver"));
        StatutMission statuts_mission = statuts_mission_repository.findById(statutMissionId)
                .orElseThrow(() -> new RuntimeException("Status non trouver"));
        mission_logistique.setVehicule(vehicule);
        mission_logistique.setChauffeur(chauffeurs);
        mission_logistique.setStatutMission(statuts_mission);

        return mission_vehicule_repository.save(mission_logistique);
    }
}
