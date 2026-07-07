package entrepot.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.HistoriqueEtatDemande;
import entrepot.demo.model.StatutDemandeStockage;
import entrepot.demo.model.TypeContrat;
import entrepot.demo.model.TypeZone;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.service.ContratService;
import entrepot.demo.service.DemandeStockageService;
import entrepot.demo.service.HistoriqueEtatDemandeService;
import entrepot.demo.service.StatutDemandeStockageService;
import entrepot.demo.service.TypeContratService;
import entrepot.demo.service.TypeZoneService;
import entrepot.demo.service.UtilisateurService;

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

    public ContratController(
            ContratService contratService,
            TypeContratService typeContratService,
            UtilisateurService utilisateurService,
            DemandeStockageService demandeStockageService,
            TypeZoneService typeZoneService,
            StatutDemandeStockageService statutDemandeStockageService,
            HistoriqueEtatDemandeService historiqueEtatDemandeService) {

        this.contratService = contratService;
        this.typeContratService = typeContratService;
        this.utilisateurService = utilisateurService;
        this.demandeStockageService = demandeStockageService;
        this.typeZoneService = typeZoneService;
        this.statutDemandeStockageService = statutDemandeStockageService;
        this.historiqueEtatDemandeService = historiqueEtatDemandeService;
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
        TypeZone typeZone = typeZoneService.findById(typeZoneId).orElseThrow();
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
}
