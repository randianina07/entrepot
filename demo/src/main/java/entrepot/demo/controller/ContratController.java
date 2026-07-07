package entrepot.demo.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.TypeContrat;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.service.ContratService;
import entrepot.demo.service.TypeContratService;
import entrepot.demo.service.UtilisateurService;

@Controller
@RequestMapping("/contrats")
public class ContratController {

    private final ContratService contratService;
    private final TypeContratService typeContratService;
    private final UtilisateurService utilisateurService;

    public ContratController(
            ContratService contratService,
            TypeContratService typeContratService,
            UtilisateurService utilisateurService) {

        this.contratService = contratService;
        this.typeContratService = typeContratService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/create")
    public String create(Model model) {

        model.addAttribute("contrat", new Contrat());
        model.addAttribute("typesContrat", typeContratService.findAll());

        return "contrats/create";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute Contrat contrat,
            @RequestParam("typeContrat") Long typeContratId) {

        Utilisateur utilisateur = utilisateurService.findById(1L).orElseThrow();

        TypeContrat typeContrat = typeContratService.findById(typeContratId).orElseThrow();

        contrat.setUtilisateur(utilisateur);
        contrat.setTypeContrat(typeContrat);
        contrat.setDateCreation(LocalDateTime.now());

        contratService.save(contrat);

        return "redirect:/contrats/create";
    }

}