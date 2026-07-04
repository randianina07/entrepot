package entrepot.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entrepot.demo.model.Chauffeurs;
import entrepot.demo.model.Historique_vehicule;
import entrepot.demo.model.Livraison;
import entrepot.demo.model.Mission_logistique;
import entrepot.demo.model.Statut_vehicule;
import entrepot.demo.model.Statuts_mission;
import entrepot.demo.model.Vehicule;
import entrepot.demo.repository.Chauffeur_repository;
import entrepot.demo.repository.Historique_vehicule_repository;
import entrepot.demo.repository.Livraison_repository;
import entrepot.demo.repository.Mission_logistique_repository;
import entrepot.demo.repository.Statut_vehicule_repository;
import entrepot.demo.repository.Statuts_mission_repository;
import entrepot.demo.repository.Vehicule_repository;

@Service
public class Mission_logistique_service {
    private final Mission_logistique_repository mission_vehicule_repository;
    private final Vehicule_repository vehicule_repository;
    private final Chauffeur_repository chauffeur_repository;
    private final Statuts_mission_repository statuts_mission_repository;
    private final Statut_vehicule_repository statut_vehicule_repository;
    private final Livraison_repository livraison_repository;
    private final Historique_vehicule_repository historique_vehicule_repository;

    public Mission_logistique_service(Mission_logistique_repository mission_vehicule_repository,
            Vehicule_repository vehicule_repository, Chauffeur_repository chauffeur_repository,
            Statuts_mission_repository statuts_mission_repository,
            Statut_vehicule_repository statut_vehicule_repository,
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

    public List<Mission_logistique> listMission_logistique() {
        return mission_vehicule_repository.findAll();
    }

    public List<Mission_logistique> missionsEnCours() {
        return mission_vehicule_repository.findByStatutMissionIdIn(List.of(1, 2));
    }

    public List<Mission_logistique> historiqueMissions() {
        return mission_vehicule_repository.findByStatutMissionIdIn(List.of(3, 4));
    }

    public Mission_logistique findById(Long missionId) {
        return mission_vehicule_repository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvee"));
    }

    public List<Vehicule> vehiculesDisponibles() {
        return vehicule_repository.findAll().stream()
                .filter(v -> v.getStatut() != null && "DISPONIBLE".equals(v.getStatut().getCode()))
                .toList();
    }

    public List<Chauffeurs> chauffeursDisponibles() {
        Set<Long> chauffeursActifs = new HashSet<>();
        for (Mission_logistique mission : missionsEnCours()) {
            if (mission.getChauffeur() != null) {
                chauffeursActifs.add(mission.getChauffeur().getId());
            }
        }
        return chauffeur_repository.findAll().stream()
                .filter(c -> !c.isActif())
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
    public Mission_logistique creerMission(LocalDate dateDepart, LocalTime heureDepart, LocalDate dateArrivee,
            LocalTime heureArrivee, Long vehiculeId, Long chauffeurId, List<Long> livraisonIds) {
        if (livraisonIds == null || livraisonIds.isEmpty()) {
            throw new RuntimeException("Selectionnez au moins une livraison");
        }

        Vehicule vehicule = vehicule_repository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Vehicule non trouver"));

        Chauffeurs chauffeurs = chauffeur_repository.findById(chauffeurId)
                .orElseThrow(() -> new RuntimeException("Chauffeur non trouver"));
        if (chauffeurs.isActif()) {
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

        if (volumeCumule > vehicule.getCapaciteVolume()) {
            throw new RuntimeException("Le volume total des livraisons depasse la capacite du vehicule");
        }

        Statuts_mission statutPlanifiee = statuts_mission_repository.findByCode("PLANIFIEE")
                .orElseThrow(() -> new RuntimeException("Statut PLANIFIEE non trouve"));

        Mission_logistique mission = new Mission_logistique();
        mission.setReferenceMission("MS-" + System.currentTimeMillis());
        mission.setDateDepartPrevue(departPrevue);
        mission.setDateArriveePrevue(arriveePrevue);
        mission.setVehicule(vehicule);
        mission.setChauffeur(chauffeurs);
        mission.setStatutMission(statutPlanifiee);

        Mission_logistique missionSauvee = mission_vehicule_repository.save(mission);
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
        Mission_logistique mission = findById(missionId);
        LocalDateTime now = LocalDateTime.now();

        if (mission.getDateDepartPrevue() != null && now.isAfter(mission.getDateDepartPrevue())) {
            throw new RuntimeException("Impossible de commencer apres la date de depart prevue");
        }

        mission.setDateDepartReelle(now);
        mission.setStatutMission(getStatutMissionByCode("EN_COURS"));

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatut(getStatutVehiculeByCode("EN_MISSION"));

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
        historique.setKilometrage_Depart(vehicule.getKilometrage());
        historique_vehicule_repository.save(historique);

        vehicule_repository.save(vehicule);
        mission_vehicule_repository.save(mission);
    }

    @Transactional
    public void terminerMission(Long missionId) {
        Mission_logistique mission = findById(missionId);
        LocalDateTime now = LocalDateTime.now();

        mission.setDateArriveeReelle(now);
        mission.setStatutMission(getStatutMissionByCode("TERMINEE"));

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatut(getStatutVehiculeByCode("DISPONIBLE"));
        Chauffeurs chauffeur = mission.getChauffeur();
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
        historique.setKilometrage_arrivee(vehicule.getKilometrage());
        historique_vehicule_repository.save(historique);

        vehicule_repository.save(vehicule);
        mission_vehicule_repository.save(mission);
    }

    @Transactional
    public void annulerMission(Long missionId) {
        Mission_logistique mission = findById(missionId);
        mission.setStatutMission(getStatutMissionByCode("ANNULEE"));

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatut(getStatutVehiculeByCode("DISPONIBLE"));
        Chauffeurs chauffeur = mission.getChauffeur();
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

    private Statuts_mission getStatutMissionByCode(String code) {
        return statuts_mission_repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Statut mission " + code + " non trouve"));
    }

    private Statut_vehicule getStatutVehiculeByCode(String code) {
        return statut_vehicule_repository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Statut vehicule " + code + " non trouve"));
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
