package com.entrepot.gestion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entrepot.gestion.dto.FactureDTO;
import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.Facture;
import com.entrepot.gestion.model.TarifZone;
import com.entrepot.gestion.model.UniteDuree;
import com.entrepot.gestion.model.UtilisateurInfo;
import com.entrepot.gestion.repository.AbonnementStockageRepository;
import com.entrepot.gestion.repository.ContratRepository;
import com.entrepot.gestion.repository.FactureRepository;
import com.entrepot.gestion.repository.TarifZoneRepository;
import com.entrepot.gestion.repository.UniteDureeRepository;
import com.entrepot.gestion.utils.DateUtil;
import com.entrepot.gestion.utils.DateUtil.Durree;

@Service
public class FactureService {

    private final FactureRepository factureRepository;
    private final ContratRepository contratRepository;
    private final TarifZoneRepository tarifZoneRepository;
    private final UniteDureeRepository uniteDureeRepository;
    private final AbonnementStockageRepository abonnementStockageRepository;

    public FactureService(
            FactureRepository factureRepository,
            ContratRepository contratRepository,
            TarifZoneRepository tarifZoneRepository,
            UniteDureeRepository uniteDureeRepository,
            AbonnementStockageRepository abonnementStockageRepository) {
        this.factureRepository = factureRepository;
        this.contratRepository = contratRepository;
        this.tarifZoneRepository = tarifZoneRepository;
        this.uniteDureeRepository = uniteDureeRepository;
        this.abonnementStockageRepository = abonnementStockageRepository;
    }

    /**
     * Génère la facture pour un contrat à une date donnée.
     */
    public FactureDTO genererFacture(Long contratId, LocalDate dateSaisie) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable avec l'ID : " + contratId));

        final LocalDate factureDate = (dateSaisie != null) ? dateSaisie : LocalDate.now();

        if (factureDate.isBefore(contrat.getDateDebut())) {
            throw new RuntimeException("La date de facture (" + factureDate
                    + ") est antérieure à la date de début du contrat (" + contrat.getDateDebut() + ")");
        }

        boolean estAbonnement = contrat.getDureeMois() != null;

        FactureDTO dto = new FactureDTO();
        dto.setContratId(contrat.getId());
        dto.setDateFacture(factureDate);
        dto.setDateDebut(contrat.getDateDebut());
        dto.setVolumeM3(contrat.getVolumeEspaceM3());
        dto.setQuantiteEmplacement(contrat.getQuantiteEmplacement());
        dto.setTypeZone(contrat.getTypeZone().getLibelle());
        dto.setTypeContrat(contrat.getTypeContrat().getLibelle());

        // Informations client
        UtilisateurInfo info = contrat.getUtilisateur().getInfo();
        if (info != null) {
            dto.setClientNom(info.getNom());
            dto.setClientPrenom(info.getPrenom());
            dto.setClientAdresse(info.getAdresse());
            dto.setClientTelephone(info.getNumero());
        }
        dto.setClientEmail(contrat.getUtilisateur().getEmail());

        BigDecimal volume = contrat.getVolumeEspaceM3();
        if (volume == null || volume.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le volume du contrat est invalide");
        }

        // Récupérer les tarifs
        UniteDuree uniteJour = uniteDureeRepository.findByCode("JOUR")
                .orElseThrow(() -> new RuntimeException("Unité de durée JOUR introuvable"));
        UniteDuree uniteMois = uniteDureeRepository.findByCode("MOIS")
                .orElseThrow(() -> new RuntimeException("Unité de durée MOIS introuvable"));

        TarifZone tarifMois = tarifZoneRepository
                .findByTypeZoneAndUniteDureeAndDate(contrat.getTypeZone(), uniteMois, factureDate)
                .orElseThrow(() -> new RuntimeException(
                        "Tarif mensuel introuvable pour la zone " + contrat.getTypeZone().getLibelle()
                                + " à la date " + factureDate));

        TarifZone tarifJour = tarifZoneRepository
                .findByTypeZoneAndUniteDureeAndDate(contrat.getTypeZone(), uniteJour, factureDate)
                .orElseThrow(() -> new RuntimeException(
                        "Tarif journalier introuvable pour la zone " + contrat.getTypeZone().getLibelle()
                                + " à la date " + factureDate));

        dto.setPrixM3Mois(tarifMois.getPrixM3());
        dto.setPrixM3Jour(tarifJour.getPrixM3());

        if (estAbonnement) {
            calculerFactureAbonnement(dto, contrat, factureDate, volume, tarifMois, tarifJour);
        } else {
            calculerFactureNonAbonnement(dto, contrat, factureDate, volume, tarifMois, tarifJour);
        }

