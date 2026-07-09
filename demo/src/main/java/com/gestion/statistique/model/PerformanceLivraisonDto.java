package com.gestion.statistique.model;
import java.math.BigDecimal;

public class PerformanceLivraisonDto {
    private Long       nbTotal;
    private Long       nbLivrees;
    private Long       nbATemps;
    private Long       nbRetard;
    private Long       nbEnAttente;
    private BigDecimal tauxPonctualitePct;
    private BigDecimal retardMoyenHeures;

    // Getters / Setters
    public Long getNbTotal() {
        return nbTotal;
    }
    public void setNbTotal(Long nbTotal) {
        this.nbTotal = nbTotal;
    }
    public Long getNbLivrees() {
        return nbLivrees;
    }
    public void setNbLivrees(Long nbLivrees) {
        this.nbLivrees = nbLivrees;
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
    public Long getNbEnAttente() {
        return nbEnAttente;
    }
    public void setNbEnAttente(Long nbEnAttente) {
        this.nbEnAttente = nbEnAttente;
    }
    public BigDecimal getTauxPonctualitePct() {
        return tauxPonctualitePct;
    }
    public void setTauxPonctualitePct(BigDecimal tauxPonctualitePct) {
        this.tauxPonctualitePct = tauxPonctualitePct;
    }
    public BigDecimal getRetardMoyenHeures() {
        return retardMoyenHeures;
    }
    public void setRetardMoyenHeures(BigDecimal retardMoyenHeures) {
        this.retardMoyenHeures = retardMoyenHeures;
    }
}