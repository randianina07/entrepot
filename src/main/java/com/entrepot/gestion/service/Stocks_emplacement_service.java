package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Stocks_emplacement;
import com.entrepot.gestion.repository.Stocks_emplacement_repository;

@Service
public class Stocks_emplacement_service {

    @Autowired
    Stocks_emplacement_repository stocks_emplacement_repository;

    public List<Stocks_emplacement> getStocksEmplacementsByZoneId(Long id) {
        
        return stocks_emplacement_repository.findByZoneId(id);

    }

}
