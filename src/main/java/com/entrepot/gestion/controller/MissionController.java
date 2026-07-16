package com.entrepot.gestion.controller;

import com.entrepot.gestion.model.Chauffeur;
import com.entrepot.gestion.model.MissionLogistique;
import com.entrepot.gestion.model.StatutMission;
import com.entrepot.gestion.model.Vehicule;
import com.entrepot.gestion.repository.ChauffeurRepository;
import com.entrepot.gestion.repository.MissionLogistiqueRepository;
import com.entrepot.gestion.repository.StatutMissionRepository;
import com.entrepot.gestion.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/missions")
public class MissionController {

    private static final List<String> STATUTS_EN_COURS = List.of("PLANIFIEE", "EN_COURS");
    private static final List<String> STATUTS_TERMINES = List.of("TERMINEE", "ANNULEE");

    @Autowired
    private MissionLogistiqueRepository missionLogistiqueRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private ChauffeurRepository chauffeurRepository;

    @Autowired
    private StatutMissionRepository statutMissionRepository;

    private final JdbcTemplate jdbcTemplate;

    public MissionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/save")
    public String gestionMissions(
            @RequestParam(required = false) Long detailMissionId,
            Model model) {

        List<MissionLogistique> toutesLesMissions = missionLogistiqueRepository.findAll();

        List<MissionLogistique> missionsEnCours = toutesLesMissions.stream()
                .filter(m -> m.getStatutMission() != null && STATUTS_EN_COURS.contains(m.getStatutMission().getCode()))
                .toList();
        List<MissionLogistique> historiqueMissions = toutesLesMissions.stream()
                .filter(m -> m.getStatutMission() != null && STATUTS_TERMINES.contains(m.getStatutMission().getCode()))
                .toList();

        model.addAttribute("missionsEnCours", missionsEnCours);
        model.addAttribute("historiqueMissions", historiqueMissions);

        // Vehicules et chauffeurs disponibles pour la creation d'une mission
        model.addAttribute("vehicule", vehiculeRepository.findByStatutVehiculeCode("DISPONIBLE"));
        model.addAttribute("chauffeur", chauffeurRepository.findByActif(true));

        // Livraisons à inclure
        List<Map<String, Object>> livraisons = jdbcTemplate.queryForList("""
                SELECT l.id,
                       u.email AS client_email,
                       l.adresse_livraison,
                       zl.commune AS zone_commune,
                       l.poids_total,
                       l.volume_total,
                       l.date_prevue,
                       to_char(l.date_prevue, 'DD/MM/YYYY HH24:MI') AS date_prevue_fmt,
                       l.mission_id
                FROM livraisons l
                JOIN utilisateurs u ON u.id = l.client_id
                LEFT JOIN zones_livraison zl ON zl.id = l.zone_livraison_id
                ORDER BY l.id DESC
                """);

        model.addAttribute("livraison", livraisons);
        model.addAttribute("detailMissionId", detailMissionId);

        List<Map<String, Object>> detailLivraisons = List.of();
        if (detailMissionId != null) {
            detailLivraisons = jdbcTemplate.queryForList("""
                    SELECT l.id,
                           u.email AS client_email,
                           l.adresse_livraison,
                           zl.commune AS zone_commune,
                           l.volume_total,
                           l.date_prevue,
                           l.date_livraison,
                           to_char(l.date_prevue, 'DD/MM/YYYY HH24:MI') AS date_prevue_fmt,
                           CASE WHEN l.date_livraison IS NULL THEN '-' ELSE to_char(l.date_livraison, 'DD/MM/YYYY HH24:MI') END AS date_livraison_fmt
                    FROM livraisons l
                    JOIN utilisateurs u ON u.id = l.client_id
                    LEFT JOIN zones_livraison zl ON zl.id = l.zone_livraison_id
                    WHERE l.mission_id = ?
                    ORDER BY l.id DESC
                    """, detailMissionId);
        }
        model.addAttribute("detailLivraisons", detailLivraisons);

        return "missions/save";
    }

