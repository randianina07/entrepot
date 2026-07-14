package com.entrepot.gestion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Emplacement;
import com.entrepot.gestion.model.Etage;
import com.entrepot.gestion.model.StockEmplacement;
import com.entrepot.gestion.model.Zone;
import com.entrepot.gestion.repository.EmplacementRepository;
import com.entrepot.gestion.repository.Etage_repository;
import com.entrepot.gestion.repository.StockEmplacementRepository;
import com.entrepot.gestion.repository.Zone_repository;

@Service
public class EmplacementService {

    @Autowired
    private Zone_repository zoneRepository;

    @Autowired
    private Etage_repository etageRepository;

    @Autowired
    private EmplacementRepository emplacementRepository;

    // Le cœur de ton MVP : l'algorithme de recherche rapide
    @Autowired
    private StockEmplacementRepository stockRepository;

public List<Emplacement> trouverPlaceRapide(Long typeZoneId,
                                            double volumeUnitaireProduit,
                                            int quantite) {

    List<Emplacement> resultat = new ArrayList<>();

    List<Zone> zones =
    zoneRepository.findByTypeZoneId(typeZoneId);

    if (zones.isEmpty()) {
        return resultat;
    }

    
    List<Etage> etages = etageRepository.findAll();
    List<Emplacement> emplacements = emplacementRepository.findAll();
    
    for (Zone zone : zones) {
        
    Long idAllee = zone.getAllees().getId();

    int quantiteRestante = quantite;

    for (Etage etage : etages) {

        for (Emplacement emp : emplacements) {

            if (!emp.getAllee().getId().equals(idAllee))
                continue;

            if (!emp.getEtage().getId().equals(etage.getId()))
                continue;

            // Calcul du volume déjà occupé
            List<StockEmplacement> stocks =
                    stockRepository.findByEmplacement(emp);

            double volumeOccupe = 0;

            for (StockEmplacement stock : stocks) {
                volumeOccupe += stock.getProduit().getVolumeUnitaireM3().doubleValue()
                        * stock.getQuantite().doubleValue();
            }

            double volumeDisponible =
                    emp.getCapacite_volume_m3() - volumeOccupe;

            if (volumeDisponible <= 0)
                continue; 

            // Nombre maximum de cartons pouvant entrer
            int cartonsPossibles =
                    (int) Math.floor(volumeDisponible / volumeUnitaireProduit);

            if (cartonsPossibles <= 0)
                continue;

            resultat.add(emp);

            if (cartonsPossibles >= quantiteRestante) {
                quantiteRestante = 0;
                break;
            } else {
                quantiteRestante -= cartonsPossibles;
            }
        }

        if (quantiteRestante == 0)
            break;
    }
    
        if (quantiteRestante > 0) {
            return new ArrayList<>();
        }
    }
    
    return resultat;
}

public Emplacement findById(Long id) {

    return emplacementRepository.findById(id).orElseThrow(() -> new RuntimeException("Emplacement introuvable"));

}

}
