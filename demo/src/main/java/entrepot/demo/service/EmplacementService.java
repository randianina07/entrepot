package entrepot.demo.service;

import entrepot.demo.model.Allee;
import entrepot.demo.model.Emplacement;
import entrepot.demo.model.Etage;
import entrepot.demo.model.Stocks_emplacement;
import entrepot.demo.repositories.EmplacementRepository;
import entrepot.demo.repositories.EtageRepository;
import entrepot.demo.repositories.ZonesRepository;
import entrepot.demo.repository.Stocks_emplacement_repository;
import entrepot.demo.model.Zones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmplacementService {

    @Autowired
    private Stocks_emplacement_repository stocksEmplacementRepository;

    @Autowired
    private ZonesRepository zonesRepository;

    @Autowired
    private EtageRepository etageRepository;

    @Autowired
    private EmplacementRepository emplacementRepository;

    // Le cœur de ton MVP : l'algorithme de recherche rapide
    public List<Emplacement> trouverPlaceRapide(Long id_zone, double tailleProduit, int quantite) {
        // Critère de vérification
        if (id_zone == null ||
                tailleProduit <= 0 ||
                quantite <= 0) {
            return new ArrayList<>(); // On renvoie une liste vide immédiatement sans chercher
        }

        // Liste d'emplacements
        List<Emplacement> tousLesEmplacements = emplacementRepository.findAll();
        // Liste d'étages
        List<Etage> tousLesEtages = etageRepository.findAll();
        // Liste d'alleees
        // List<Allee> tousLesAllees = alleeRepository.findAll();
        // Liste des zones
        List<Zones> toutesLesZones = zonesRepository.findAll();

        List<Emplacement> listeEmplacementsTrouves = new ArrayList<>();
        for (Zones zones : toutesLesZones) {
            if (zones.getId() != null && zones.getId().equals(id_zone)) {
                Allee alleeDeLaZone = zones.getAllee();
                if (alleeDeLaZone == null)
                    continue;

                for (Etage etage : tousLesEtages) {
                    for (Emplacement emp : tousLesEmplacements) {
                        if (listeEmplacementsTrouves.size() == quantite) {
                            break;
                        }
                        if (emp.getAllee() != null && emp.getAllee().getId().equals(alleeDeLaZone.getId()) &&
                                emp.getEtage() != null && emp.getEtage().getId().equals(etage.getId())) {

                            // Liste d'stocks Emplacement
                            List<Stocks_emplacement> stock = stocksEmplacementRepository.findByEmplacementId(emp.getId());
                            boolean estActif = false;

                            for (Stocks_emplacement ls_stock : stock) {
                                if (ls_stock.isActif()) {
                                    estActif = true;
                                    break;
                                }
                            }

                            if (!estActif) {
                                continue;
                            }

                            // Règle métier : Doit être actif ET assez grand (taille < capacité)
                            double volumeRestant = emp.getCapacite_volume_m3();
                            if (volumeRestant >= tailleProduit) {
                                while (listeEmplacementsTrouves.size() < quantite && volumeRestant >= tailleProduit) {
                                    listeEmplacementsTrouves.add(emp);
                                    volumeRestant -= tailleProduit;
                                }
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