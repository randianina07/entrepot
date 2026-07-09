package entrepot.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.ui.Model;
import entrepot.demo.model.Mission_logistique;
import entrepot.demo.service.Mission_logistique_service;
import entrepot.demo.service.Statuts_mission_service;

@Controller
@RequestMapping("/missions")
public class Mission_controller {
    private final Mission_logistique_service mission_vehicule_service;
    private final Statuts_mission_service statuts_mission_service;

    public Mission_controller(Mission_logistique_service mission_vehicule_service,
            Statuts_mission_service statuts_mission_service) {
        this.mission_vehicule_service = mission_vehicule_service;
        this.statuts_mission_service = statuts_mission_service;
    }

    @GetMapping("/save")
    public String ajouteMission(@RequestParam(required = false) Long detailMissionId, Model model ){

        model.addAttribute("mission_logistique" , new Mission_logistique());
        model.addAttribute("vehicule" , mission_vehicule_service.vehiculesDisponibles());
        model.addAttribute("chauffeur" , mission_vehicule_service.chauffeursDisponibles());
        model.addAttribute("Statuts_mission" , statuts_mission_service.listStatuts_missions());
        model.addAttribute("missionsEnCours", mission_vehicule_service.missionsEnCours());
        model.addAttribute("historiqueMissions", mission_vehicule_service.historiqueMissions());
        model.addAttribute("livraison" , mission_vehicule_service.livraisonsDisponiblesPourMission());

        if (detailMissionId != null) {
            model.addAttribute("detailMissionId", detailMissionId);
            model.addAttribute("detailLivraisons", mission_vehicule_service.detailsMission(detailMissionId));
        }

        return "missions/save";
    }

    @PostMapping("/create")
    public String createMission(
            @RequestParam LocalDate dateDepart,
            @RequestParam LocalTime heureDepart,
            @RequestParam LocalDate dateArrivee,
            @RequestParam LocalTime heureArrivee,
            @RequestParam Long vehiculeId,
            @RequestParam Long chauffeurId,
            @RequestParam(name = "livraisonIds", required = false) List<Long> livraisonIds,
            RedirectAttributes redirectAttributes) {
        try {
            mission_vehicule_service.creerMission(
                    dateDepart,
                    heureDepart,
                    dateArrivee,
                    heureArrivee,
                    vehiculeId,
                    chauffeurId,
                    livraisonIds);
            redirectAttributes.addFlashAttribute("successMessage", "Mission creee avec statut PLANIFIEE");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/missions/save";
    }

    @PostMapping("/start")
    public String startMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        try {
            mission_vehicule_service.commencerMission(missionId);
            redirectAttributes.addFlashAttribute("successMessage", "Mission demarree");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/missions/save";
    }

    @PostMapping("/finish")
    public String finishMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        try {
            mission_vehicule_service.terminerMission(missionId);
            redirectAttributes.addFlashAttribute("successMessage", "Mission terminee");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/missions/save";
    }

    @PostMapping("/cancel")
    public String cancelMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        try {
            mission_vehicule_service.annulerMission(missionId);
            redirectAttributes.addFlashAttribute("successMessage", "Mission annulee");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/missions/save";
    }

    @GetMapping("/details")
    public String detailsMission(@RequestParam Long missionId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("detailMissionId", missionId);
        return "redirect:/missions/save";
    }
}
