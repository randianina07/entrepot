package com.entrepot.gestion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "missions_logistiques")
public class MissionLogistique {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reference_mission", nullable = false, unique = true)
    private String referenceMission;
    
    @Column(name = "date_depart_prevue")
    private LocalDateTime dateDepartPrevue;
    
    @Column(name = "date_arrivee_prevue")
    private LocalDateTime dateArriveePrevue;
    
    @Column(name = "date_depart_reelle")
    private LocalDateTime dateDepartReelle;
    
    @Column(name = "date_arrivee_reelle")
    private LocalDateTime dateArriveeReelle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_id", nullable = false)
    private Chauffeur chauffeur;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_mission_id", nullable = false)
    private StatutMission statutMission;
    
    private String observations;
    
    // Constructors
    public MissionLogistique() {}
    
    public MissionLogistique(String referenceMission, LocalDateTime dateDepartPrevue, LocalDateTime dateArriveePrevue,
                           LocalDateTime dateDepartReelle, LocalDateTime dateArriveeReelle,
                           Vehicule vehicule, Chauffeur chauffeur, StatutMission statutMission, String observations) {
        this.referenceMission = referenceMission;
        this.dateDepartPrevue = dateDepartPrevue;
        this.dateArriveePrevue = dateArriveePrevue;
        this.dateDepartReelle = dateDepartReelle;
        this.dateArriveeReelle = dateArriveeReelle;
        this.vehicule = vehicule;
        this.chauffeur = chauffeur;
        this.statutMission = statutMission;
        this.observations = observations;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getReferenceMission() {
        return referenceMission;
    }
    
    public void setReferenceMission(String referenceMission) {
        this.referenceMission = referenceMission;
    }
    
    public LocalDateTime getDateDepartPrevue() {
        return dateDepartPrevue;
    }
    
    public void setDateDepartPrevue(LocalDateTime dateDepartPrevue) {
        this.dateDepartPrevue = dateDepartPrevue;
    }
    
    public LocalDateTime getDateArriveePrevue() {
        return dateArriveePrevue;
    }
    
    public void setDateArriveePrevue(LocalDateTime dateArriveePrevue) {
        this.dateArriveePrevue = dateArriveePrevue;
    }
    
    public LocalDateTime getDateDepartReelle() {
        return dateDepartReelle;
    }
    
    public void setDateDepartReelle(LocalDateTime dateDepartReelle) {
        this.dateDepartReelle = dateDepartReelle;
    }
    
    public LocalDateTime getDateArriveeReelle() {
        return dateArriveeReelle;
    }
    
    public void setDateArriveeReelle(LocalDateTime dateArriveeReelle) {
        this.dateArriveeReelle = dateArriveeReelle;
    }
    
    public Vehicule getVehicule() {
        return vehicule;
    }
    
    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }
    
    public Chauffeur getChauffeur() {
        return chauffeur;
    }
    
    public void setChauffeur(Chauffeur chauffeur) {
        this.chauffeur = chauffeur;
    }
    
    public StatutMission getStatutMission() {
        return statutMission;
    }
    
    public void setStatutMission(StatutMission statutMission) {
        this.statutMission = statutMission;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
}
