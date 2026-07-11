package entrepot.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import entrepot.demo.model.Mode_payement;
import entrepot.demo.model.Payement;
import entrepot.demo.model.PayementForm;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.service.Mode_payement_service;
import entrepot.demo.service.Payement_service;
import entrepot.demo.service.UtilisateurService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class Payement_controller {
    
    @Autowired
    Payement_service payement_service;
    
    @Autowired
    Mode_payement_service mode_payement_service;

    @Autowired
    UtilisateurService utilisateurService;

    @GetMapping("/payer")
    public String Payer(Model model) {
        
        List<Utilisateur> allClient = utilisateurService.getAllclient();
        model.addAttribute("all_client", allClient);
        List<Mode_payement> all_Mode_payement = mode_payement_service.getAllMode_payement();
        model.addAttribute("mode_payement" , all_Mode_payement);
        PayementForm payementForm = new PayementForm();
        payementForm.getPayements().add(new Payement());
        model.addAttribute("payementForm", payementForm);

        return "Payement/payer_form";

    }
    
    @PostMapping("/payer")
    public String insertPayement(@ModelAttribute PayementForm payementForm, Model model) {
        
        for (Payement payement : payementForm.getPayements()) {

            payement_service.insert_payement(payement);
            
        }
        model.addAttribute("successMessage", "Paiement effectué avec succès !");

        return "redirect:/payer";
    }
    

}
