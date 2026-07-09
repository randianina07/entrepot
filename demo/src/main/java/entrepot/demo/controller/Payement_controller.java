package entrepot.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import entrepot.demo.service.Mode_payement_service;
import entrepot.demo.service.Payement_service;


@Controller
public class Payement_controller {
    
    @Autowired
    Payement_service payement_service;
    
    @Autowired
    Mode_payement_service mode_payement_service;

    @GetMapping("/payer")
    public String Payer(Model model) {
        
        return "Payement/payer_form";

    }
    

}
