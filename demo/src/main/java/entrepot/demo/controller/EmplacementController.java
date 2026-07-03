package entrepot.demo.controller;

import entrepot.demo.model.Emplacement;
import entrepot.demo.service.EmplacementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import entrepot.demo.service.TypeZoneService;
import entrepot.demo.model.TypeZone;

@Controller
public class EmplacementController {
    
    private TypeZoneService typeZoneService;
    private final EmplacementService emplacementService;

    // Spring va injecter automatiquement le service ici
    public EmplacementController(EmplacementService emplacementService) {
        this.emplacementService = emplacementService;
    }

    // URL pour afficher la page de recherche : http://localhost:8080/recherche
    @GetMapping("/recherche")
    public String afficherPageRecherche(Model model) {
        TypeZoneService typeZoneService = new TypeZoneService();
        List<TypeZone> listTypeZone = typeZoneService.getAll();

        if (listTypeZone != null) {
            model.addAttribute("listeZone" , listTypeZone);
        } else {
            model.addAttribute("listeZone", new ArrayList<>());
        }

        return "search"; // Va ouvrir search.jsp
    }

    // URL appelée quand on clique sur le bouton de recherche
    @GetMapping("/faire-recherche")
    public String executerRecherche(@RequestParam("quantite") int quantite ,@RequestParam("volume") double volume, Model model) {
        
        // 1. On appelle l'algorithme du service
        List<Emplacement> placeTrouvee = emplacementService.trouverPlaceRapide(volume,quantite);
        
        // 2. On prépare les données pour la page JSP
        if (placeTrouvee.isEmpty()) {
            model.addAttribute("erreur", "Aucun emplacement disponible pour " + volume + " en m3 , avec " + quantite + " comme quantite" );
        } else {
            model.addAttribute("resultat", placeTrouvee);
        }
        
        // On renvoie vers la même page pour afficher le résultat en bas
        return "search";
    }
}