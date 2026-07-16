package com.entrepot.gestion.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "zone")
public class Zone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "libelle", length = 150)
    private String libelle;

    @Column(name = "volume_total_m3", precision = 12, scale = 3)
    private BigDecimal volume_total_m3;

    @ManyToOne
    @JoinColumn(name = "allees_id")
    private Allees allees;

    @ManyToOne
    @JoinColumn(name = "type_zone_id")
    private Type_zone type_zone;
    
    
    public Allees getAllees() {
        return allees;
    }
    public void setAllees(Allees allees) {
        this.allees = allees;
    }
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
    public Type_zone getType_zone() {
        return type_zone;
    }
    public void setType_zone(Type_zone type_zone) {
        this.type_zone = type_zone;
    }
    public BigDecimal getVolume_total_m3() {
        return volume_total_m3;
    }
    public void setVolume_total_m3(BigDecimal volume_total_m3) {
        this.volume_total_m3 = volume_total_m3;
    }
    
}
