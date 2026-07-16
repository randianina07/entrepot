package com.entrepot.gestion.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "maintenances_vehicule")
public class Maintenance_vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "type_maintenance_id")
    private Type_maintenance typeMaintenance;

    @Column(name = "date_maintenance")
    private LocalDate dateMaintenance;

    private Double kilometrage;
    private Double cout;
    private String description;

    @Column(name = "prochaine_maintenance")
    private LocalDate prochaineMaintenance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Type_maintenance getTypeMaintenance() {
        return typeMaintenance;
    }

    public void setTypeMaintenance(Type_maintenance typeMaintenance) {
        this.typeMaintenance = typeMaintenance;
    }

    public LocalDate getDateMaintenance() {
        return dateMaintenance;
    }

    public void setDateMaintenance(LocalDate dateMaintenance) {
        this.dateMaintenance = dateMaintenance;
    }

    public Double getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(Double kilometrage) {
        this.kilometrage = kilometrage;
    }

    public Double getCout() {
        return cout;
    }

    public void setCout(Double cout) {
        this.cout = cout;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getProchaineMaintenance() {
        return prochaineMaintenance;
    }

    public void setProchaineMaintenance(LocalDate prochaineMaintenance) {
        this.prochaineMaintenance = prochaineMaintenance;
    }
}
