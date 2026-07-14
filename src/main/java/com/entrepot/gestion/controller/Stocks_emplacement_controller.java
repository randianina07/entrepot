package com.entrepot.gestion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.entrepot.gestion.model.StockEmplacement;
import com.entrepot.gestion.service.Stocks_emplacement_service;

@RestController
public class Stocks_emplacement_controller {
    
    @Autowired
    Stocks_emplacement_service stocks_emplacement_service;

    @GetMapping("/api/zone/{id}")
    public List<StockEmplacement> api_emplacement(@PathVariable("id") Long id) {
        
        List<StockEmplacement> stocks_emplacements = stocks_emplacement_service.getStocksEmplacementsByZoneId(id);
        return stocks_emplacements;
        
    }

}
