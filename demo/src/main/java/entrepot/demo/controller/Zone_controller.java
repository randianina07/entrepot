package entrepot.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import entrepot.demo.model.Zone;
import entrepot.demo.service.Zone_service;


@Controller
public class Zone_controller {

    @Autowired
    Zone_service zone_service;

    @GetMapping("/type-zone/{id}")
    public String getTypeZone(@PathVariable Long id, Model model) {

        List<Zone> zones = zone_service.getZonesByTypeZoneId(id);
        model.addAttribute("zones", zones);

        return "Emplacement/list-zones";
    
    }

}
