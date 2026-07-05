package com.gestion.statistique.model;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FinanceMensuelleDto {
    private LocalDate  mois;
    private String     moisLibelle;
    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal resultatNet;
    private BigDecimal tauxChargePct;

    // Getters / Setters
    public LocalDate getMois() {
        return mois;
    }
    public void setMois(LocalDate mois) {
        this.mois = mois;
    }
    public String getMoisLibelle() {
        return moisLibelle;
    }
    public void setMoisLibelle(String moisLibelle) {
        this.moisLibelle = moisLibelle;
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
    public BigDecimal getResultatNet() {
        return resultatNet;
    }
    public void setResultatNet(BigDecimal resultatNet) {
        this.resultatNet = resultatNet;
    }
    public BigDecimal getTauxChargePct() {
        return tauxChargePct;
    }
    public void setTauxChargePct(BigDecimal tauxChargePct) {
        this.tauxChargePct = tauxChargePct;
    }
}