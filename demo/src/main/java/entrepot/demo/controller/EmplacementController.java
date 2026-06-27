package entrepot.demo.controller;

import entrepot.demo.model.Emplacement;
import entrepot.demo.service.EmplacementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmplacementController {

    private final EmplacementService emplacementService;

    // Spring va injecter automatiquement le service ici
    public EmplacementController(EmplacementService emplacementService) {
        this.emplacementService = emplacementService;
    }

    // URL pour afficher la page de recherche : http://localhost:8080/recherche
    @GetMapping("/recherche")
    public String afficherPageRecherche() {
        return "search"; // Va ouvrir search.jsp
    }

    // URL appelée quand on clique sur le bouton de recherche
    @GetMapping("/faire-recherche")
    public String executerRecherche(@RequestParam("volume") double volume, Model model) {
        
        // 1. On appelle l'algorithme du service
        Emplacement placeTrouvee = emplacementService.trouverPlaceRapide(volume);
        
        // 2. On prépare les données pour la page JSP
        if (placeTrouvee != null) {
            model.addAttribute("resultat", placeTrouvee.getCode());
        } else {
            model.addAttribute("resultat", "Aucun emplacement disponible pour cette taille.");
        }
        
        // On renvoie vers la même page pour afficher le résultat en bas
        return "search";
    }
}