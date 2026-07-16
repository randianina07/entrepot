package com.entrepot.gestion.service;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.StockEmplacement;
import com.entrepot.gestion.repository.StockEmplacementRepository;

@Service
public class Stocks_emplacement_service {

    @Autowired
    StockEmplacementRepository stocks_emplacement_repository;

    public List<StockEmplacement> getStocksEmplacementsByZoneId(Long id) {
        
        return stocks_emplacement_repository.findByZoneId(id);

    }

    public void save (StockEmplacement stk) {

        stocks_emplacement_repository.save(stk);

    }

    public List<StockEmplacement> findByEmplacementIdAndProduitIdAndZoneId(Long emplacementId, Long produitId, Long zoneId) {

        return stocks_emplacement_repository.findByEmplacementIdAndProduitIdAndZoneIdOrderByIdAsc(emplacementId, produitId, zoneId);

    }

    public void delete(StockEmplacement stk) {

        stocks_emplacement_repository.delete(stk);

    }

    public List<StockEmplacement> findAll() {

        return stocks_emplacement_repository.findAll();

    }

}
