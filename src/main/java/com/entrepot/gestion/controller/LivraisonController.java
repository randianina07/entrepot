package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/livraisons")
public class LivraisonController {

    @GetMapping("/livraison")
    public String listeLivraisons(Model model) {
        // TODO: Intégrer les vraies données quand le repository sera disponible
        model.addAttribute("listeLivraison", Collections.emptyList());
        
        return "livraisons/livraison";
    }

    @GetMapping("/config_livraison")
    public String configLivraison(@RequestParam Long id, Model model) {
        // TODO: Charger la livraison à configurer
        model.addAttribute("livraison", new Object());
        model.addAttribute("tarif_livraison", Collections.emptyList());
        model.addAttribute("mode_calcule", Collections.emptyList());
        
        return "livraisons/config_livraison";
    }

    @PostMapping("/config_livraison")
    public String saveConfigLivraison(@RequestParam Long livraisonId, @RequestParam Long tarifId) {
        // TODO: Sauvegarder la configuration de livraison
        return "redirect:/livraisons/livraison";
    }
}
