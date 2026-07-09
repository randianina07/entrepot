package entrepot.demo.service;

import entrepot.demo.entity.Chauffeur;
import entrepot.demo.repository.ChauffeurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class Chauffeur_service {

    private final ChauffeurRepository chauffeurRepository;

    public Chauffeur_service(ChauffeurRepository chauffeurRepository) {
        this.chauffeurRepository = chauffeurRepository;
    }

    public List<Chauffeur> getAllChauffeurs() {
        return chauffeurRepository.findAll();
    }

    public List<Chauffeur> getChauffeurs(
        String statut,
        LocalDate dateDebutExpiration,
        LocalDate dateFinExpiration) {

    List<Chauffeur> chauffeurs = chauffeurRepository.findAll();

    if (statut != null && !"all".equalsIgnoreCase(statut)) {
        String statutNormalise = statut.toLowerCase(Locale.ROOT);

        if ("actif".equals(statutNormalise)) {
            chauffeurs = chauffeurs.stream()
                    .filter(Chauffeur::isActif)
                    .collect(Collectors.toList());

        } else if ("non-actif".equals(statutNormalise)
                || "nonactif".equals(statutNormalise)) {

            chauffeurs = chauffeurs.stream()
                    .filter(chauffeur -> !chauffeur.isActif())
                    .collect(Collectors.toList());
        }
    }

    if (dateDebutExpiration != null && dateFinExpiration != null) {
        chauffeurs = chauffeurs.stream()
                .filter(chauffeur -> chauffeur.getDateExpirationPermis() != null)
                .filter(chauffeur ->
                        !chauffeur.getDateExpirationPermis().isBefore(dateDebutExpiration)
                        && !chauffeur.getDateExpirationPermis().isAfter(dateFinExpiration))
                .collect(Collectors.toList());
    }

    return chauffeurs;
}

    public Chauffeur addChauffeur(Chauffeur chauffeur) {
        return chauffeurRepository.save(chauffeur);
    }
}
