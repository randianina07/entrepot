package com.entrepot.gestion.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Tarif_livraison;
import com.entrepot.gestion.repository.Tarif_livraison_repository;


@Service
public class Tarif_livraison_service {
    
    private final Tarif_livraison_repository tarif_livraison_repository;

    public Tarif_livraison_service(Tarif_livraison_repository tarif_livraison_repository){
        this.tarif_livraison_repository = tarif_livraison_repository;
    }
    
    public List<Tarif_livraison> listeTarif(){
        return tarif_livraison_repository.findAll();
    }
}
