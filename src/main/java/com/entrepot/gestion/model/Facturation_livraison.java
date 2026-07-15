package com.entrepot.gestion.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "facturation_livraison")

public class Facturation_livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @ManyToOne
    @JoinColumn(name = "tarif_livraison_id")
    private Tarif_livraison tarifLivraison;

    @Column(name = "poids_facture")
    private Double poidsFacture;

    @Column(name = "volume_facture")
    private Double volumeFacture;

    @Column(name = "montant_calcule")
    private Double montantCalcule;

    @Column(name = "montant_final")
    private Double montantFinal;

    @Column(name = "date_facturation")
    private LocalDateTime dateFacturation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public Tarif_livraison getTarifLivraison() {
        return tarifLivraison;
    }

    public void setTarifLivraison(Tarif_livraison tarifLivraison) {
        this.tarifLivraison = tarifLivraison;
    }

    public Double getPoidsFacture() {
        return poidsFacture;
    }

    public void setPoidsFacture(Double poidsFacture) {
        this.poidsFacture = poidsFacture;
    }

    public Double getVolumeFacture() {
        return volumeFacture;
    }

    public void setVolumeFacture(Double volumeFacture) {
        this.volumeFacture = volumeFacture;
    }

    public Double getMontantCalcule() {
        return montantCalcule;
    }

    public void setMontantCalcule(Double montantCalcule) {
        this.montantCalcule = montantCalcule;
    }

    public Double getMontantFinal() {
        return montantFinal;
    }

    public void setMontantFinal(Double montantFinal) {
        this.montantFinal = montantFinal;
    }

    public LocalDateTime getDateFacturation() {
        return dateFacturation;
    }

    public void setDateFacturation(LocalDateTime dateFacturation) {
        this.dateFacturation = dateFacturation;
    }

    // Getters et Setters
}
