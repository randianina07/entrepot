package com.entrepot.gestion.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.entrepot.gestion.model.LigneMouvement;
import com.entrepot.gestion.model.MissionLogistique;
import com.entrepot.gestion.model.Mouvement;
import com.entrepot.gestion.model.StatsClient;
import com.entrepot.gestion.model.StockEmplacement;
import com.entrepot.gestion.model.Zone;
import com.entrepot.gestion.repository.EmplacementRepository;
import com.entrepot.gestion.repository.LigneMouvementRepository;
import com.entrepot.gestion.repository.MissionLogistiqueRepository;
import com.entrepot.gestion.repository.MouvementRepository;
import com.entrepot.gestion.repository.StatsClientRepository;
import com.entrepot.gestion.repository.StockEmplacementRepository;
import com.entrepot.gestion.repository.Zone_repository;

@Controller
@RequestMapping("/dashboard")
public class DashboardStatsController {

    @Autowired
    private EmplacementRepository emplacementRepository;

    @Autowired
    private StockEmplacementRepository stockEmplacementRepository;

    @Autowired
    private MouvementRepository mouvementRepository;

    @Autowired
    private MissionLogistiqueRepository missionLogistiqueRepository;

    @Autowired
    private StatsClientRepository statsClientRepository;

    @Autowired
    private LigneMouvementRepository ligneMouvementRepository;

    @Autowired
    private Zone_repository zoneRepository;

