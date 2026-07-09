package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@RequestMapping("/chauffeurs")
public class ChauffeurController {

    @GetMapping
    public String listeChauffeurs(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String dateDebutExpiration,
            @RequestParam(required = false) String dateFinExpiration,
            Model model) {
        
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("chauffeurs", Collections.emptyList());
        model.addAttribute("statutSelectionne", statut);
        model.addAttribute("dateDebutSelectionnee", dateDebutExpiration);
        model.addAttribute("dateFinSelectionnee", dateFinExpiration);
        
        return "chauffeurs";
    }

    @GetMapping("/ajouter")
    public String ajouterChauffeur(Model model) {
        // TODO: Créer le formulaire d'ajout
        return "chauffeurs"; // Temporaire
    }
}
