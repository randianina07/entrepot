package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Produit;
import com.entrepot.gestion.model.Zone;
import com.entrepot.gestion.repository.ProduitRepository;
import com.entrepot.gestion.repository.Zone_repository;

@Service
public class Zone_service {
    
    @Autowired
    Zone_repository zone_repository;

    @Autowired
    ProduitRepository produitRepository;

    public List<Zone> getZonesByTypeZoneId(long id) {

        return zone_repository.findByTypeZoneId(id);
    
    }

    public List<Zone> findAll() {

        return zone_repository.findAll();

    }

    public List<Zone> getZonesParProduit(Long idProduit) {

    Produit produit = produitRepository.findById(idProduit)
            .orElseThrow();

    Long typeProduitId = produit.getTypeProduit().getId();

    return zone_repository.findZonesByTypeProduit(typeProduitId);
    
}

public Zone findById(Long id) {

    return zone_repository.findById(id).orElse(null);

}

}
