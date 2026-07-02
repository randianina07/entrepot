package entrepot.demo.service;

import entrepot.demo.model.Emplacement;
import entrepot.demo.repositories.EmplacementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmplacementService {

    @Autowired
    private EmplacementRepository emplacementRepository;

    // Le cœur de ton MVP : l'algorithme de recherche rapide
    public List<Emplacement> trouverPlaceRapide(double tailleProduit, int quantite) {
        List<Emplacement> listeEmplacementsTrouves = new ArrayList<>();
        List<Emplacement> tousLesEmplacements = emplacementRepository.findAll();

        // On parcourt la liste (Vô mitady libre ra
            for (Emplacement emp : tousLesEmplacements) {
                //Quand la liste est égale à la quantite demandé 
                if (listeEmplacementsTrouves.size() == quantite) {
                    break;
                }
                // Règle métier : Doit être actif ET assez grand (taille < capacité)
                if (emp.isActif() && emp.getCapacite_volume_m3() >= tailleProduit) {
                        listeEmplacementsTrouves.add(emp);
                }

                //Si la quantite demandée n'a pas assez de place 
                if (listeEmplacementsTrouves.size() <= quantite) {
                    return new ArrayList<>();
                }
            }
        return listeEmplacementsTrouves; // Renvoie null si aucune place ne correspond
    }
}