    @GetMapping("/stats")
    public String getDashboardStats(Model model) {

        // =====================================================================
        // KPI : Taux d'occupation global
        // =====================================================================
        BigDecimal capaciteTotale = BigDecimal.ZERO;
        BigDecimal volumeOccupeTotal = BigDecimal.ZERO;
        for (var emplacement : emplacementRepository.findAll()) {
            BigDecimal capacite = emplacement.getCapaciteVolumeM3();
            if (capacite == null) {
                capacite = BigDecimal.ZERO;
            }
            capaciteTotale = capaciteTotale.add(capacite);

            BigDecimal volumeOccupe = stockEmplacementRepository.sumVolumeByEmplacementId(emplacement.getId());
            if (volumeOccupe != null) {
                volumeOccupeTotal = volumeOccupeTotal.add(volumeOccupe);
            }
        }
        BigDecimal tauxOccupation = capaciteTotale.compareTo(BigDecimal.ZERO) > 0
                ? volumeOccupeTotal.multiply(new BigDecimal("100")).divide(capaciteTotale, 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // KPI : Nombre total de mouvements
        long totalMouvements = mouvementRepository.count();

        // KPI : Ponctualité des missions terminées
        List<MissionLogistique> missionsTerminees = missionLogistiqueRepository.findByStatutMissionCode("TERMINEE");
        long missionsAlheure = missionsTerminees.stream()
                .filter(m -> m.getDateArriveeReelle() != null && m.getDateArriveePrevue() != null)
                .filter(m -> !m.getDateArriveeReelle().isAfter(m.getDateArriveePrevue()))
                .count();
        BigDecimal tauxPonctualite = !missionsTerminees.isEmpty()
                ? new BigDecimal(missionsAlheure).multiply(new BigDecimal("100"))
                        .divide(new BigDecimal(missionsTerminees.size()), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // KPI : Chiffre d'affaires total (stats_clients)
        List<StatsClient> statsClients;
        try {
            statsClients = statsClientRepository.findAll();
        } catch (DataAccessException ex) {
            statsClients = List.of();
        }

        BigDecimal chiffreAffairesTotal = statsClients.stream()
            .map(s -> s.getChiffreAffaires())
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("tauxOccupation", tauxOccupation);
        model.addAttribute("totalMouvements", totalMouvements);
        model.addAttribute("tauxPonctualite", tauxPonctualite);
        model.addAttribute("chiffreAffairesTotal", chiffreAffairesTotal);

        // =====================================================================
        // GRAPHIQUE 1 : Occupation par zone (barres avec % par zone)
        // =====================================================================
        List<Zone> allZones = zoneRepository.findAll();
        List<Object[]> volumeParZone = stockEmplacementRepository.sumVolumeGroupedByZone();
        Map<Long, BigDecimal> volumeOccupeParZoneMap = new HashMap<>();
        for (Object[] row : volumeParZone) {
            Long zoneId = ((Number) row[0]).longValue();
            BigDecimal vol = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            volumeOccupeParZoneMap.put(zoneId, vol);
        }

        List<String> occupationLabels = new ArrayList<>();
        List<BigDecimal> occupationData = new ArrayList<>();
        for (Zone zone : allZones) {
            String label = zone.getLibelle() != null ? zone.getLibelle() : "Zone #" + zone.getId();
            BigDecimal capaciteZone = BigDecimal.valueOf(zone.getVolume_total_m3());
            BigDecimal occupe = volumeOccupeParZoneMap.getOrDefault(zone.getId(), BigDecimal.ZERO);
            BigDecimal pct = capaciteZone.compareTo(BigDecimal.ZERO) > 0
                    ? occupe.multiply(new BigDecimal("100")).divide(capaciteZone, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            occupationLabels.add(label);
            occupationData.add(pct);
        }

        // =====================================================================
        // GRAPHIQUE 2 : Flux entrées/sorties (7 derniers jours)
        // Calculé depuis mouvements + lignes_mouvement + types_mouvement
        // =====================================================================
        LocalDate debutFlux = LocalDate.now().minusDays(6);
        LocalDate finFlux = LocalDate.now();
        LocalDateTime debutDateTime = debutFlux.atStartOfDay();
        LocalDateTime finDateTime = finFlux.atTime(LocalTime.MAX);

        List<Mouvement> mouvementsPeriode = mouvementRepository.findByDateMouvementBetween(debutDateTime, finDateTime);

        Map<LocalDate, BigDecimal> entreesParDate = new TreeMap<>();
        Map<LocalDate, BigDecimal> sortiesParDate = new TreeMap<>();

        for (Mouvement mvt : mouvementsPeriode) {
            if (mvt.getTypeMouvement() == null || mvt.getDateMouvement() == null) {
                continue;
            }
            String sens = mvt.getTypeMouvement().getSens();
            LocalDate dateMvt = mvt.getDateMouvement().toLocalDate();
            List<LigneMouvement> lignes = ligneMouvementRepository.findByMouvement_Id(mvt.getId());
            for (LigneMouvement ligne : lignes) {
                BigDecimal qte = ligne.getQuantite() != null ? ligne.getQuantite() : BigDecimal.ZERO;
                BigDecimal volumeUnitaire = BigDecimal.ZERO;
                if (ligne.getProduit() != null && ligne.getProduit().getVolumeUnitaireM3() != null) {
                    volumeUnitaire = ligne.getProduit().getVolumeUnitaireM3();
                }
                BigDecimal volumeLigne = qte.multiply(volumeUnitaire);
                if ("ENTREE".equalsIgnoreCase(sens)) {
                    entreesParDate.merge(dateMvt, volumeLigne, BigDecimal::add);
                } else if ("SORTIE".equalsIgnoreCase(sens)) {
                    sortiesParDate.merge(dateMvt, volumeLigne, BigDecimal::add);
                }
            }
        }

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM");
        List<String> fluxLabels = new ArrayList<>();
        List<BigDecimal> fluxEntrees = new ArrayList<>();
        List<BigDecimal> fluxSorties = new ArrayList<>();
        for (LocalDate d = debutFlux; !d.isAfter(finFlux); d = d.plusDays(1)) {
            fluxLabels.add(d.format(dateFmt));
            fluxEntrees.add(entreesParDate.getOrDefault(d, BigDecimal.ZERO));
            fluxSorties.add(sortiesParDate.getOrDefault(d, BigDecimal.ZERO));
        }

        // =====================================================================
        // GRAPHIQUE 3 : Chiffre d'affaires (depuis stats_clients)
        // =====================================================================
        BigDecimal chiffreAffairesTotalGlobal = statsClients.stream()
            .map(s -> s.getChiffreAffaires() != null ? s.getChiffreAffaires() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<String> caLabels = List.of("Chiffre d'affaires total");
        List<BigDecimal> caData = List.of(chiffreAffairesTotalGlobal);

        // =====================================================================
        // GRAPHIQUE 4 : Top produits (depuis stocks_emplacement)
        // =====================================================================
        List<StockEmplacement> allStocks = stockEmplacementRepository.findAll();
        Map<String, BigDecimal> quantiteParProduit = new LinkedHashMap<>();
        for (StockEmplacement se : allStocks) {
            String nomProduit;
            if (se.getProduit() != null && se.getProduit().getNom() != null && !se.getProduit().getNom().isBlank()) {
                nomProduit = se.getProduit().getNom();
            } else {
                nomProduit = "Produit #" + (se.getProduit() != null ? se.getProduit().getId() : "N/A");
            }
            BigDecimal qte = se.getQuantite() != null ? se.getQuantite() : BigDecimal.ZERO;
            quantiteParProduit.merge(nomProduit, qte, BigDecimal::add);
        }

        List<Map.Entry<String, BigDecimal>> topProduitsGlobaux = quantiteParProduit.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .toList();

        List<String> topProduitLabels = topProduitsGlobaux.stream()
            .map(Map.Entry::getKey)
            .toList();
        List<BigDecimal> topProduitData = topProduitsGlobaux.stream()
            .map(Map.Entry::getValue)
            .toList();

        // =====================================================================
        // GRAPHIQUE 5 : Ponctualité logistique
        // =====================================================================
        long nbRetards = Math.max(0, missionsTerminees.size() - missionsAlheure);

        // =====================================================================
        // Attributs du modèle
        // =====================================================================
        model.addAttribute("periodeAffichee", LocalDate.now());

        model.addAttribute("occupationLabels", occupationLabels);
        model.addAttribute("occupationData", occupationData);

        model.addAttribute("fluxLabels", fluxLabels);
        model.addAttribute("fluxEntrees", fluxEntrees);
        model.addAttribute("fluxSorties", fluxSorties);

        model.addAttribute("caLabels", caLabels);
        model.addAttribute("caData", caData);

        model.addAttribute("topProduitLabels", topProduitLabels);
        model.addAttribute("topProduitData", topProduitData);

        model.addAttribute("ponctualiteLabels", List.of("À l'heure", "Retard"));
        model.addAttribute("ponctualiteData", List.of(missionsAlheure, nbRetards));

        return "dashboard/stats";
    }
}
