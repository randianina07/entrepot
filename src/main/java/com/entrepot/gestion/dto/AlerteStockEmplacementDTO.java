package com.entrepot.gestion.dto;

import java.math.BigDecimal;

public class AlerteStockEmplacementDTO {

    private String emplacement;
    private BigDecimal pourcentage;
    private BigDecimal stockActuel;
    private BigDecimal capacite;

    public AlerteStockEmplacementDTO() {
    }

    public AlerteStockEmplacementDTO(String emplacement, BigDecimal pourcentage, BigDecimal stockActuel, BigDecimal capacite) {
        this.emplacement = emplacement;
        this.pourcentage = pourcentage;
        this.stockActuel = stockActuel;
        this.capacite = capacite;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public BigDecimal getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(BigDecimal pourcentage) {
        this.pourcentage = pourcentage;
    }

    public BigDecimal getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(BigDecimal stockActuel) {
        this.stockActuel = stockActuel;
    }

    public BigDecimal getCapacite() {
        return capacite;
    }

    public void setCapacite(BigDecimal capacite) {
        this.capacite = capacite;
    }
}