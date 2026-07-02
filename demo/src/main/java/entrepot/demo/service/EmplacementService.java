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
        int count = 0;
        List<Emplacement> listeEmplacementsTrouves = new ArrayList<>();
        List<Emplacement> tousLesEmplacements = emplacementRepository.findAll();

        // On parcourt la liste (Vô mitady libre ray)
        while (count != quantite) {
            boolean trouveDansCeTour = false;
            for (Emplacement emp : tousLesEmplacements) {
                // Règle métier : Doit être actif ET assez grand (taille < capacité)
                if (emp.isActif() && emp.getCapacite_volume_m3() >= tailleProduit) {
                    double verifVolum = tailleProduit - emp.getCapacite_volume_m3();
                    if (verifVolum < 0) {
                        continue;
                    } else {
                        listeEmplacementsTrouves.add(emp);
                        trouveDansCeTour = true;
                        count++;
                    }

                    if (count == quantite) {
                        break;
                    }
                }

                if (trouveDansCeTour) {
                    break;
                }
            }
        }
        return listeEmplacementsTrouves; // Renvoie null si aucune place ne correspond
    }
}