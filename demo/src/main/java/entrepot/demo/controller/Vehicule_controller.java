package entrepot.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entrepot.demo.model.Historique_vehicule;
import entrepot.demo.model.Vehicule;
import entrepot.demo.service.Historique_vehicule_service;
import entrepot.demo.service.Livraison_service;
import entrepot.demo.service.Maintenance_vehicule_service;
import entrepot.demo.service.Vehicule_service;

@Controller
@RequestMapping("/vehicules")
public class Vehicule_controller {

    private final Vehicule_service service;
    private final Historique_vehicule_service historique_vehicule_service;
    private final Livraison_service livraison_service;
    private final Maintenance_vehicule_service maintenance_vehicule_service;

    public Vehicule_controller(Vehicule_service service,
            Historique_vehicule_service historique_vehicule_service,
            Livraison_service livraison_service,
            Maintenance_vehicule_service maintenance_vehicule_service) {
        this.service = service;
        this.historique_vehicule_service = historique_vehicule_service;
        this.livraison_service = livraison_service;
        this.maintenance_vehicule_service = maintenance_vehicule_service;
    }
    @GetMapping("/liste")
    public String listeVehicules(Model model) {

        model.addAttribute(
                "vehicules",
                service.listeVehicules());

        return "vehicules/liste";
    }

    @GetMapping("/save")
    public String pageVehicules(Model model) {

        model.addAttribute("vehicule", new Vehicule());
        model.addAttribute("vehicules", service.listeVehicules());
        model.addAttribute("typesVehicule", service.finType_vehicules());
        model.addAttribute("statutVehicule", service.finStatut_vehicules());

        return "vehicules/save";
    }

    @PostMapping("/save")
    public String saveVehicules(
            @ModelAttribute Vehicule vehicule,
            @RequestParam Long typeVehiculeId,
            @RequestParam Long statutId) {

        service.ajouterVehicule(vehicule, typeVehiculeId, statutId);

        return "redirect:/vehicules/liste";
    }

    @GetMapping("/historique_vehicule")
    public String historiqueVehicule(@RequestParam Long id, Model model) {

        List<Historique_vehicule> historique = historique_vehicule_service.findByVehiculeId(id);

        model.addAttribute("historique", historique);
        model.addAttribute("livraisonsVehicule", livraison_service.findByVehiculeId(id));
        model.addAttribute("maintenancesVehicule", maintenance_vehicule_service.findByVehiculeId(id));

        return "vehicules/historique_vehicule";
    }

    @GetMapping("/mission_vehicule")
    public String missionVehicule(){
        return "vehicules/mission_vehicule";
    }
}