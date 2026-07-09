package entrepot.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import entrepot.demo.service.Type_zone_service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import entrepot.demo.model.Type_zone;
import org.springframework.web.bind.annotation.RequestParam;



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

    @GetMapping("/")
    public String Espace_stockage() {

        return "Emplacement/accueil";

    }
    
        

}
