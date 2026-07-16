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
@Table(name = "stats_clients")
public class StatsClient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;
    
    @Column(name = "volume_stocke_m3", precision = 12, scale = 3)
    private BigDecimal volumeStockeM3;
    
    @Column(name = "duree_moyenne_jours", precision = 8, scale = 2)
    private BigDecimal dureeMoyenneJours;
    
    @Column(name = "nb_entrees")
    private Integer nbEntrees = 0;
    
    @Column(name = "nb_sorties")
    private Integer nbSorties = 0;
    
    @Column(name = "chiffre_affaires", precision = 14, scale = 2)
    private BigDecimal chiffreAffaires = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Utilisateur client;
    
    public StatsClient() {
    }
    
    public StatsClient(Long id, LocalDate dateDebut, LocalDate dateFin, BigDecimal volumeStockeM3, BigDecimal dureeMoyenneJours, Integer nbEntrees, Integer nbSorties, BigDecimal chiffreAffaires, Utilisateur client) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.volumeStockeM3 = volumeStockeM3;
        this.dureeMoyenneJours = dureeMoyenneJours;
        this.nbEntrees = nbEntrees != null ? nbEntrees : 0;
        this.nbSorties = nbSorties != null ? nbSorties : 0;
        this.chiffreAffaires = chiffreAffaires != null ? chiffreAffaires : BigDecimal.ZERO;
        this.client = client;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public BigDecimal getVolumeStockeM3() {
        return volumeStockeM3;
    }
    
    public void setVolumeStockeM3(BigDecimal volumeStockeM3) {
        this.volumeStockeM3 = volumeStockeM3;
    }
    
    public BigDecimal getDureeMoyenneJours() {
        return dureeMoyenneJours;
    }
    
    public void setDureeMoyenneJours(BigDecimal dureeMoyenneJours) {
        this.dureeMoyenneJours = dureeMoyenneJours;
    }
    
    public Integer getNbEntrees() {
        return nbEntrees;
    }
    
    public void setNbEntrees(Integer nbEntrees) {
        this.nbEntrees = nbEntrees != null ? nbEntrees : 0;
    }
    
    public Integer getNbSorties() {
        return nbSorties;
    }
    
    public void setNbSorties(Integer nbSorties) {
        this.nbSorties = nbSorties != null ? nbSorties : 0;
    }
    
    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }
    
    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires != null ? chiffreAffaires : BigDecimal.ZERO;
    }
    
    public Utilisateur getClient() {
        return client;
    }
    
    public void setClient(Utilisateur client) {
        this.client = client;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsClient that = (StatsClient) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "StatsClient{" +
                "id=" + id +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", nbEntrees=" + nbEntrees +
                ", nbSorties=" + nbSorties +
                '}';
    }
}
