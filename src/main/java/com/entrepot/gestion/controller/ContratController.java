package com.entrepot.gestion.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.DemandeRenouvellement;
import com.entrepot.gestion.model.DemandeStockage;
import com.entrepot.gestion.model.HistoriqueEtatDemande;
import com.entrepot.gestion.model.HistoriqueRenouvellement;
import com.entrepot.gestion.model.StatutDemandeStockage;
import com.entrepot.gestion.model.StatutRenouvellement;
import com.entrepot.gestion.model.TypeContrat;
import com.entrepot.gestion.model.TypeZone;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.service.ContratService;
import com.entrepot.gestion.service.DemandeRenouvellementService;
import com.entrepot.gestion.service.DemandeStockageService;
import com.entrepot.gestion.service.HistoriqueEtatDemandeService;
import com.entrepot.gestion.service.HistoriqueRenouvellementService;
import com.entrepot.gestion.service.StatutDemandeStockageService;
import com.entrepot.gestion.service.StatutRenouvellementService;
import com.entrepot.gestion.service.TypeContratService;
import com.entrepot.gestion.service.TypeZoneService;
import com.entrepot.gestion.service.UtilisateurService;

@Controller
@RequestMapping("/contrats")
public class ContratController {

    private final ContratService contratService;
    private final TypeContratService typeContratService;
    private final UtilisateurService utilisateurService;
    private final DemandeStockageService demandeStockageService;
    private final TypeZoneService typeZoneService;
    private final StatutDemandeStockageService statutDemandeStockageService;
    private final HistoriqueEtatDemandeService historiqueEtatDemandeService;
    private final DemandeRenouvellementService demandeRenouvellementService;
    private final StatutRenouvellementService statutRenouvellementService;
    private final HistoriqueRenouvellementService historiqueRenouvellementService;

    public ContratController(
            ContratService contratService,
            TypeContratService typeContratService,
            UtilisateurService utilisateurService,
            DemandeStockageService demandeStockageService,
            TypeZoneService typeZoneService,
            StatutDemandeStockageService statutDemandeStockageService,
            HistoriqueEtatDemandeService historiqueEtatDemandeService,
            DemandeRenouvellementService demandeRenouvellementService,
            StatutRenouvellementService statutRenouvellementService,
            HistoriqueRenouvellementService historiqueRenouvellementService) {

        this.contratService = contratService;
        this.typeContratService = typeContratService;
        this.utilisateurService = utilisateurService;
        this.demandeStockageService = demandeStockageService;
        this.typeZoneService = typeZoneService;
        this.statutDemandeStockageService = statutDemandeStockageService;
        this.historiqueEtatDemandeService = historiqueEtatDemandeService;
        this.demandeRenouvellementService = demandeRenouvellementService;
        this.statutRenouvellementService = statutRenouvellementService;
        this.historiqueRenouvellementService = historiqueRenouvellementService;
    }

