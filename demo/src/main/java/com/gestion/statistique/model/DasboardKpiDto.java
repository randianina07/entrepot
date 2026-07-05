package com.gestion.statistique.model;
import java.math.BigDecimal;

public class DasboardKpiDto {
    // v_kpi_occupation_globale
    private BigDecimal tauxOccupationPct;
    private BigDecimal occVariationPts;

    // v_kpi_mouvements_mois
    private Long   nbMouvementsMois;
    private BigDecimal mvtVariationPct;

    // v_kpi_ponctualite_mois
    private BigDecimal tauxPonctualitePct;
    private Long   nbATemps;
    private Long   nbRetard;

    // v_kpi_resultat_net_mois
    private BigDecimal resultatNet;
    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal rnVariationPct;


    // Getters and Setters
    public BigDecimal getTauxOccupationPct() {
        return tauxOccupationPct;
    }
    public void setTauxOccupationPct(BigDecimal tauxOccupationPct) {
        this.tauxOccupationPct = tauxOccupationPct;
    }
    public BigDecimal getOccVariationPts() {
        return occVariationPts;
    }
    public void setOccVariationPts(BigDecimal occVariationPts) {
        this.occVariationPts = occVariationPts;
    }
    public Long getNbMouvementsMois() {
        return nbMouvementsMois;
    }
    public void setNbMouvementsMois(Long nbMouvementsMois) {
        this.nbMouvementsMois = nbMouvementsMois;
    }
    public BigDecimal getMvtVariationPct() {
        return mvtVariationPct;
    }
    public void setMvtVariationPct(BigDecimal mvtVariationPct) {
        this.mvtVariationPct = mvtVariationPct;
    }
    public BigDecimal getTauxPonctualitePct() {
        return tauxPonctualitePct;
    }
    public void setTauxPonctualitePct(BigDecimal tauxPonctualitePct) {
        this.tauxPonctualitePct = tauxPonctualitePct;
    }
    public Long getNbATemps() {
        return nbATemps;
    }
    public void setNbATemps(Long nbATemps) {
        this.nbATemps = nbATemps;
    }
    public Long getNbRetard() {
        return nbRetard;
    }
    public void setNbRetard(Long nbRetard) {
        this.nbRetard = nbRetard;
    }
    public BigDecimal getResultatNet() {
        return resultatNet;
    }
    public void setResultatNet(BigDecimal resultatNet) {
        this.resultatNet = resultatNet;
    }
    public BigDecimal getTotalRecettes() {
        return totalRecettes;
    }
    public void setTotalRecettes(BigDecimal totalRecettes) {
        this.totalRecettes = totalRecettes;
    }
    public BigDecimal getTotalDepenses() {
        return totalDepenses;
    }
    public void setTotalDepenses(BigDecimal totalDepenses) {
        this.totalDepenses = totalDepenses;
    }
    public BigDecimal getRnVariationPct() {
        return rnVariationPct;
    }
    public void setRnVariationPct(BigDecimal rnVariationPct) {
        this.rnVariationPct = rnVariationPct;
    }
}