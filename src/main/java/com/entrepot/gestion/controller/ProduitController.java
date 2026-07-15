package com.entrepot.gestion.controller;

import com.entrepot.gestion.model.Produit;
import com.entrepot.gestion.model.TypeProduit;
import com.entrepot.gestion.repository.ProduitRepository;
import com.entrepot.gestion.repository.TypeProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private TypeProduitRepository typeProduitRepository;

    @GetMapping("/liste")
    public String listeProduits(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String typeProduitId,
            @RequestParam(required = false) String actif,
            Model model) {

        List<Produit> produits = produitRepository.findAll();

        if (code != null && !code.isBlank()) {
            String needle = code.trim().toLowerCase();
            produits = produits.stream()
                    .filter(p -> p.getCode() != null && p.getCode().toLowerCase().contains(needle))
                    .toList();
        }
        if (nom != null && !nom.isBlank()) {
            String needle = nom.trim().toLowerCase();
            produits = produits.stream()
                    .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(needle))
                    .toList();
        }
        if (typeProduitId != null && !typeProduitId.isBlank()) {
            produits = produits.stream()
                    .filter(p -> p.getTypeProduit() != null && typeProduitId.equals(p.getTypeProduit().getId().toString()))
                    .toList();
        }
        if (actif != null && !actif.isBlank()) {
            boolean actifBool = Boolean.parseBoolean(actif);
            produits = produits.stream()
                    .filter(p -> p.getActif() != null && p.getActif() == actifBool)
                    .toList();
        }

        model.addAttribute("produits", produits);
        model.addAttribute("typesProduit", typeProduitRepository.findAll());
        model.addAttribute("code", code);
        model.addAttribute("nom", nom);
        model.addAttribute("typeProduitId", typeProduitId);
        model.addAttribute("actif", actif);

        return "produits/liste";
    }

    @GetMapping("/save")
    public String ajouterProduit(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("typesProduit", typeProduitRepository.findAll());
        return "produits/save";
    }

    @PostMapping("/save")
    public String saveProduit(
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam Long typeProduitId,
            @RequestParam BigDecimal volumeUnitaireM3,
            @RequestParam(required = false) BigDecimal poidsUnitaireKg,
            @RequestParam(required = false, defaultValue = "true") Boolean actif,
            RedirectAttributes redirectAttributes) {

        try {
            if (produitRepository.existsByCode(code)) {
                redirectAttributes.addFlashAttribute("error", "Ce code produit existe déjà");
                return "redirect:/produits/save";
            }

            TypeProduit typeProduit = typeProduitRepository.findById(typeProduitId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de produit introuvable"));

            Produit produit = new Produit(
                    null, code, nom, description, typeProduit,
                    volumeUnitaireM3, poidsUnitaireKg, actif);

            produitRepository.save(produit);
            redirectAttributes.addFlashAttribute("success", "Produit ajouté avec succès");
            return "redirect:/produits/liste";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
            return "redirect:/produits/save";
        }
    }

    @GetMapping("/modifier/{id}")
    public String modifierProduit(@PathVariable Long id, Model model) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + id));
        model.addAttribute("produit", produit);
        model.addAttribute("typesProduit", typeProduitRepository.findAll());
        return "produits/modifier";
    }

    @PostMapping("/modifier/{id}")
    public String updateProduit(@PathVariable Long id,
            @RequestParam String code,
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam Long typeProduitId,
            @RequestParam BigDecimal volumeUnitaireM3,
            @RequestParam(required = false) BigDecimal poidsUnitaireKg,
            @RequestParam(required = false, defaultValue = "false") Boolean actif,
            RedirectAttributes redirectAttributes) {

        try {
            Produit produit = produitRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé: " + id));

            if (!produit.getCode().equals(code) && produitRepository.existsByCode(code)) {
                redirectAttributes.addFlashAttribute("error", "Ce code produit existe déjà");
                return "redirect:/produits/modifier/" + id;
            }

            TypeProduit typeProduit = typeProduitRepository.findById(typeProduitId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de produit introuvable"));

            produit.setCode(code);
            produit.setNom(nom);
            produit.setDescription(description);
            produit.setTypeProduit(typeProduit);
            produit.setVolumeUnitaireM3(volumeUnitaireM3);
            produit.setPoidsUnitaireKg(poidsUnitaireKg);
            produit.setActif(actif);

            produitRepository.save(produit);
            redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès");
            return "redirect:/produits/liste";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/produits/modifier/" + id;
        }
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerProduit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer ce produit: " + e.getMessage());
        }
        return "redirect:/produits/liste";
    }
}
