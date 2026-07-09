package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardStatsController {

    @GetMapping("/stats")
    public String getDashboardStats(Model model) {
        // Pour l'instant, on retourne la page avec des données factices
        // TODO: Intégrer les vraies données statistiques quand les repositories seront disponibles
        
        // KPIs factices pour démonstration
        model.addAttribute("kpis", new Object());
        model.addAttribute("occupationZones", java.util.Collections.emptyList());
        model.addAttribute("fluxJournaliers", java.util.Collections.emptyList());
        model.addAttribute("financeData", java.util.Collections.emptyList());
        model.addAttribute("performanceLivr", null);
        model.addAttribute("caClients", java.util.Collections.emptyList());
        model.addAttribute("top5Produits", java.util.Collections.emptyList());
        model.addAttribute("periodeAffichee", java.time.LocalDate.now());
        
        return "dashboard/stats";
    }
}