    @GetMapping("/create")
    public String create(
            @RequestParam(required = false) Long clientId,
            Model model) {
        List<Utilisateur> clients = utilisateurService.listeClientsUtilisateur();

        model.addAttribute("contrat", new Contrat());
        model.addAttribute("clients", clients);
        model.addAttribute("typesContrat", typeContratService.findAll());

        if (clientId != null) {
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

    @GetMapping("/demande")
    public String afficherDemandeStockage(Model model) {
        model.addAttribute("demande", new DemandeStockage());
        model.addAttribute("typesZone", typeZoneService.findAll());
        model.addAttribute("typesContrat", typeContratService.findAll());
        return "contrats/demande";
    }

    @PostMapping("/demande")
    public String enregistrerDemande(
            @ModelAttribute DemandeStockage demande,
            @RequestParam Long typeZoneId,
            @RequestParam Long typeContratId) {
        // indice 1 lony fa refaveo atao contexte
        Utilisateur client = utilisateurService.findById(1L);
        TypeZone typeZone = typeZoneService.findById(typeZoneId);
        TypeContrat typeContrat = typeContratService.findById(typeContratId).orElseThrow();

        demande.setUtilisateur(client);
        demande.setTypeZone(typeZone);
        demande.setTypeContrat(typeContrat);

        DemandeStockage demandeSauvee = demandeStockageService.save(demande);
        StatutDemandeStockage enAttente = statutDemandeStockageService.findByCode("EN_ATTENTE").orElseThrow();
        HistoriqueEtatDemande historique = new HistoriqueEtatDemande();

        historique.setDemandeStockage(demandeSauvee);
        historique.setStatut(enAttente);
        historique.setDateStatut(LocalDateTime.now());

        historiqueEtatDemandeService.save(historique);

        return "redirect:/contrats/demande?success";
    }

    @GetMapping("/demandes")
    public String listeDemandes(Model model) {
        List<DemandeStockage> demandes = demandeStockageService.findAll();
        List<DemandeStockage> attente = demandes.stream().filter(d -> historiqueEtatDemandeService
            .dernierStatut(d).getStatut().getCode().equals("EN_ATTENTE")).toList();

        model.addAttribute("demandes", attente);
        return "contrats/demandes";
    }

    @GetMapping("/demande/accepter/{id}")
    public String accepter(
            @PathVariable Long id
    ) {

        contratService.accepterDemande(id);

        return "redirect:/contrats/demandes";
    }

    @GetMapping("/demande/refuser/{id}")
    public String refuser(
            @PathVariable Long id
    ) {
        DemandeStockage demande = demandeStockageService.findById(id).orElseThrow();
        StatutDemandeStockage statut =statutDemandeStockageService.findByCode("REFUSEE").orElseThrow();
        HistoriqueEtatDemande historique = new HistoriqueEtatDemande();

        historique.setDemandeStockage(demande);
        historique.setStatut(statut);
        historique.setDateStatut(LocalDateTime.now());

        historiqueEtatDemandeService.save(historique);
        return "redirect:/contrats/demandes";
    }

    @GetMapping("/renouvellement")
    public String afficherDemandeRenouvellement(Model model) {

        // TODO : remplacer par les contrats du client connecté
        Utilisateur client = utilisateurService.findById(1L);
        List<Contrat> contrats = contratService.findByUtilisateur(client);

        model.addAttribute("demande", new DemandeRenouvellement());
        model.addAttribute("contrats", contrats);

        return "contrats/renouvellement";
    }

    @PostMapping("/renouvellement")
    public String enregistrerDemandeRenouvellement(
            @ModelAttribute DemandeRenouvellement demande,
            @RequestParam Long contratId) {

        Contrat contrat = contratService.findById(contratId).orElseThrow();

        demande.setContrat(contrat);
        demande.setDateDemande(LocalDate.now());

        DemandeRenouvellement demandeSauvee = demandeRenouvellementService.save(demande);

        StatutRenouvellement enAttente = statutRenouvellementService.findByCode("EN_ATTENTE").orElseThrow();

        HistoriqueRenouvellement historique = new HistoriqueRenouvellement();

        historique.setDemandeRenouvellement(demandeSauvee);
        historique.setStatutRenouvellement(enAttente);
        historique.setDateStatut(LocalDateTime.now());

        historiqueRenouvellementService.save(historique);

        return "redirect:/contrats/renouvellement?success";
    }
    
    @GetMapping("/renouvellements")
    public String listeDemandesRenouvellement(Model model) {

        List<DemandeRenouvellement> demandes = demandeRenouvellementService.findAll();

        List<DemandeRenouvellement> attente = demandes.stream()
            .filter(d -> historiqueRenouvellementService
                .dernierStatut(d)
                .getStatutRenouvellement()
                .getCode()
                .equals("EN_ATTENTE")
            ).toList();
        model.addAttribute("demandes", attente);

        return "contrats/renouvellements";
    }

    @GetMapping("/renouvellement/accepter/{id}")
    public String accepterRenouvellement(
            @PathVariable Long id) {

        contratService.accepterRenouvellement(id);

        return "redirect:/contrats/renouvellements";
    }

    @GetMapping("/renouvellement/refuser/{id}")
    public String refuserRenouvellement(
            @PathVariable Long id) {

        DemandeRenouvellement demande = demandeRenouvellementService.findById(id) .orElseThrow();
        StatutRenouvellement refuse = statutRenouvellementService.findByCode("REFUSEE").orElseThrow();
        HistoriqueRenouvellement historique = new HistoriqueRenouvellement();

        historique.setDemandeRenouvellement(demande);
        historique.setStatutRenouvellement(refuse);
        historique.setDateStatut(LocalDateTime.now());

        historiqueRenouvellementService.save(historique);

        return "redirect:/contrats/renouvellements";
    }
}
