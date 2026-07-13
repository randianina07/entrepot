package com.entrepot.gestion.controller;

import org.springframework.stereotype.Controller;

import com.entrepot.gestion.service.FactureService;

@Controller
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

}
