package entrepot.demo.service;

import entrepot.demo.model.Emplacement;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmplacementService {

    // Cette méthode simule ta base de données pour le MVP
    private List<Emplacement> genererFauxEmplacements() {
        List<Emplacement> liste = new ArrayList<>();

        // On crée 3 faux emplacements pour tester les cas de figure
        Emplacement emp1 = new Emplacement();
        emp1.setCode("ETA-A1-ET1-E1");
        emp1.setCapacite_volume_m3(10.0); // Trop petit pour ton produit de 20
        emp1.setActif(true);

        Emplacement emp2 = new Emplacement();
        emp2.setCode("ETA-A1-ET1-E2");
        emp2.setCapacite_volume_m3(30.0);
        emp2.setActif(true); // Assez grand, mais déjà occupé/inactif !

        Emplacement emp3 = new Emplacement();
        emp3.setCode("ETA-A1-ET1-E3");
        emp3.setCapacite_volume_m3(35.0);
        emp3.setActif(true); // Parfait ! Libre et assez grand.

        liste.add(emp1);
        liste.add(emp2);
        liste.add(emp3);
        return liste;
    }

    // Le cœur de ton MVP : l'algorithme de recherche rapide
    public List<Emplacement> trouverPlaceRapide(double tailleProduit, int quantite) {
        int count = 0;
        List<Emplacement> listeEmplacementsTrouves = new ArrayList<>();
        List<Emplacement> tousLesEmplacements = genererFauxEmplacements();

        // On parcourt la liste (Vô mitady libre ray)
        while (count != quantite) {
            boolean trouveDansCeTour = false;
            for (Emplacement emp : tousLesEmplacements) {
                // Règle métier : Doit être actif ET assez grand (taille < capacité)
                if (emp.isActif() && emp.getCapacite_volume_m3() >= tailleProduit) {
                    listeEmplacementsTrouves.add(emp);
                    trouveDansCeTour = true;
                    count++;
                    
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