        return dto;
    }

    /**
     * Calcul pour un contrat non abonnement (simple).
     */
    private void calculerFactureNonAbonnement(
            FactureDTO dto, Contrat contrat, LocalDate factureDate,
            BigDecimal volume, TarifZone tarifMois, TarifZone tarifJour) {

        LocalDate dateFinContrat = contrat.getDateFin();
        LocalDate dateFinEffective;
        if (dateFinContrat != null && factureDate.isAfter(dateFinContrat)) {
            // Si apres la date fin du contrat: utiliser date fin
            dateFinEffective = dateFinContrat;
        } else {
            // Sinon ou si date_fin = null: prendre la date facture
            dateFinEffective = factureDate;
        }

        // Vérifier que la date de début est bien avant
        if (dateFinEffective.isBefore(contrat.getDateDebut())) {
            throw new RuntimeException("La date de facture est antérieure à la date de début du contrat");
        }

        Durree duree = DateUtil.calculerDuree(contrat.getDateDebut(), dateFinEffective);

        dto.setDateFin(dateFinEffective);
        dto.setDureeMois(duree.getMois());
        dto.setDureeJours(duree.getJours());
        dto.setDureeAffichage(duree.toString());

        // Calcul des totaux : volume * duree * prix
        BigDecimal totalMois = BigDecimal.ZERO;
        if (duree.getMois() > 0) {
            totalMois = tarifMois.getPrixM3()
                    .multiply(volume)
                    .multiply(BigDecimal.valueOf(duree.getMois()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalJours = BigDecimal.ZERO;
        if (duree.getJours() > 0) {
            totalJours = tarifJour.getPrixM3()
                    .multiply(volume)
                    .multiply(BigDecimal.valueOf(duree.getJours()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        dto.setTotalMois(totalMois);
        dto.setTotalJours(totalJours);
        dto.setTotalGeneral(totalMois.add(totalJours));
    }

    /**
     * Calcul pour un contrat abonnement.
     */
    private void calculerFactureAbonnement(
            FactureDTO dto, Contrat contrat, LocalDate factureDate,
            BigDecimal volume, TarifZone tarifMois, TarifZone tarifJour) {

        // Date de fin théorique basée sur la durée en mois depuis la date début
        LocalDate dateFinTheorique = DateUtil.ajouterMois(contrat.getDateDebut(), contrat.getDureeMois());

        // Vérifier si l'abonnement a une date_fin en base (renouvellement etc.)
        LocalDate dateFinContrat = contrat.getDateFin();

        // Date de fin effective : la plus petite entre dateFinTheorique et dateFinContrat
        LocalDate dateFinEffective = dateFinTheorique;
        if (dateFinContrat != null && dateFinContrat.isBefore(dateFinEffective)) {
            dateFinEffective = dateFinContrat;
        }

        boolean depasseDateFin = factureDate.isAfter(dateFinEffective);

        if (depasseDateFin) {
            // Si date est au dela de date fin, utiliser date fin (duree en mois fois tarifs mois)
            int totalMoisAbonnement = DateUtil.calculerMoisCompletsDepuisDebut(contrat.getDateDebut(), dateFinEffective);
            if (totalMoisAbonnement <= 0) {
                throw new RuntimeException("La durée d'abonnement calculée est invalide");
            }

            dto.setDateFin(dateFinEffective);
            dto.setDureeMois(totalMoisAbonnement);
            dto.setDureeJours(0);
            dto.setDureeAffichage(totalMoisAbonnement + " mois");

            BigDecimal total = tarifMois.getPrixM3()
                    .multiply(volume)
                    .multiply(BigDecimal.valueOf(totalMoisAbonnement))
                    .setScale(2, RoundingMode.HALF_UP);

            dto.setTotalMois(total);
            dto.setTotalJours(BigDecimal.ZERO);
            dto.setTotalGeneral(total);
        } else {
            // Avant la date de fin : mode abonnement mensuel
            // Ne pas regarder n'importe quelle valeur, mais la date d'un mois avant la date entrée
            // Donc on cherche la dernière date anniversaire (basée sur le jour de début)
            LocalDate derniereAnniversaire = DateUtil.trouverDerniereAnniversaire(
                    contrat.getDateDebut(), factureDate);

            if (derniereAnniversaire.isBefore(contrat.getDateDebut())) {
                derniereAnniversaire = contrat.getDateDebut();
            }

            // Nombre de mois entre début et cette date anniversaire
            int moisPayes = DateUtil.calculerMoisCompletsDepuisDebut(contrat.getDateDebut(), derniereAnniversaire);

            if (moisPayes <= 0) {
                throw new RuntimeException("Aucun mois complet écoulé depuis le début de l'abonnement");
            }

            dto.setDateFin(derniereAnniversaire);
            dto.setDureeMois(moisPayes);
            dto.setDureeJours(0);
            dto.setDureeAffichage(moisPayes + " mois (abonnement)");

            BigDecimal total = tarifMois.getPrixM3()
                    .multiply(volume)
                    .multiply(BigDecimal.valueOf(moisPayes))
                    .setScale(2, RoundingMode.HALF_UP);

            dto.setTotalMois(total);
            dto.setTotalJours(BigDecimal.ZERO);
            dto.setTotalGeneral(total);
        }
    }

    /**
     * Sauvegarde une facture en base de données.
     */
    @Transactional
    public Facture sauvegarderFacture(Long contratId, LocalDate dateFacture) {
        FactureDTO dto = genererFacture(contratId, dateFacture);
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        Facture facture = new Facture();
        facture.setContrat(contrat);
        facture.setVolumeEspaceM3(dto.getVolumeM3());
        facture.setPrixFacture(dto.getTotalGeneral());
        facture.setDateEmission(dto.getDateFacture());

        return factureRepository.save(facture);
    }
}
