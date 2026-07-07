package entrepot.demo.controller;

import org.springframework.stereotype.Controller;

import entrepot.demo.service.FactureService;

@Controller
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

}
