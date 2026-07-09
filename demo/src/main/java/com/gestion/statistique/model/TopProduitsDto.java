package com.gestion.statistique.model;
import java.math.BigDecimal;

public class TopProduitsDto {
    private Long       produitId;
    private String     produitCode;
    private String     produitNom;
    private String     typeProduit;
    private BigDecimal totalEntrees;
    private BigDecimal totalSorties;
    private BigDecimal totalMouvements;
    private BigDecimal stockActuel;
    private Long       rang;

    // Getters / Setters
    public Long getProduitId() {
        return produitId;
    }
    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }
    public String getProduitCode() {
        return produitCode;
    }
    public void setProduitCode(String produitCode) {
        this.produitCode = produitCode;
    }
    public String getProduitNom() {
        return produitNom;
    }
    public void setProduitNom(String produitNom) {
        this.produitNom = produitNom;
    }
    public String getTypeProduit() {
        return typeProduit;
    }
    public void setTypeProduit(String typeProduit) {
        this.typeProduit = typeProduit;
    }
    public BigDecimal getTotalEntrees() {
        return totalEntrees;
    }
    public void setTotalEntrees(BigDecimal totalEntrees) {
        this.totalEntrees = totalEntrees;
    }
    public BigDecimal getTotalSorties() {
        return totalSorties;
    }
    public void setTotalSorties(BigDecimal totalSorties) {
        this.totalSorties = totalSorties;
    }
    public BigDecimal getTotalMouvements() {
        return totalMouvements;
    }
    public void setTotalMouvements(BigDecimal totalMouvements) {
        this.totalMouvements = totalMouvements;
    }
    public BigDecimal getStockActuel() {
        return stockActuel;
    }
    public void setStockActuel(BigDecimal stockActuel) {
        this.stockActuel = stockActuel;
    }
    public Long getRang() {
        return rang;
    }
    public void setRang(Long rang) {
        this.rang = rang;
    }
}
