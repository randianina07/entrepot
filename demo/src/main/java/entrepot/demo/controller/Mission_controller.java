package entrepot.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import entrepot.demo.model.Mission_logistique;
import entrepot.demo.service.Chauffeur_service;
import entrepot.demo.service.Mission_logistique_service;
import entrepot.demo.service.Statuts_mission_service;
import entrepot.demo.service.Vehicule_service;

@Controller
@RequestMapping("/missions")
public class Mission_controller {
    private final Mission_logistique_service mission_vehicule_service;
    private final Vehicule_service vehicule_service;
    private final Statuts_mission_service statuts_mission_service;
    private final Chauffeur_service chauffeur_service;

    public Mission_controller(Mission_logistique_service mission_vehicule_service,Vehicule_service vehicule_service , Statuts_mission_service statuts_mission_service , Chauffeur_service chauffeur_service){
        this.mission_vehicule_service = mission_vehicule_service;
        this.chauffeur_service = chauffeur_service;
        this.vehicule_service = vehicule_service;
        this.statuts_mission_service = statuts_mission_service;
    }

    @GetMapping("/save")
    public String ajouteMission(Model model ){

        model.addAttribute("mission_logistique" , new Mission_logistique());
        model.addAttribute("vehicule" , vehicule_service.listeVehicules());
        model.addAttribute("chauffeur" , chauffeur_service.listeChauffeur());
        model.addAttribute("Statuts_mission" , statuts_mission_service.listStatuts_missions());
        // model.addAttribute("mission_logistique" , mission_vehicule_service.listMission_logistique());
        model.addAttribute("missionsEnCours", mission_vehicule_service.missionsEnCours());
        model.addAttribute("historiqueMissions", mission_vehicule_service.historiqueMissions());
        
        return "missions/save";
    }
}
