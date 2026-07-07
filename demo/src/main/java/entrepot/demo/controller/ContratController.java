package entrepot.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

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
    public String create(
            @RequestParam(required = false) Long clientId,
            Model model) {
        List<Utilisateur> clients = utilisateurService.listeClientsUtilisateur();

        model.addAttribute("contrat", new Contrat());
        model.addAttribute("clients", clients);
        model.addAttribute("typesContrat", typeContratService.findAll()
        );

        if(clientId != null){
            Utilisateur client = utilisateurService.findById(clientId);
            model.addAttribute("clientSelectionne", client);
        }
        return "contrats/create";
    }



    @PostMapping("/create")
    public String create(
            @ModelAttribute Contrat contrat,
            @RequestParam Long utilisateurId,
            @RequestParam Long typeContratId) {


        Utilisateur utilisateur = utilisateurService.findById(utilisateurId);
        TypeContrat typeContrat = typeContratService.findById(typeContratId).orElseThrow();

        contrat.setUtilisateur(utilisateur);
        contrat.setTypeContrat(typeContrat);
        contrat.setDateCreation(LocalDateTime.now());

        contratService.save(contrat);

        return "redirect:/contrats/create";

    }

}
