package com.entrepot.gestion.controller;

import com.entrepot.gestion.model.StatutVehicule;
import com.entrepot.gestion.model.TypeVehicule;
import com.entrepot.gestion.model.Vehicule;
import com.entrepot.gestion.repository.StatutVehiculeRepository;
import com.entrepot.gestion.repository.TypeVehiculeRepository;
import com.entrepot.gestion.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/vehicules")
public class VehiculeController {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private TypeVehiculeRepository typeVehiculeRepository;

    @Autowired
    private StatutVehiculeRepository statutVehiculeRepository;

    @GetMapping("/liste")
    public String listeVehicules(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String immatriculation,
            @RequestParam(required = false) String marque,
            @RequestParam(required = false) String modele,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Double capaciteVolume,
            @RequestParam(required = false) Double capaciteChargekg,
            @RequestParam(required = false) Double kilometrage,
            @RequestParam(required = false) String typeVehiculeId,
            @RequestParam(required = false) String statutId,
            Model model) {

        List<Vehicule> vehicules = vehiculeRepository.findAll();

        // Filtres appliques en memoire (le volume du parc reste faible)
        if (id != null) {
            vehicules = vehicules.stream().filter(v -> v.getId().equals(id)).toList();
        }
        if (immatriculation != null && !immatriculation.isBlank()) {
            String needle = immatriculation.trim().toLowerCase();
            vehicules = vehicules.stream()
                    .filter(v -> v.getImmatriculation() != null && v.getImmatriculation().toLowerCase().contains(needle))
                    .toList();
        }
        if (marque != null && !marque.isBlank()) {
            String needle = marque.trim().toLowerCase();
            vehicules = vehicules.stream()
                    .filter(v -> v.getMarque() != null && v.getMarque().toLowerCase().contains(needle))
                    .toList();
        }
        if (modele != null && !modele.isBlank()) {
            String needle = modele.trim().toLowerCase();
            vehicules = vehicules.stream()
                    .filter(v -> v.getModele() != null && v.getModele().toLowerCase().contains(needle))
                    .toList();
        }
        if (annee != null) {
            vehicules = vehicules.stream().filter(v -> annee.equals(v.getAnnee())).toList();
        }
        if (typeVehiculeId != null && !typeVehiculeId.isBlank()) {
            vehicules = vehicules.stream()
                    .filter(v -> v.getTypeVehicule() != null && typeVehiculeId.equals(v.getTypeVehicule().getId().toString()))
                    .toList();
        }
        if (statutId != null && !statutId.isBlank()) {
            vehicules = vehicules.stream()
                    .filter(v -> v.getStatutVehicule() != null && statutId.equals(v.getStatutVehicule().getId().toString()))
                    .toList();
        }

        model.addAttribute("vehicules", vehicules);
        model.addAttribute("typesVehicule", typeVehiculeRepository.findAll());
        model.addAttribute("statutVehicule", statutVehiculeRepository.findAll());
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

        return "vehicules/liste";
    }

    @GetMapping("/save")
    public String ajouterVehicule(Model model) {
        model.addAttribute("vehicule", new Vehicule());
        model.addAttribute("typesVehicule", typeVehiculeRepository.findAll());
        model.addAttribute("statutVehicule", statutVehiculeRepository.findAll());

        return "vehicules/save";
    }

