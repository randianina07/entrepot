package entrepot.demo.controller;

import entrepot.demo.entity.Chauffeur;
import entrepot.demo.service.Chauffeur_service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/chauffeurs")
public class ChauffeurController {

    private final Chauffeur_service chauffeurService;

    public ChauffeurController(Chauffeur_service chauffeurService) {
        this.chauffeurService = chauffeurService;
    }

    @GetMapping
        public String getAllChauffeurs(
            @RequestParam(required = false, defaultValue = "all") String statut,
            @RequestParam(required = false) LocalDate dateDebutExpiration,
            @RequestParam(required = false) LocalDate dateFinExpiration,
            Model model
        ) {
        String statutFiltre = statut == null || statut.isBlank() ? "all" : statut;
        List<Chauffeur> chauffeurs = chauffeurService.getChauffeurs(
            statutFiltre,
            dateDebutExpiration,
            dateFinExpiration
        );

        model.addAttribute("chauffeurs", chauffeurs);
        model.addAttribute("statutSelectionne", statutFiltre);
        model.addAttribute("dateDebutSelectionnee", dateDebutExpiration);
        model.addAttribute("dateFinSelectionnee", dateFinExpiration);

        return "chauffeurs";
        }

    @GetMapping({"/ajouter", "/ajouter/"})
    public String showAddForm() {
        return "formulaireAjoutChauffeurs";
    }

    @PostMapping({"/ajouter", "/ajouter/"})
    public String addChauffeur(
            @RequestParam String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String telephone,
            @RequestParam String numeroPermis,
            @RequestParam LocalDate dateExpirationPermis,
            @RequestParam(defaultValue = "false") boolean actif
    ) {
        Chauffeur chauffeur = new Chauffeur(null, nom, prenom, telephone, numeroPermis, dateExpirationPermis, actif);
        chauffeurService.addChauffeur(chauffeur);
        return "redirect:/chauffeurs";
    }
}
