package com.entrepot.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "flux_entrees_sorties")
public class FluxEntreesSorties {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "type_flux", nullable = false, length = 10)
    private String typeFlux;
    
    @Column(name = "type_detail", length = 50)
    private String typeDetail;
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantite;
    
    @Column(name = "volume_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal volumeM3;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mouvement_id", nullable = false)
    private Mouvement mouvement;
    
    public FluxEntreesSorties() {
    }
    
    public FluxEntreesSorties(Long id, LocalDate date, String typeFlux, String typeDetail, BigDecimal quantite, BigDecimal volumeM3, Mouvement mouvement) {
        this.id = id;
        this.date = date;
        this.typeFlux = typeFlux;
        this.typeDetail = typeDetail;
        this.quantite = quantite;
        this.volumeM3 = volumeM3;
        this.mouvement = mouvement;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getTypeFlux() {
        return typeFlux;
    }
    
    public void setTypeFlux(String typeFlux) {
        this.typeFlux = typeFlux;
    }
    
    public String getTypeDetail() {
        return typeDetail;
    }
    
    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }
    
    public BigDecimal getQuantite() {
        return quantite;
    }
    
    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }
    
    public BigDecimal getVolumeM3() {
        return volumeM3;
    }
    
    public void setVolumeM3(BigDecimal volumeM3) {
        this.volumeM3 = volumeM3;
    }
    
    public Mouvement getMouvement() {
        return mouvement;
    }
    
    public void setMouvement(Mouvement mouvement) {
        this.mouvement = mouvement;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FluxEntreesSorties that = (FluxEntreesSorties) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "FluxEntreesSorties{" +
                "id=" + id +
                ", date=" + date +
                ", typeFlux='" + typeFlux + '\'' +
                ", quantite=" + quantite +
                ", volumeM3=" + volumeM3 +
                '}';
    }
}