    @PostMapping("/save")
    public String saveVehicule(
            @RequestParam String immatriculation,
            @RequestParam String marque,
            @RequestParam(required = false) String modele,
            @RequestParam(required = false) Integer annee,
            @RequestParam BigDecimal capaciteVolumeM3,
            @RequestParam BigDecimal capaciteChargeKg,
            @RequestParam(required = false) BigDecimal kilometrageActuel,
            @RequestParam Long typeVehiculeId,
            @RequestParam Long statutId,
            RedirectAttributes redirectAttributes) {

        try {
            if (vehiculeRepository.existsByImmatriculation(immatriculation)) {
                redirectAttributes.addFlashAttribute("error", "Cette immatriculation existe déjà");
                return "redirect:/vehicules/save";
            }

            TypeVehicule typeVehicule = typeVehiculeRepository.findById(typeVehiculeId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de véhicule introuvable"));
            StatutVehicule statutVehicule = statutVehiculeRepository.findById(statutId)
                    .orElseThrow(() -> new IllegalArgumentException("Statut de véhicule introuvable"));

            Vehicule vehicule = new Vehicule(
                    immatriculation, marque, modele, annee,
                    capaciteVolumeM3, capaciteChargeKg,
                    kilometrageActuel != null ? kilometrageActuel : BigDecimal.ZERO,
                    typeVehicule, statutVehicule);

            vehiculeRepository.save(vehicule);
            redirectAttributes.addFlashAttribute("success", "Véhicule ajouté avec succès");
            return "redirect:/vehicules/liste";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
            return "redirect:/vehicules/save";
        }
    }

    @GetMapping("/modifier/{id}")
    public String modifierVehicule(@PathVariable Long id, Model model) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé: " + id));
        model.addAttribute("vehicule", vehicule);
        model.addAttribute("typesVehicule", typeVehiculeRepository.findAll());
        model.addAttribute("statutVehicule", statutVehiculeRepository.findAll());

        return "vehicules/modifier";
    }

    @PostMapping("/modifier/{id}")
    public String updateVehicule(@PathVariable Long id,
            @RequestParam String immatriculation,
            @RequestParam String marque,
            @RequestParam(required = false) String modele,
            @RequestParam(required = false) Integer annee,
            @RequestParam BigDecimal capaciteVolumeM3,
            @RequestParam BigDecimal capaciteChargeKg,
            @RequestParam(required = false) BigDecimal kilometrageActuel,
            @RequestParam Long typeVehiculeId,
            @RequestParam Long statutId,
            RedirectAttributes redirectAttributes) {

        try {
            Vehicule vehicule = vehiculeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé: " + id));

            if (!vehicule.getImmatriculation().equals(immatriculation)
                    && vehiculeRepository.existsByImmatriculation(immatriculation)) {
                redirectAttributes.addFlashAttribute("error", "Cette immatriculation existe déjà");
                return "redirect:/vehicules/modifier/" + id;
            }

            TypeVehicule typeVehicule = typeVehiculeRepository.findById(typeVehiculeId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de véhicule introuvable"));
            StatutVehicule statutVehicule = statutVehiculeRepository.findById(statutId)
                    .orElseThrow(() -> new IllegalArgumentException("Statut de véhicule introuvable"));

            vehicule.setImmatriculation(immatriculation);
            vehicule.setMarque(marque);
            vehicule.setModele(modele);
            vehicule.setAnnee(annee);
            vehicule.setCapaciteVolumeM3(capaciteVolumeM3);
            vehicule.setCapaciteChargeKg(capaciteChargeKg);
            vehicule.setKilometrageActuel(kilometrageActuel != null ? kilometrageActuel : BigDecimal.ZERO);
            vehicule.setTypeVehicule(typeVehicule);
            vehicule.setStatutVehicule(statutVehicule);

            vehiculeRepository.save(vehicule);
            redirectAttributes.addFlashAttribute("success", "Véhicule modifié avec succès");
            return "redirect:/vehicules/liste";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/vehicules/modifier/" + id;
        }
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerVehicule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehiculeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Véhicule supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/vehicules/liste";
    }

    @GetMapping("/historique_vehicule")
    public String historiqueVehicule(@RequestParam(required = false) Long id, Model model) {
        // NOTE: les livraisons et maintenances n'ont pas encore d'entite JPA dans le
        // projet (aucune table "livraisons"/"maintenances" n'est mappee cote Java).
        // On affiche donc uniquement les missions reelles du vehicule; le reste
        // reste a Collections.emptyList() en attendant la creation des entites.
        model.addAttribute("vehicules", vehiculeRepository.findAll());
        model.addAttribute("id", id);

        if (id != null) {
            model.addAttribute("historique", java.util.Collections.emptyList());
        } else {
            model.addAttribute("historique", java.util.Collections.emptyList());
        }
        model.addAttribute("livraisonsVehicule", java.util.Collections.emptyList());
        model.addAttribute("maintenancesVehicule", java.util.Collections.emptyList());

        return "vehicules/historique_vehicule";
    }
}
