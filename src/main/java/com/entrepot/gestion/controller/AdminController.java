package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/utilisateurs")
    public String utilisateurs() {
        return "redirect:/utilisateurs/nouveau";
    }

    @GetMapping("/admin/roles")
    public String roles() {
        return "redirect:/dashboard";
    }

    @GetMapping("/admin/produits")
    public String produits() {
        return "redirect:/dashboard";
    }

    @GetMapping("/admin/types-zone")
    public String typesZone() {
        return "redirect:/choose-type_zones";
    }

    @GetMapping("/admin/statistiques")
    public String statistiques() {
        return "redirect:/dashboard/stats";
    }
}
