package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogistiqueController {

    @GetMapping("/logistique/vehicules")
    public String vehicules() {
        return "redirect:/vehicules/liste";
    }

    @GetMapping("/logistique/chauffeurs")
    public String chauffeurs() {
        return "redirect:/chauffeurs";
    }

    @GetMapping("/logistique/missions")
    public String missions() {
        return "redirect:/missions/save";
    }

    @GetMapping("/logistique/livraisons")
    public String livraisons() {
        return "redirect:/livraisons/livraison";
    }

    @GetMapping("/logistique/maintenances")
    public String maintenances() {
        return "redirect:/maintenances";
    }
}
