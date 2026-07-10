package com.entrepot.gestion.controller;

import com.entrepot.gestion.model.Chauffeur;
import com.entrepot.gestion.repository.ChauffeurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/chauffeurs")
public class ChauffeurController {

    @Autowired
    private ChauffeurRepository chauffeurRepository;

    @GetMapping
    public String getAllChauffeurs(
            @RequestParam(required = false, defaultValue = "all") String statut,
            @RequestParam(required = false) LocalDate dateDebutExpiration,
            @RequestParam(required = false) LocalDate dateFinExpiration,
            Model model) {
        List<Chauffeur> chauffeurs;

        // Filtrage par statut
        if ("actif".equals(statut)) {
            chauffeurs = chauffeurRepository.findByActif(true);
        } else if ("inactif".equals(statut)) {
            chauffeurs = chauffeurRepository.findByActif(false);
        } else if ("expire".equals(statut)) {
            chauffeurs = chauffeurRepository.findByDateExpirationPermisBefore(LocalDate.now());
        } else {
            chauffeurs = chauffeurRepository.findAll();
        }

        // Filtrage par date d'expiration
        if (dateDebutExpiration != null && dateFinExpiration != null) {
            chauffeurs = chauffeurs.stream()
                    .filter(c -> !c.getDateExpirationPermis().isBefore(dateDebutExpiration) &&
                            !c.getDateExpirationPermis().isAfter(dateFinExpiration))
                    .toList();
        }

        model.addAttribute("chauffeurs", chauffeurs);
        model.addAttribute("statutSelectionne", statut);
        model.addAttribute("dateDebutSelectionnee", dateDebutExpiration);
        model.addAttribute("dateFinSelectionnee", dateFinExpiration);

        return "chauffeurs";
    }

    @GetMapping({ "/ajouter", "/ajouter/" })
    public String showAddForm(Model model) {
        model.addAttribute("chauffeur", new Chauffeur());
        return "formulaireAjoutChauffeurs";
    }

    @PostMapping({ "/ajouter", "/ajouter/" })
    public String addChauffeur(
            @RequestParam String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String telephone,
            @RequestParam String numeroPermis,
            @RequestParam(required = false) LocalDate dateExpirationPermis,
            @RequestParam(defaultValue = "true") boolean actif,
            RedirectAttributes redirectAttributes) {
        try {
            // Vérifier si le numéro de permis existe déjà
            if (chauffeurRepository.existsByNumeroPermis(numeroPermis)) {
                redirectAttributes.addFlashAttribute("error", "Ce numéro de permis existe déjà");
                return "redirect:/chauffeurs/ajouter";
            }

            Chauffeur chauffeur = new Chauffeur(nom, prenom, telephone, numeroPermis,
                    dateExpirationPermis, actif);
            chauffeurRepository.save(chauffeur);
            redirectAttributes.addFlashAttribute("success", "Chauffeur ajouté avec succès");
            return "redirect:/chauffeurs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
            return "redirect:/chauffeurs/ajouter";
        }
    }

    @GetMapping("/{id}/modifier")
    public String showEditForm(@PathVariable Long id, Model model) {
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur non trouvé: " + id));
        model.addAttribute("chauffeur", chauffeur);
        return "formulaireAjoutChauffeurs";
    }

    @PostMapping("/{id}/modifier")
    public String updateChauffeur(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String telephone,
            @RequestParam String numeroPermis,
            @RequestParam(required = false) LocalDate dateExpirationPermis,
            @RequestParam(defaultValue = "true") boolean actif,
            RedirectAttributes redirectAttributes) {
        try {
            Chauffeur chauffeur = chauffeurRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Chauffeur non trouvé: " + id));

            // Vérifier si le numéro de permis existe déjà (pour un autre chauffeur)
            if (!chauffeur.getNumeroPermis().equals(numeroPermis) &&
                    chauffeurRepository.existsByNumeroPermis(numeroPermis)) {
                redirectAttributes.addFlashAttribute("error", "Ce numéro de permis existe déjà");
                return "redirect:/chauffeurs/" + id + "/modifier";
            }

            chauffeur.setNom(nom);
            chauffeur.setPrenom(prenom);
            chauffeur.setTelephone(telephone);
            chauffeur.setNumeroPermis(numeroPermis);
            chauffeur.setDateExpirationPermis(dateExpirationPermis);
            chauffeur.setActif(actif);

            chauffeurRepository.save(chauffeur);
            redirectAttributes.addFlashAttribute("success", "Chauffeur modifié avec succès");
            return "redirect:/chauffeurs";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/chauffeurs/" + id + "/modifier";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String deleteChauffeur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            chauffeurRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Chauffeur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/chauffeurs";
    }
}
