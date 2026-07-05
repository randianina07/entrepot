package entrepot.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import entrepot.demo.model.Utilisateur;
import entrepot.demo.model.UtilisateurInfo;
import entrepot.demo.service.UtilisateurService;

@Controller
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/clients/nouveau")
    public String afficherFormulaire(Model model){
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("utilisateurInfo", new UtilisateurInfo());

        return "client/formulaire";
    }

    @PostMapping
    public String enregistrerClient(Utilisateur utilisateur, UtilisateurInfo utilisateurInfo, Model model){
        try {
            String motDePasse = utilisateurService.creerClient(utilisateur, utilisateurInfo);

            model.addAttribute("message", "Le client a été créé avec succès.");
            model.addAttribute("motDepasse", motDePasse);

            return "client/succes";
        } catch (Exception e) {
            // TODO: handle exception
            model.addAttribute("erreur", e.getMessage());

            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("utilisateurInfo", utilisateurInfo);

            return "client/formulaire";
        }
    }
}
