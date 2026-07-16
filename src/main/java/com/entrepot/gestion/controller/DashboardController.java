package com.entrepot.gestion.controller;

import com.entrepot.gestion.dto.MouvementListDTO;
import com.entrepot.gestion.service.MouvementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private MouvementService mouvementService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistiques du jour
        LocalDateTime debutJour = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        long entreesAujourdhui = mouvementService.countMouvementsByTypeAndDate("ENTREE", debutJour, finJour);
        long sortiesAujourdhui = mouvementService.countMouvementsByTypeAndDate("SORTIE", debutJour, finJour);
        long enAttenteValidation = mouvementService.countMouvementsByStatut("BROUILLON");

        model.addAttribute("entreesAujourdhui", entreesAujourdhui);
        model.addAttribute("sortiesAujourdhui", sortiesAujourdhui);
        model.addAttribute("enAttenteValidation", enAttenteValidation);

        // 5 derniers mouvements
        List<MouvementListDTO> derniersMouvements = mouvementService.getDerniersMouvements(5);
        model.addAttribute("derniersMouvements", derniersMouvements);

        // Alertes stock bas
        model.addAttribute("alertesStock", mouvementService.getEmplacementssature());

        return "dashboard";
    }
}
