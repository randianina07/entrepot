package com.entrepot.gestion.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stocks_emplacement")
public class StockEmplacement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_id", nullable = false)
    private Emplacement emplacement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantite = BigDecimal.ZERO;
    
    public StockEmplacement() {
    }
    
    public StockEmplacement(Long id, Emplacement emplacement, Produit produit, BigDecimal quantite) {
        this.id = id;
        this.emplacement = emplacement;
        this.produit = produit;
        this.quantite = quantite != null ? quantite : BigDecimal.ZERO;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Emplacement getEmplacement() {
        return emplacement;
    }
    
    public void setEmplacement(Emplacement emplacement) {
        this.emplacement = emplacement;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public BigDecimal getQuantite() {
        return quantite;
    }
    
    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite != null ? quantite : BigDecimal.ZERO;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockEmplacement that = (StockEmplacement) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "StockEmplacement{" +
                "id=" + id +
                ", quantite=" + quantite +
                '}';
    }
}
