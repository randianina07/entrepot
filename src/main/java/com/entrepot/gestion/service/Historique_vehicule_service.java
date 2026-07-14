package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Historique_vehicule;
import com.entrepot.gestion.repository.Historique_vehicule_repository;


@Service
public class Historique_vehicule_service {
    private final Historique_vehicule_repository historique_vehicule_repository;

    public Historique_vehicule_service(Historique_vehicule_repository historique_vehicule_repository) {
        this.historique_vehicule_repository = historique_vehicule_repository;
    }

    public Historique_vehicule findById(Long id) {
        return historique_vehicule_repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voiture non Trouver"));
    }

    public List<Historique_vehicule> findByVehiculeId(Long vehiculeId) {
        return historique_vehicule_repository.findByVehiculeId(vehiculeId);
    }
}
