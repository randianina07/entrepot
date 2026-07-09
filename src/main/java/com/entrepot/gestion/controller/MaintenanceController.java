package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/maintenances")
public class MaintenanceController {

    @GetMapping
    public String listeMaintenances(
            @RequestParam(required = false) Long typeMaintenanceId,
            @RequestParam(required = false) String dateDebutMaintenance,
            @RequestParam(required = false) String dateFinMaintenance,
            Model model) {
        
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("maintenances", Collections.emptyList());
        model.addAttribute("typesMaintenances", Collections.emptyList());
        model.addAttribute("typeMaintenanceIdSelectionne", typeMaintenanceId);
        model.addAttribute("dateDebutMaintenanceSelectionnee", dateDebutMaintenance);
        model.addAttribute("dateFinMaintenanceSelectionnee", dateFinMaintenance);
        
        return "historiqueMaintenances";
    }

    @GetMapping("/ajouter")
    public String ajouterMaintenance(Model model) {
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("vehicules", Collections.emptyList());
        model.addAttribute("typesMaintenances", Collections.emptyList());
        
        return "formulaireAjoutMaintenances";
    }

    @PostMapping("/ajouter")
    public String saveMaintenance(@RequestParam Long vehiculeId, @RequestParam Long typeMaintenanceId,
                                 @RequestParam String dateMaintenance, @RequestParam(required = false) Double cout,
                                 @RequestParam(required = false) Double kilometrage,
                                 @RequestParam(required = false) String description) {
        // TODO: Sauvegarder la maintenance
        return "redirect:/maintenances";
    }
}
