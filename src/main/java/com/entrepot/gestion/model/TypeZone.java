package com.entrepot.gestion.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "types_zone")
public class TypeZone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    @Column(nullable = false, length = 100)
    private String libelle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_produit_id")
    private TypeProduit typeProduit;
    
    @Column(name = "controle_temperature", nullable = false)
    private Boolean controleTemperature = false;
    
    @Column(name = "acces_restreint", nullable = false)
    private Boolean accesRestreint = false;
    
    @Column(name = "journalisation_acces", nullable = false)
    private Boolean journalisationAcces = false;
    
    @Column(name = "charge_lourde_possible", nullable = false)
    private Boolean chargeLourdePossible = false;
    
    public TypeZone() {
    }
    
    public TypeZone(Long id, String code, String libelle, TypeProduit typeProduit, Boolean controleTemperature, Boolean accesRestreint, Boolean journalisationAcces, Boolean chargeLourdePossible) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.typeProduit = typeProduit;
        this.controleTemperature = controleTemperature;
        this.accesRestreint = accesRestreint;
        this.journalisationAcces = journalisationAcces;
        this.chargeLourdePossible = chargeLourdePossible;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public TypeProduit getTypeProduit() {
        return typeProduit;
    }
    
    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }
    
    public Boolean getControleTemperature() {
        return controleTemperature;
    }
    
    public void setControleTemperature(Boolean controleTemperature) {
        this.controleTemperature = controleTemperature;
    }
    
    public Boolean getAccesRestreint() {
        return accesRestreint;
    }
    
    public void setAccesRestreint(Boolean accesRestreint) {
        this.accesRestreint = accesRestreint;
    }
    
    public Boolean getJournalisationAcces() {
        return journalisationAcces;
    }
    
    public void setJournalisationAcces(Boolean journalisationAcces) {
        this.journalisationAcces = journalisationAcces;
    }
    
    public Boolean getChargeLourdePossible() {
        return chargeLourdePossible;
    }
    
    public void setChargeLourdePossible(Boolean chargeLourdePossible) {
        this.chargeLourdePossible = chargeLourdePossible;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeZone that = (TypeZone) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "TypeZone{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                ", controleTemperature=" + controleTemperature +
                ", accesRestreint=" + accesRestreint +
                '}';
    }
}
