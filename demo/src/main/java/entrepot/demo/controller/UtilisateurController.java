package entrepot.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entrepot.demo.model.Utilisateur;
import entrepot.demo.model.UtilisateurInfo;
import entrepot.demo.service.UtilisateurService;

@Controller
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/utilisateurs/nouveau")
    public String afficherFormulaire(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("utilisateurInfo", new UtilisateurInfo());

        return "utilisateur/formulaire";
    }

    @PostMapping("/utilisateurs/enregistrer")
    public String enregistrerClient(
            @ModelAttribute Utilisateur utilisateur,
            @ModelAttribute UtilisateurInfo utilisateurInfo,
            @RequestParam String roleCode,
            Model model) {

        try {

            String motDePasse = utilisateurService.creerUtilisateur(
                    utilisateur,
                    utilisateurInfo,
                    roleCode);

            model.addAttribute("message",
                    "Utilisateur créé avec succès.");

            model.addAttribute("motDePasse",
                    motDePasse);

            return "client/succes";

        } catch (Exception e) {

            model.addAttribute("erreur",
                    e.getMessage());

            return "utilisateur/formulaire";
        }
    }

    @GetMapping("/clients")
    public String listeClients(Model model) {
        model.addAttribute("clients", utilisateurService.listeClients());

        return "client/liste";
    }

    @GetMapping("/clients/supprimer/{id}")
    public String supprimerClient(@PathVariable Long id) {

        utilisateurService.supprimerClient(id);

        return "redirect:/clients";
    }

    @GetMapping("/clients/modifier/{id}")
    public String afficherModification(@PathVariable Long id, Model model) {

        UtilisateurInfo client = utilisateurService.trouverClient(id);

        model.addAttribute("client", client);

        return "client/modifier";
    }

    @PostMapping("/clients/modifier/{id}")
    public String modifierClient(
            @PathVariable Long id,
            @ModelAttribute("client") UtilisateurInfo client) {

        utilisateurService.modifierClient(id, client);

        return "redirect:/clients";
    }
}
