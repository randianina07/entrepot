package entrepot.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import entrepot.demo.model.Livraison;
import entrepot.demo.service.Livraison_service;
import entrepot.demo.service.Mode_calcule_livraison_service;
import entrepot.demo.service.Tarif_livraison_service;
import entrepot.demo.service.Vehicule_service;

@Controller
@RequestMapping("/livraisons")
public class Livraison_controller {
    private final Vehicule_service vehicule_service;
    private final Livraison_service livraison_service;
    private final Tarif_livraison_service tarif_livraison_service;
    private final Mode_calcule_livraison_service mode_calcule_livraison_service;

    public Livraison_controller(Vehicule_service vehicule_service , Livraison_service livraison_service , Tarif_livraison_service tarif_livraison_service , Mode_calcule_livraison_service mode_calcule_livraison_service){
        this.vehicule_service = vehicule_service;
        this.livraison_service = livraison_service;
        this.tarif_livraison_service = tarif_livraison_service;
        this.mode_calcule_livraison_service = mode_calcule_livraison_service;
    }

    @GetMapping("/livraison")
    public String livraisonVehicule(Model model) {

        List<Livraison> listLivraison = vehicule_service.findallLivraisons();
        model.addAttribute("listeLivraison", listLivraison);
        return "livraisons/livraison";
    }

    @GetMapping("/config_livraison")
    public String configurationLivraison(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) {
        Livraison livraison;
        try {
            livraison = livraison_service.findById(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/livraisons/livraison";
        }

        model.addAttribute("mode_calcule" , mode_calcule_livraison_service.modeCalculfindAll());

        model.addAttribute("object_livraison", livraison);
        model.addAttribute("tarif_livraison" , tarif_livraison_service.listeTarif());
        model.addAttribute("livraison", livraison);
        return "livraisons/config_livraison";
    }

    @PostMapping("/config_livraison")
    public String save_configuration(
            @RequestParam Long livraisonId,
            @RequestParam Long tarifId,
            @RequestParam LocalDate datePrevue,
            @RequestParam LocalTime heurePrevue,
            RedirectAttributes redirectAttributes) {
        try {
            livraison_service.configurerLivraison(livraisonId, tarifId, datePrevue, heurePrevue);
            redirectAttributes.addFlashAttribute("successMessage", "Livraison configuree");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addAttribute("id", livraisonId);
            return "redirect:/livraisons/config_livraison";
        }
        return "redirect:/livraisons/livraison";
    }

}
