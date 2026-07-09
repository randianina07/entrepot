package entrepot.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        public String listeVehicules(
            Model model,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String immatriculation,
            @RequestParam(required = false) String marque,
            @RequestParam(required = false) String modele,
            @RequestParam(required = false) String annee,
            @RequestParam(required = false) String capaciteVolume,
            @RequestParam(required = false) String capaciteChargekg,
            @RequestParam(required = false) String kilometrage,
            @RequestParam(required = false) String typeVehiculeId,
            @RequestParam(required = false) String statutId) {

        List<Vehicule> vehiculesFiltre = new ArrayList<>(service.listeVehicules());
        vehiculesFiltre.removeIf(vehicule -> !matchLong(vehicule.getId(), id)
            || !matchText(vehicule.getImmatriculation(), immatriculation)
            || !matchText(vehicule.getMarque(), marque)
            || !matchText(vehicule.getModele(), modele)
            || !matchInteger(vehicule.getAnnee(), annee)
            || !matchDouble(vehicule.getCapaciteVolume(), capaciteVolume)
            || !matchDouble(vehicule.getCapaciteChargekg(), capaciteChargekg)
            || !matchDouble(vehicule.getKilometrage(), kilometrage)
            || !matchLong(vehicule.getTypeVehicule() != null ? vehicule.getTypeVehicule().getId() : null,
                typeVehiculeId)
            || !matchLong(vehicule.getStatut() != null ? vehicule.getStatut().getId() : null, statutId));

        model.addAttribute("vehicules", vehiculesFiltre);
        model.addAttribute("id", id);
        model.addAttribute("immatriculation", immatriculation);
        model.addAttribute("marque", marque);
        model.addAttribute("modele", modele);
        model.addAttribute("annee", annee);
        model.addAttribute("capaciteVolume", capaciteVolume);
        model.addAttribute("capaciteChargekg", capaciteChargekg);
        model.addAttribute("kilometrage", kilometrage);
        model.addAttribute("typeVehiculeId", typeVehiculeId);
        model.addAttribute("statutId", statutId);
        model.addAttribute("typesVehicule", service.finType_vehicules());
        model.addAttribute("statutVehicule", service.finStatut_vehicules());

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

    @GetMapping("/modifier/{id}")
    public String pageModifierVehicule(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("vehicule", service.findById(id));
            model.addAttribute("typesVehicule", service.finType_vehicules());
            model.addAttribute("statutVehicule", service.finStatut_vehicules());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/vehicules/liste";
        }

        return "vehicules/modifier";
    }

    @PostMapping("/modifier/{id}")
    public String modifierVehicule(
            @PathVariable Long id,
            @ModelAttribute Vehicule vehicule,
            @RequestParam Long typeVehiculeId,
            @RequestParam Long statutId,
            RedirectAttributes redirectAttributes) {

        try {
            service.modifierVehicule(id, vehicule, typeVehiculeId, statutId);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicule modifie avec succes");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addAttribute("id", id);
            return "redirect:/vehicules/modifier/{id}";
        }

        return "redirect:/vehicules/liste";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerVehicule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.supprimerVehicule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicule supprime avec succes");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

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

    private boolean matchText(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private boolean matchLong(Long value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        if (value == null) {
            return false;
        }
        try {
            return value.equals(Long.valueOf(filter));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean matchInteger(int value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        try {
            return value == Integer.parseInt(filter);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean matchDouble(double value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        try {
            return Double.compare(value, Double.parseDouble(filter)) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}