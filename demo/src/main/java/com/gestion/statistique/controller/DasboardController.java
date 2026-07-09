package com.gestion.statistique.controller;
import com.gestion.statistique.model.*;
import com.gestion.statistique.repository.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller 
@RequestMapping("/dashboard")
public class DasboardController {
    private final DasboardRepository dasboardRepository;
    private final OccupationZoneRepository occupationZoneRepository;
    private final FluxJournalierRepository fluxJournalierRepository;
    private final FinanceRepository financeRepository;
    private final PerformanceLivraisonRepository performanceLivraisonRepository;
    private final CaClientsRepository caClientsRepository;
    private final TopProduitsRepository topProduitsRepository;

    public DasboardController(DasboardRepository dasboardRepository,
                              OccupationZoneRepository occupationZoneRepository,
                              FluxJournalierRepository fluxJournalierRepository,
                              FinanceRepository financeRepository,
                              PerformanceLivraisonRepository performanceLivraisonRepository,
                              CaClientsRepository caClientsRepository,
                              TopProduitsRepository topProduitsRepository) {
        this.dasboardRepository = dasboardRepository;
        this.occupationZoneRepository = occupationZoneRepository;
        this.fluxJournalierRepository = fluxJournalierRepository;
        this.financeRepository = financeRepository;
        this.performanceLivraisonRepository = performanceLivraisonRepository;
        this.caClientsRepository = caClientsRepository;
        this.topProduitsRepository = topProduitsRepository;
    }

    @GetMapping("/stats")
    public String getDashboardStats(Model model,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d1From,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d1To,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d2From,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d2To,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d3From,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d3To,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d4From,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d4To,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d5From,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d5To,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d6From,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate d6To) {
        // KPIs
        DasboardKpiDto kpis = dasboardRepository.getKpis();
        normalizeKpis(kpis);
        model.addAttribute("kpis", kpis);

        // Occupation Zones
        List<OccupationZoneDto> occupationZones = (d1From != null && d1To != null) ?
                occupationZoneRepository.getOccupationZonesByPeriod(d1From, d1To) :
                occupationZoneRepository.getOccupationZones();
        model.addAttribute("occupationZones", occupationZones);

        // Flux Journalier
        List<FluxJournalierDto> fluxJournaliers = (d2From != null && d2To != null) ?
                fluxJournalierRepository.getFluxByPeriod(d2From, d2To) :
                fluxJournalierRepository.getFluxJournalier7jours();
        if (fluxJournaliers.isEmpty() && d2From == null && d2To == null) {
            fluxJournaliers = fluxJournalierRepository.getFluxJournalierHistorique();
        }
        model.addAttribute("fluxJournaliers", fluxJournaliers);

        // Finance
        List<FinanceMensuelleDto> financeData = (d3From != null && d3To != null) ?
                financeRepository.getFinanceByPeriod(d3From, d3To) :
                financeRepository.getFinance6mois();
        if (financeData.isEmpty() && d3From == null && d3To == null) {
            financeData = financeRepository.getFinanceHistorique();
        }
        model.addAttribute("financeData", financeData);

        // Performance Livraison
        PerformanceLivraisonDto performanceLivr = (d4From != null && d4To != null) ?
                performanceLivraisonRepository.getPerformanceLivraisonByPeriod(d4From, d4To) :
                performanceLivraisonRepository.getPerformanceLivraison();
        normalizePerformance(performanceLivr);
        model.addAttribute("performanceLivr", performanceLivr);

        // CA Clients
        List<CaClientsDto> caClients = (d6From != null && d6To != null) ?
                caClientsRepository.getCaClientsByPeriod(d6From, d6To) :
                caClientsRepository.getCaClients();
        model.addAttribute("caClients", caClients);
        

        // Top produits
        List<TopProduitsDto> top5Produits = (d5From != null && d5To != null) ?
                topProduitsRepository.getTopProduitsByPeriod(d5From, d5To) :
                topProduitsRepository.getTopProduits();
        model.addAttribute("top5Produits", top5Produits);

         model.addAttribute("periodeAffichee", LocalDate.now());

         
        return "dashboard/stats";
    }

        private void normalizeKpis(DasboardKpiDto kpis) {
                if (kpis.getTauxOccupationPct() == null) kpis.setTauxOccupationPct(java.math.BigDecimal.ZERO);
                if (kpis.getOccVariationPts() == null) kpis.setOccVariationPts(java.math.BigDecimal.ZERO);
                if (kpis.getNbMouvementsMois() == null) kpis.setNbMouvementsMois(0L);
                if (kpis.getMvtVariationPct() == null) kpis.setMvtVariationPct(java.math.BigDecimal.ZERO);
                if (kpis.getTauxPonctualitePct() == null) kpis.setTauxPonctualitePct(java.math.BigDecimal.ZERO);
                if (kpis.getNbATemps() == null) kpis.setNbATemps(0L);
                if (kpis.getNbRetard() == null) kpis.setNbRetard(0L);
                if (kpis.getResultatNet() == null) kpis.setResultatNet(java.math.BigDecimal.ZERO);
                if (kpis.getTotalRecettes() == null) kpis.setTotalRecettes(java.math.BigDecimal.ZERO);
                if (kpis.getTotalDepenses() == null) kpis.setTotalDepenses(java.math.BigDecimal.ZERO);
                if (kpis.getRnVariationPct() == null) kpis.setRnVariationPct(java.math.BigDecimal.ZERO);
        }

        private void normalizePerformance(PerformanceLivraisonDto performanceLivr) {
                if (performanceLivr.getNbTotal() == null) performanceLivr.setNbTotal(0L);
                if (performanceLivr.getNbLivrees() == null) performanceLivr.setNbLivrees(0L);
                if (performanceLivr.getNbATemps() == null) performanceLivr.setNbATemps(0L);
                if (performanceLivr.getNbRetard() == null) performanceLivr.setNbRetard(0L);
                if (performanceLivr.getNbEnAttente() == null) performanceLivr.setNbEnAttente(0L);
                if (performanceLivr.getTauxPonctualitePct() == null) performanceLivr.setTauxPonctualitePct(java.math.BigDecimal.ZERO);
                if (performanceLivr.getRetardMoyenHeures() == null) performanceLivr.setRetardMoyenHeures(java.math.BigDecimal.ZERO);
        }
    
    
}
