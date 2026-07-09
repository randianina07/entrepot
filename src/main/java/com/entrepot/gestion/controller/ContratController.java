package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;

@Controller
@RequestMapping("/contrats")
public class ContratController {

    @GetMapping("/create")
    public String create(
            @RequestParam(required = false) Long clientId,
            Model model) {
        // TODO: Intégrer les vraies données quand le service sera disponible
        model.addAttribute("contrat", new Object());
        model.addAttribute("clients", Collections.emptyList());
        model.addAttribute("typesContrat", Collections.emptyList());
        
        if (clientId != null) {
            model.addAttribute("clientSelectionne", new Object());
        }
        return "contrats/create";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute Object contrat,
            @RequestParam Long utilisateurId,
            @RequestParam Long typeContratId) {
        // TODO: Sauvegarder le contrat avec le service
        return "redirect:/contrats/create";
    }

    @GetMapping("/demande")
    public String afficherDemandeStockage(Model model) {
        model.addAttribute("demande", new Object());
        model.addAttribute("typesZone", Collections.emptyList());
        model.addAttribute("typesContrat", Collections.emptyList());
        return "contrats/demande";
    }

    @PostMapping("/demande")
    public String enregistrerDemande(
            @ModelAttribute Object demande,
            @RequestParam Long typeZoneId,
            @RequestParam Long typeContratId) {
        // TODO: Sauvegarder la demande avec le service
        return "redirect:/contrats/demande?success";
    }

    @GetMapping("/demandes")
    public String listeDemandes(Model model) {
        // TODO: Intégrer les vraies données quand le service sera disponible
        model.addAttribute("demandes", Collections.emptyList());
        return "contrats/demandes";
    }

    @GetMapping("/nouvelle-demande")
    public String nouvelleDemande() {
        return "redirect:/contrats/demande";
    }

    @GetMapping("/demande/accepter/{id}")
    public String accepter(@PathVariable Long id) {
        // TODO: Accepter la demande avec le service
        return "redirect:/contrats/demandes";
    }

    @GetMapping("/demande/refuser/{id}")
    public String refuser(@PathVariable Long id) {
        // TODO: Refuser la demande avec le service
        return "redirect:/contrats/demandes";
    }

    @GetMapping("/renouvellement")
    public String afficherDemandeRenouvellement(Model model) {
        // TODO: Intégrer les vraies données quand le service sera disponible
        model.addAttribute("demande", new Object());
        model.addAttribute("contrats", Collections.emptyList());
        return "contrats/renouvellement";
    }

    @PostMapping("/renouvellement")
    public String enregistrerDemandeRenouvellement(
            @ModelAttribute Object demande,
            @RequestParam Long contratId) {
        // TODO: Sauvegarder la demande de renouvellement avec le service
        return "redirect:/contrats/renouvellement?success";
    }

    @GetMapping("/renouvellements")
    public String listeDemandesRenouvellement(Model model) {
        // TODO: Intégrer les vraies données quand le service sera disponible
        model.addAttribute("demandes", Collections.emptyList());
        return "contrats/renouvellements";
    }

    @GetMapping("/renouvellement/accepter/{id}")
    public String accepterRenouvellement(@PathVariable Long id) {
        // TODO: Accepter le renouvellement avec le service
        return "redirect:/contrats/renouvellements";
    }

    @GetMapping("/renouvellement/refuser/{id}")
    public String refuserRenouvellement(@PathVariable Long id) {
        // TODO: Refuser le renouvellement avec le service
        return "redirect:/contrats/renouvellements";
    }
}
