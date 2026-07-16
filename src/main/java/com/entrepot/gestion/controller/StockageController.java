package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StockageController {

    @GetMapping("/stockage/visualisation")
    public String visualisation(Model model) {
        return "redirect:/choose-type_zones";
    }

    @GetMapping("/stockage/emplacements")
    public String emplacements(Model model) {
        return "redirect:/choose-type_zones";
    }

    @GetMapping("/stockage/stocks")
    public String stocks(Model model) {
        return "redirect:/accueil";
    }

    @GetMapping("/stockage/recherche")
    public String recherche(Model model) {
        return "redirect:/recherche";
    }
}
