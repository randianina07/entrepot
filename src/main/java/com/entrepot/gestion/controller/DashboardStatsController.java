package com.entrepot.gestion.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

        // Chiffre d'affaires total (snapshots stats_clients)
        BigDecimal chiffreAffairesTotal = statsClientRepository.findAll().stream()
                .map(StatsClient::getChiffreAffaires)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Top produits stockes (derniers instantanes disponibles)
        List<TopProduit> top5Produits = topProduitRepository.findAll().stream()
                .sorted(Comparator.comparing(TopProduit::getRang))
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
            occupationParEmplacement.put(emplacement.getCode(), pct);
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
                entreesParDate.merge(flux.getDate(), volume, BigDecimal::add);
            } else if ("SORTIE".equalsIgnoreCase(flux.getTypeFlux())) {
                sortiesParDate.merge(flux.getDate(), volume, BigDecimal::add);
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
        List<StatsClient> financeData = statsClientRepository.findAll().stream()
                .sorted(Comparator.comparing(StatsClient::getDateFin))
                .toList();

        java.util.List<String> caLabels = financeData.stream()
                .map(s -> s.getDateFin() != null
                        ? s.getDateFin().format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"))
                        : "N/A")
                .toList();
        java.util.List<BigDecimal> caData = financeData.stream()
                .map(s -> s.getChiffreAffaires() != null ? s.getChiffreAffaires() : BigDecimal.ZERO)
                .toList();

        // Top produits (camembert)
        java.util.List<String> topProduitLabels = top5Produits.stream()
                .map(tp -> {
                    if (tp.getProduit() != null && tp.getProduit().getNom() != null && !tp.getProduit().getNom().isBlank()) {
                        return tp.getProduit().getNom();
                    }
                    return "Produit #" + (tp.getProduit() != null ? tp.getProduit().getId() : "N/A");
                })
                .toList();
        java.util.List<BigDecimal> topProduitData = top5Produits.stream()
                .map(tp -> tp.getQuantiteTotale() != null ? tp.getQuantiteTotale() : BigDecimal.ZERO)
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
        model.addAttribute("occupationLabels", new java.util.ArrayList<>(occupationParEmplacement.keySet()));
        model.addAttribute("occupationData", new java.util.ArrayList<>(occupationParEmplacement.values()));
        model.addAttribute("ponctualiteLabels", java.util.List.of("À l'heure", "Retard"));
        model.addAttribute("ponctualiteData", java.util.List.of(missionsAlheure, nbRetards));

        return "dashboard/stats";
    }
}
