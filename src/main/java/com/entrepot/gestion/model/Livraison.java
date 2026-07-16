package com.entrepot.gestion.model;

import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "livraisons")
// @Getter
// @Setter
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private MissionLogistique mission;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Utilisateur client;

    @Column(name = "adresse_livraison")
    private String adresseLivraison;

    @ManyToOne
    @JoinColumn(name = "zone_livraison_id")
    private ZoneLivraison zoneLivraison;

    @Column(name = "poids_total")
    private Double poidsTotal;

    @Column(name = "volume_total")
    private Double volumeTotal;

    @Column(name = "date_prevue")
    private LocalDateTime datePrevue;

    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison;

    @Column(name = "montant_livraison")
    private Double montantLivraison;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MissionLogistique getMission() {
        return mission;
    }

    public void setMission(MissionLogistique mission) {
        this.mission = mission;
    }

    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public ZoneLivraison getZoneLivraison() {
        return zoneLivraison;
    }

    public void setZoneLivraison(ZoneLivraison zoneLivraison) {
        this.zoneLivraison = zoneLivraison;
    }

    public Double getPoidsTotal() {
        return poidsTotal;
    }

    public void setPoidsTotal(Double poidsTotal) {
        this.poidsTotal = poidsTotal;
    }

    public Double getVolumeTotal() {
        return volumeTotal;
    }

    public void setVolumeTotal(Double volumeTotal) {
        this.volumeTotal = volumeTotal;
    }

    public LocalDateTime getDatePrevue() {
        return datePrevue;
    }

    public void setDatePrevue(LocalDateTime datePrevue) {
        this.datePrevue = datePrevue;
    }

    public LocalDateTime getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDateTime dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public Double getMontantLivraison() {
        return montantLivraison;
    }

    public void setMontantLivraison(Double montantLivraison) {
        this.montantLivraison = montantLivraison;
    }

}
