package com.gestion.entrepot.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "top_produits")
public class TopProduit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_snapshot", nullable = false)
    private LocalDate dateSnapshot;
    
    @Column(nullable = false)
    private Integer rang;
    
    @Column(name = "quantite_totale", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantiteTotale;
    
    @Column(name = "duree_moyenne_stockage_jours", precision = 8, scale = 2)
    private BigDecimal dureeMoyenneStockageJours;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    public TopProduit() {
    }
    
    public TopProduit(Long id, LocalDate dateSnapshot, Integer rang, BigDecimal quantiteTotale, BigDecimal dureeMoyenneStockageJours, Produit produit) {
        this.id = id;
        this.dateSnapshot = dateSnapshot;
        this.rang = rang;
        this.quantiteTotale = quantiteTotale;
        this.dureeMoyenneStockageJours = dureeMoyenneStockageJours;
        this.produit = produit;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDateSnapshot() {
        return dateSnapshot;
    }
    
    public void setDateSnapshot(LocalDate dateSnapshot) {
        this.dateSnapshot = dateSnapshot;
    }
    
    public Integer getRang() {
        return rang;
    }
    
    public void setRang(Integer rang) {
        this.rang = rang;
    }
    
    public BigDecimal getQuantiteTotale() {
        return quantiteTotale;
    }
    
    public void setQuantiteTotale(BigDecimal quantiteTotale) {
        this.quantiteTotale = quantiteTotale;
    }
    
    public BigDecimal getDureeMoyenneStockageJours() {
        return dureeMoyenneStockageJours;
    }
    
    public void setDureeMoyenneStockageJours(BigDecimal dureeMoyenneStockageJours) {
        this.dureeMoyenneStockageJours = dureeMoyenneStockageJours;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopProduit that = (TopProduit) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "TopProduit{" +
                "id=" + id +
                ", dateSnapshot=" + dateSnapshot +
                ", rang=" + rang +
                ", quantiteTotale=" + quantiteTotale +
                '}';
    }
}
