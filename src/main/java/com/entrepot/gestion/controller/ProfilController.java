package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.model.UtilisateurInfo;
import com.entrepot.gestion.service.UtilisateurService;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    private final UtilisateurService utilisateurService;

    public ProfilController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // Affichage du profil utilisateur connecté
    @GetMapping
    public String afficherProfil(Model model) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurConnecte();

        UtilisateurInfo info = utilisateurService.getProfil();

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("profil", info);

        return "profil/index";
    }

    // Afficher le formulaire de changement de mot de passe
    @GetMapping("/motDePasse")
    public String afficherFormulaireMotDePasse(Model model) {
        return "profil/changerMotDePasse";
    }

    // traitement du changement de mot de passe
    @PostMapping("/motDePasse")
    public String changerMotDePasse(@RequestParam(required = false) String ancienMotDePasse,
            @RequestParam String nouveauMotDePasse,
            @RequestParam String confirmationMotDePasse,
            Model model) {

        // vérification confirmation
        if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
            model.addAttribute("erreur", "les mots de passe ne correspondent pas");

            return "profil/changerMotDePasse";
        }

        try {

            Utilisateur utilisateur = utilisateurService.getUtilisateurConnecte();

            boolean verifierAncien = !utilisateur.getRole().getCode().equals("ADMIN");

            utilisateurService.changerMotDePasse(ancienMotDePasse, nouveauMotDePasse, verifierAncien);

            model.addAttribute("message", "mot de passe modifié avec succès");
        } catch (Exception e) {

            // TODO: handle exception
            model.addAttribute(
                    "erreur",
                    e.getMessage());

            return "profil/changerMotDePasse";
        }
        return "profil/changerMotDePasse";
    }
}
