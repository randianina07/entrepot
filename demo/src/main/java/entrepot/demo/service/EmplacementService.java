package entrepot.demo.service;

import entrepot.demo.model.Allee;
import entrepot.demo.model.Emplacement;
import entrepot.demo.model.Etage;
import entrepot.demo.repositories.EmplacementRepository;
import entrepot.demo.repositories.EtageRepository;
import entrepot.demo.repositories.ZonesRepository;
import entrepot.demo.repositories.AlleeRepository;
import entrepot.demo.model.Zones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmplacementService {

    @Autowired
    private ZonesRepository zonesRepository;

    @Autowired
    private AlleeRepository alleeRepository;

    @Autowired
    private EtageRepository etageRepository;

    @Autowired
    private EmplacementRepository emplacementRepository;

    // Le cœur de ton MVP : l'algorithme de recherche rapide
    public List<Emplacement> trouverPlaceRapide(double tailleProduit, int quantite, int idTypeZone) {
        // Liste d'emplacements
        List<Emplacement> tousLesEmplacements = emplacementRepository.findAll();
        // Liste d'étages
        List<Etage> tousLesEtages = etageRepository.findAll();
        // Liste d'alleees
        List<Allee> tousLesAllees = alleeRepository.findAll();
        // Liste des zones
        List<Zones> toutesLesZones = zonesRepository.findAll();

        List<Emplacement> listeEmplacementsTrouves = new ArrayList<>();
        for (Zones zones : toutesLesZones) {

            for (Allee allee : tousLesAllees) {
                for (Etage etage : tousLesEtages) {
                    for (Emplacement emp : tousLesEmplacements) {
                        if (listeEmplacementsTrouves.size() == quantite) {
                            break;
                        }
                        if (allee.getId().equals(emp.getAllee().getId()) && emp.getEtage() != null
                                && emp.getEtage().getId().equals(etage.getId())) {
                            // Règle métier : Doit être actif ET assez grand (taille < capacité)
                            if (emp.isActif() && emp.getCapacite_volume_m3() >= tailleProduit) {
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