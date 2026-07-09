package com.entrepot.gestion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.entrepot.gestion.model.Type_zone;
import com.entrepot.gestion.service.Type_zone_service;



@Controller
public class Type_zone_controller {
    
    @Autowired
    Type_zone_service type_zone_service;

    @GetMapping("/choose-type_zones")
    public String choose_type_zone(Model model) {
        
        List<Type_zone> type_zones = type_zone_service.getAllTypeZones();
        model.addAttribute("type_zones", type_zones);
        
        return "Emplacement/Choose_type_zone";
    }

    @GetMapping("/accueil")
    public String Espace_stockage() {

        return "Emplacement/accueil";

    }
    
        

}
