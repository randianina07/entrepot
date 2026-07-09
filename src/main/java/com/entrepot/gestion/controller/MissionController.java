package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/missions")
public class MissionController {

    @GetMapping("/save")
    public String gestionMissions(Model model) {
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("missionsEnCours", Collections.emptyList());
        model.addAttribute("historiqueMissions", Collections.emptyList());
        model.addAttribute("vehicule", Collections.emptyList());
        model.addAttribute("chauffeur", Collections.emptyList());
        model.addAttribute("livraison", Collections.emptyList());
        
        return "missions/save";
    }

    @GetMapping("/details")
    public String detailsMission(@RequestParam Long missionId, Model model) {
        // TODO: Charger les détails de la mission
        model.addAttribute("detailMissionId", missionId);
        model.addAttribute("detailLivraisons", Collections.emptyList());
        
        return "forward:/missions/save";
    }

    @PostMapping("/create")
    public String createMission(@RequestParam String dateDepart, @RequestParam String heureDepart,
                               @RequestParam String dateArrivee, @RequestParam String heureArrivee,
                               @RequestParam Long vehiculeId, @RequestParam Long chauffeurId,
                               @RequestParam(required = false) Long[] livraisonIds) {
        // TODO: Créer la mission
        return "redirect:/missions/save";
    }

    @PostMapping("/start")
    public String startMission(@RequestParam Long missionId) {
        // TODO: Démarrer la mission
        return "redirect:/missions/save";
    }

    @PostMapping("/finish")
    public String finishMission(@RequestParam Long missionId) {
        // TODO: Terminer la mission
        return "redirect:/missions/save";
    }

    @PostMapping("/cancel")
    public String cancelMission(@RequestParam Long missionId) {
        // TODO: Annuler la mission
        return "redirect:/missions/save";
    }
}
