package entrepot.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entrepot.demo.model.Livraison;
import entrepot.demo.service.Livraison_service;
import entrepot.demo.service.Vehicule_service;

@Controller
@RequestMapping("/livraisons")
public class Livraison_controller {
    private final Vehicule_service vehicule_service;
    private final Livraison_service livraison_service;

    public Livraison_controller(Vehicule_service vehicule_service , Livraison_service livraison_service){
        this.vehicule_service = vehicule_service;
        this.livraison_service = livraison_service;
    }

    @GetMapping("/livraison")
    public String livraisonVehicule(Model model) {

        List<Livraison> listLivraison = vehicule_service.findallLivraisons();
        model.addAttribute("listeLivraison", listLivraison);
        return "livraisons/livraison";
    }

    @GetMapping("/config_livraison")
    public String configurationLivraison(@RequestParam Long id, Model model) {
        Livraison livraison = livraison_service.findById(id);

        model.addAttribute("livraison", livraison);
        return "livraisons/config_livraison";
    }
}
