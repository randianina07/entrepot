package com.entrepot.gestion.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicules")
public class Vehicule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "immatriculation", nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(name = "marque", length = 60)
    private String marque;

    @Column(name = "modele", length = 60)
    private String modele;

    @Column(name = "annee")
    private Integer annee;

    @Column(name = "capacite_volume_m3", nullable = false, precision = 10, scale = 3)
    private BigDecimal capaciteVolumeM3;

    @Column(name = "capacite_charge_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal capaciteChargeKg;

    @Column(name = "kilometrage_actuel", nullable = false, precision = 10, scale = 2)
    private BigDecimal kilometrageActuel = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_vehicule_id", nullable = false)
    private TypeVehicule typeVehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_vehicule_id", nullable = false)
    private StatutVehicule statutVehicule;
    
    // Constructors
    public Vehicule() {}
    
    public Vehicule(String immatriculation, String marque, String modele, Integer annee,
                   BigDecimal capaciteVolumeM3, BigDecimal capaciteChargeKg, BigDecimal kilometrageActuel,
                   TypeVehicule typeVehicule, StatutVehicule statutVehicule) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.capaciteVolumeM3 = capaciteVolumeM3;
        this.capaciteChargeKg = capaciteChargeKg;
        this.kilometrageActuel = kilometrageActuel;
        this.typeVehicule = typeVehicule;
        this.statutVehicule = statutVehicule;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getImmatriculation() {
        return immatriculation;
    }
    
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }
    
    public String getMarque() {
        return marque;
    }
    
    public void setMarque(String marque) {
        this.marque = marque;
    }
    
    public String getModele() {
        return modele;
    }
    
    public void setModele(String modele) {
        this.modele = modele;
    }
    
    public Integer getAnnee() {
        return annee;
    }
    
    public void setAnnee(Integer annee) {
        this.annee = annee;
    }
    
    public BigDecimal getCapaciteVolumeM3() {
        return capaciteVolumeM3;
    }
    
    public void setCapaciteVolumeM3(BigDecimal capaciteVolumeM3) {
        this.capaciteVolumeM3 = capaciteVolumeM3;
    }
    
    public BigDecimal getCapaciteChargeKg() {
        return capaciteChargeKg;
    }
    
    public void setCapaciteChargeKg(BigDecimal capaciteChargeKg) {
        this.capaciteChargeKg = capaciteChargeKg;
    }
    
    public BigDecimal getKilometrageActuel() {
        return kilometrageActuel;
    }
    
    public void setKilometrageActuel(BigDecimal kilometrageActuel) {
        this.kilometrageActuel = kilometrageActuel;
    }
    
    public TypeVehicule getTypeVehicule() {
        return typeVehicule;
    }
    
    public void setTypeVehicule(TypeVehicule typeVehicule) {
        this.typeVehicule = typeVehicule;
    }
    
    public StatutVehicule getStatutVehicule() {
        return statutVehicule;
    }
    
    public void setStatutVehicule(StatutVehicule statutVehicule) {
        this.statutVehicule = statutVehicule;
    }
    
    // Helper method for full name
    public String getFullName() {
        if (modele != null && marque != null) {
            return marque + " " + modele + " (" + immatriculation + ")";
        } else if (marque != null) {
            return marque + " (" + immatriculation + ")";
        }
        return immatriculation;
    }
}