    @GetMapping("/details")
    public String detailsMission(@RequestParam Long missionId) {
        return "redirect:/missions/save?detailMissionId=" + missionId;
    }

    @PostMapping("/create")
    public String createMission(@RequestParam String dateDepart, @RequestParam String heureDepart,
                               @RequestParam String dateArrivee, @RequestParam String heureArrivee,
                               @RequestParam Long vehiculeId, @RequestParam Long chauffeurId,
                               @RequestParam(required = false) Long[] livraisonIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                    .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable"));
            Chauffeur chauffeur = chauffeurRepository.findById(chauffeurId)
                    .orElseThrow(() -> new IllegalArgumentException("Chauffeur introuvable"));
            StatutMission statutPlanifiee = statutMissionRepository.findByCode("PLANIFIEE");
            if (statutPlanifiee == null) {
                throw new IllegalStateException("Statut PLANIFIEE introuvable");
            }

            LocalDateTime dateDepartPrevue = LocalDate.parse(dateDepart).atTime(LocalTime.parse(heureDepart));
            LocalDateTime dateArriveePrevue = LocalDate.parse(dateArrivee).atTime(LocalTime.parse(heureArrivee));

            MissionLogistique mission = new MissionLogistique();
            mission.setReferenceMission("MIS-" + System.currentTimeMillis());
            mission.setDateDepartPrevue(dateDepartPrevue);
            mission.setDateArriveePrevue(dateArriveePrevue);
            mission.setVehicule(vehicule);
            mission.setChauffeur(chauffeur);
            mission.setStatutMission(statutPlanifiee);

            mission = missionLogistiqueRepository.save(mission);

            if (livraisonIds != null) {
                for (Long livraisonId : livraisonIds) {
                    jdbcTemplate.update("UPDATE livraisons SET mission_id = ? WHERE id = ?", mission.getId(), livraisonId);
                }
            }

            redirectAttributes.addFlashAttribute("success", "✅ Mission créée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de créer la mission: " + e.getMessage());
        }
        return "redirect:/missions/save";
    }

    @PostMapping("/start")
    public String startMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        updateMissionStatut(missionId, "EN_COURS", redirectAttributes);
        return "redirect:/missions/save";
    }

    @PostMapping("/finish")
    public String finishMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        try {
            MissionLogistique mission = missionLogistiqueRepository.findById(missionId)
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
            StatutMission statutTerminee = statutMissionRepository.findByCode("TERMINEE");
            mission.setStatutMission(statutTerminee);
            mission.setDateArriveeReelle(LocalDateTime.now());
            missionLogistiqueRepository.save(mission);
            redirectAttributes.addFlashAttribute("success", "✅ Mission terminée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de terminer la mission: " + e.getMessage());
        }
        return "redirect:/missions/save";
    }

    @PostMapping("/cancel")
    public String cancelMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        updateMissionStatut(missionId, "ANNULEE", redirectAttributes);
        return "redirect:/missions/save";
    }

    private void updateMissionStatut(Long missionId, String code, RedirectAttributes redirectAttributes) {
        try {
            MissionLogistique mission = missionLogistiqueRepository.findById(missionId)
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
            StatutMission statut = statutMissionRepository.findByCode(code);
            if (statut == null) {
                throw new IllegalStateException("Statut " + code + " introuvable");
            }
            mission.setStatutMission(statut);
            if ("EN_COURS".equals(code)) {
                mission.setDateDepartReelle(LocalDateTime.now());
            }
            missionLogistiqueRepository.save(mission);
            String action = "EN_COURS".equals(code) ? "démarrée" : "mise à jour";
            if ("ANNULEE".equals(code)) {
                action = "annulée";
            }
            redirectAttributes.addFlashAttribute("success", "✅ Mission " + action + " avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de mettre à jour la mission: " + e.getMessage());
        }
    }
}
