package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/vehicules")
public class VehiculeController {

    @GetMapping("/liste")
    public String listeVehicules(
            @RequestParam(required = false) String immatriculation,
            @RequestParam(required = false) String marque,
            @RequestParam(required = false) String typeVehiculeId,
            @RequestParam(required = false) String statutId,
            Model model) {
        
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("vehicules", Collections.emptyList());
        model.addAttribute("typesVehicule", Collections.emptyList());
        model.addAttribute("statutVehicule", Collections.emptyList());
        model.addAttribute("immatriculation", immatriculation);
        model.addAttribute("marque", marque);
        model.addAttribute("typeVehiculeId", typeVehiculeId);
        model.addAttribute("statutId", statutId);
        
        return "vehicules/liste";
    }

    @GetMapping("/save")
    public String ajouterVehicule(Model model) {
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("vehicule", new Object());
        model.addAttribute("typesVehicule", Collections.emptyList());
        model.addAttribute("statutVehicule", Collections.emptyList());
        
        return "vehicules/save";
    }

    @PostMapping("/save")
    public String saveVehicule(@ModelAttribute Object vehicule) {
        // TODO: Sauvegarder le véhicule
        return "redirect:/vehicules/liste";
    }

    @GetMapping("/modifier/{id}")
    public String modifierVehicule(@PathVariable Long id, Model model) {
        // TODO: Charger le véhicule à modifier
        model.addAttribute("vehicule", new Object());
        model.addAttribute("typesVehicule", Collections.emptyList());
        model.addAttribute("statutVehicule", Collections.emptyList());
        
        return "vehicules/modifier";
    }

    @PostMapping("/modifier/{id}")
    public String updateVehicule(@PathVariable Long id, @ModelAttribute Object vehicule) {
        // TODO: Mettre à jour le véhicule
        return "redirect:/vehicules/liste";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerVehicule(@PathVariable Long id) {
        // TODO: Supprimer le véhicule
        return "redirect:/vehicules/liste";
    }

    @GetMapping("/historique_vehicule")
    public String historiqueVehicule(@RequestParam(required = false) Long id, Model model) {
        // TODO: Charger l'historique du véhicule
        model.addAttribute("historique", Collections.emptyList());
        model.addAttribute("livraisonsVehicule", Collections.emptyList());
        model.addAttribute("maintenancesVehicule", Collections.emptyList());
        model.addAttribute("vehicules", Collections.emptyList());
        
        return "vehicules/historique_vehicule";
    }
}
