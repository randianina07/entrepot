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
@Table(name = "produits")
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 40)
    private String code;
    
    @Column(nullable = false, length = 150)
    private String nom;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_produit_id")
    private TypeProduit typeProduit;
    
    @Column(name = "volume_unitaire_m3", nullable = false, precision = 10, scale = 4)
    private BigDecimal volumeUnitaireM3;
    
    @Column(name = "poids_unitaire_kg", precision = 10, scale = 3)
    private BigDecimal poidsUnitaireKg;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    public Produit() {
    }
    
    public Produit(Long id, String code, String nom, String description, TypeProduit typeProduit, BigDecimal volumeUnitaireM3, BigDecimal poidsUnitaireKg, Boolean actif) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.typeProduit = typeProduit;
        this.volumeUnitaireM3 = volumeUnitaireM3;
        this.poidsUnitaireKg = poidsUnitaireKg;
        this.actif = actif;
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
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TypeProduit getTypeProduit() {
        return typeProduit;
    }
    
    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }
    
    public BigDecimal getVolumeUnitaireM3() {
        return volumeUnitaireM3;
    }
    
    public void setVolumeUnitaireM3(BigDecimal volumeUnitaireM3) {
        this.volumeUnitaireM3 = volumeUnitaireM3;
    }
    
    public BigDecimal getPoidsUnitaireKg() {
        return poidsUnitaireKg;
    }
    
    public void setPoidsUnitaireKg(BigDecimal poidsUnitaireKg) {
        this.poidsUnitaireKg = poidsUnitaireKg;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produit produit = (Produit) o;
        return Objects.equals(id, produit.id) && Objects.equals(code, produit.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", actif=" + actif +
                '}';
    }
}
