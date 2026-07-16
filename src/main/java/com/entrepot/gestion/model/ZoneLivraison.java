package com.entrepot.gestion.model;

import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

@Entity
@Table(name = "zones_livraison")
// @Getter
// @Setter
public class ZoneLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle" )
    private String libelle;

    @Column(name = "commune")
    private String commune;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "tarif_base")
    private Double tarifBase;

    private Boolean actif;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getTarifBase() {
        return tarifBase;
    }

    public void setTarifBase(Double tarifBase) {
        this.tarifBase = tarifBase;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}

