package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/accueil")
    public String accueil() {
        return "Emplacement/accueil";
    }

    @GetMapping("/historiqueMaintenances")
    public String legacyHistoriqueMaintenances() {
        return "redirect:/maintenances";
    }

    @GetMapping("/formulaireAjoutMaintenances")
    public String legacyFormulaireAjoutMaintenances() {
        return "redirect:/maintenances/ajouter";
    }

    @GetMapping("/formulaireAjoutChauffeurs")
    public String legacyFormulaireAjoutChauffeurs() {
        return "redirect:/chauffeurs/ajouter";
    }
}
