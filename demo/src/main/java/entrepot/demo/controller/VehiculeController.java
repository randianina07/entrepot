package entrepot.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import entrepot.demo.service.VehiculeService;

@Controller
public class VehiculeController {

    private final VehiculeService service;

    public VehiculeController(VehiculeService service) {
        this.service = service;
    }

    @GetMapping("/vehicules")
    public String listeVehicules(Model model) {

        model.addAttribute(
                "vehicules",
                service.listeVehicules());

        return "vehicules";
    }
}