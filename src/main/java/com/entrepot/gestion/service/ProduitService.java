package com.entrepot.gestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Produit;
import com.entrepot.gestion.repository.ProduitRepository;

@Service
public class ProduitService {
    
    @Autowired
    ProduitRepository produitRepository;

    public Produit findById(Long id) {

        return produitRepository.findById(id).orElse(null);

    }

}
