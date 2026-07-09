package com.entrepot.gestion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Emplacement;
import com.entrepot.gestion.model.Etage;
import com.entrepot.gestion.model.Zone;
import com.entrepot.gestion.repository.EmplacementRepository;
import com.entrepot.gestion.repository.Etage_repository;
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
    public List<Emplacement> trouverPlaceRapide(Long id_zone, double tailleProduit, int quantite) {
        // Liste d'emplacements
        List<Emplacement> tousLesEmplacements = emplacementRepository.findAll();
        // Liste d'étages
        List<Etage> tousLesEtages = etageRepository.findAll();
        // Liste d'alleees
        // List<Allee> tousLesAllees = alleeRepository.findAll();
        // Liste des zones
        List<Zone> toutesLesZones = zoneRepository.findAll();

        List<Emplacement> listeEmplacementsTrouves = new ArrayList<>();
        for (Zone zone : toutesLesZones) {
            if (zone.getId() != null && zone.getId().equals(id_zone)) {
                Long alleeIdDeLaZone = zone.getAllees() != null ? zone.getAllees().getId() : null;
                if (alleeIdDeLaZone == null) {
                    continue;
                }

                for (Etage etage : tousLesEtages) {
                    for (Emplacement emp : tousLesEmplacements) {
                        if (listeEmplacementsTrouves.size() == quantite) {
                            break;
                        }
                        if (emp.getAllee() != null && emp.getAllee().getId().equals(alleeIdDeLaZone) &&
                            emp.getEtage() != null && emp.getEtage().getId().equals(etage.getId())) {
                            // Règle métier : Doit être actif ET assez grand (taille < capacité)
                            if (!emp.isActif() && emp.getCapacite_volume_m3() >= tailleProduit) {
                                listeEmplacementsTrouves.add(emp);
                            }
                        }

                    }

                    if (listeEmplacementsTrouves.size() == quantite) {
                        break;
                    }
                }
            }

        }

        if (listeEmplacementsTrouves.size() < quantite) {
            return new ArrayList<>();
        }
        // Si la quantite demandée n'a pas assez de place
        return listeEmplacementsTrouves; // Renvoie null si aucune place ne correspond
    }
}
