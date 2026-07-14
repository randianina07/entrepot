package com.entrepot.gestion.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
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

import com.entrepot.gestion.model.MissionLogistique;
import com.entrepot.gestion.model.StatsClient;
import com.entrepot.gestion.model.TopProduit;
import com.entrepot.gestion.repository.EmplacementRepository;
import com.entrepot.gestion.repository.FluxEntreesSortiesRepository;
import com.entrepot.gestion.repository.MissionLogistiqueRepository;
import com.entrepot.gestion.repository.MouvementRepository;
import com.entrepot.gestion.repository.StatsClientRepository;
import com.entrepot.gestion.repository.StockEmplacementRepository;
import com.entrepot.gestion.repository.TopProduitRepository;

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
    private TopProduitRepository topProduitRepository;

    @Autowired
    private FluxEntreesSortiesRepository fluxEntreesSortiesRepository;

    @GetMapping("/stats")
    public String getDashboardStats(Model model) {

        // Taux d'occupation global = volume occupe / capacite totale des emplacements actifs
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

        // Nombre total de mouvements enregistres
        long totalMouvements = mouvementRepository.count();

        // Ponctualite des missions terminees (arrivee reelle <= arrivee prevue)
        List<MissionLogistique> missionsTerminees = missionLogistiqueRepository.findByStatutMissionCode("TERMINEE");
        long missionsAlheure = missionsTerminees.stream()
                .filter(m -> m.getDateArriveeReelle() != null && m.getDateArriveePrevue() != null)
                .filter(m -> !m.getDateArriveeReelle().isAfter(m.getDateArriveePrevue()))
                .count();
        BigDecimal tauxPonctualite = !missionsTerminees.isEmpty()
                ? new BigDecimal(missionsAlheure).multiply(new BigDecimal("100"))
                        .divide(new BigDecimal(missionsTerminees.size()), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<StatsClient> statsClients;
        try {
            statsClients = statsClientRepository.findAll();
        } catch (DataAccessException ex) {
            statsClients = List.of();
        }

        // Chiffre d'affaires total (snapshots stats_clients)
        BigDecimal chiffreAffairesTotal = statsClients.stream()
            .map(s -> s.getChiffreAffaires())
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, (left, right) -> left.add(right));

        // Top produits stockes (derniers instantanes disponibles)
        List<TopProduit> top5Produits = topProduitRepository.findAll().stream()
            .sorted(Comparator.comparing((TopProduit tp) -> tp.getRang(), Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .toList();

        model.addAttribute("tauxOccupation", tauxOccupation);
        model.addAttribute("totalMouvements", totalMouvements);
        model.addAttribute("tauxPonctualite", tauxPonctualite);
        model.addAttribute("chiffreAffairesTotal", chiffreAffairesTotal);

        // Occupation par emplacement (proxy du "par zone" dans le modèle actuel)
        Map<String, BigDecimal> occupationParEmplacement = new TreeMap<>();
        for (var emplacement : emplacementRepository.findAll()) {
            BigDecimal cap = emplacement.getCapaciteVolumeM3() != null ? emplacement.getCapaciteVolumeM3() : BigDecimal.ZERO;
            BigDecimal occ = stockEmplacementRepository.sumVolumeByEmplacementId(emplacement.getId());
            if (occ == null) {
                occ = BigDecimal.ZERO;
            }
            BigDecimal pct = cap.compareTo(BigDecimal.ZERO) > 0
                    ? occ.multiply(new BigDecimal("100")).divide(cap, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            occupationParEmplacement.put(Objects.toString(emplacement.getCode(), "Emplacement sans code"), pct);
        }

        // Flux des 7 derniers jours (entrées/sorties)
        LocalDate debutFlux = LocalDate.now().minusDays(6);
        LocalDate finFlux = LocalDate.now();
        List<com.entrepot.gestion.model.FluxEntreesSorties> fluxJournaliers =
                fluxEntreesSortiesRepository.findByDateBetween(debutFlux, finFlux);

        Map<LocalDate, BigDecimal> entreesParDate = new TreeMap<>();
        Map<LocalDate, BigDecimal> sortiesParDate = new TreeMap<>();
        for (var flux : fluxJournaliers) {
            if (flux.getDate() == null) {
                continue;
            }
            BigDecimal volume = flux.getVolumeM3() != null ? flux.getVolumeM3() : BigDecimal.ZERO;
            if ("ENTREE".equalsIgnoreCase(flux.getTypeFlux())) {
                entreesParDate.merge(flux.getDate(), volume, (left, right) -> left.add(right));
            } else if ("SORTIE".equalsIgnoreCase(flux.getTypeFlux())) {
                sortiesParDate.merge(flux.getDate(), volume, (left, right) -> left.add(right));
            }
        }

        java.util.List<String> fluxLabels = new java.util.ArrayList<>();
        java.util.List<BigDecimal> fluxEntrees = new java.util.ArrayList<>();
        java.util.List<BigDecimal> fluxSorties = new java.util.ArrayList<>();
        for (LocalDate d = debutFlux; !d.isAfter(finFlux); d = d.plusDays(1)) {
            fluxLabels.add(d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM")));
            fluxEntrees.add(entreesParDate.getOrDefault(d, BigDecimal.ZERO));
            fluxSorties.add(sortiesParDate.getOrDefault(d, BigDecimal.ZERO));
        }

        // Données financières (CA par snapshot)
        List<StatsClient> financeData = statsClients.stream()
                .sorted(Comparator.comparing((StatsClient s) -> s.getDateFin(), Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        BigDecimal chiffreAffairesTotalGlobal = financeData.stream()
            .map(s -> s.getChiffreAffaires() != null ? s.getChiffreAffaires() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, (left, right) -> left.add(right));
        java.util.List<String> caLabels = java.util.List.of("Chiffre d'affaires total");
        java.util.List<BigDecimal> caData = java.util.List.of(chiffreAffairesTotalGlobal);

        // Top produits (vue globale par produit)
        Map<String, BigDecimal> quantiteParProduit = topProduitRepository.findAll().stream()
            .collect(Collectors.toMap(
                tp -> {
                    if (tp.getProduit() != null && tp.getProduit().getNom() != null && !tp.getProduit().getNom().isBlank()) {
                    return tp.getProduit().getNom();
                    }
                    return "Produit #" + (tp.getProduit() != null ? tp.getProduit().getId() : "N/A");
                },
                tp -> tp.getQuantiteTotale() != null ? tp.getQuantiteTotale() : BigDecimal.ZERO,
                        (left, right) -> left.add(right),
                LinkedHashMap::new));

        List<Map.Entry<String, BigDecimal>> topProduitsGlobaux = quantiteParProduit.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .toList();

        java.util.List<String> topProduitLabels = topProduitsGlobaux.stream()
            .map(entry -> entry.getKey())
            .toList();
        java.util.List<BigDecimal> topProduitData = topProduitsGlobaux.stream()
            .map(entry -> entry.getValue())
            .toList();

        long nbRetards = Math.max(0, missionsTerminees.size() - missionsAlheure);

        model.addAttribute("occupationZones", occupationParEmplacement);
        model.addAttribute("fluxJournaliers", fluxJournaliers);
        model.addAttribute("financeData", financeData);
        model.addAttribute("performanceLivr", null);
        model.addAttribute("caClients", financeData);
        model.addAttribute("top5Produits", top5Produits);
        model.addAttribute("periodeAffichee", LocalDate.now());

        // Attributs prêts pour les graphiques
        model.addAttribute("fluxLabels", fluxLabels);
        model.addAttribute("fluxEntrees", fluxEntrees);
        model.addAttribute("fluxSorties", fluxSorties);
        model.addAttribute("caLabels", caLabels);
        model.addAttribute("caData", caData);
        model.addAttribute("topProduitLabels", topProduitLabels);
        model.addAttribute("topProduitData", topProduitData);
        model.addAttribute("occupationLabels", java.util.List.of("Occupé", "Disponible"));
        model.addAttribute("occupationData", java.util.List.of(
            volumeOccupeTotal,
            capaciteTotale.subtract(volumeOccupeTotal).max(BigDecimal.ZERO)
        ));
        model.addAttribute("ponctualiteLabels", java.util.List.of("À l'heure", "Retard"));
        model.addAttribute("ponctualiteData", java.util.List.of(missionsAlheure, nbRetards));

        return "dashboard/stats";
    }
}
