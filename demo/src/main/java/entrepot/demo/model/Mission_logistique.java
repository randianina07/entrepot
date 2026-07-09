package entrepot.demo.model;

import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "missions_logistiques")
// @Getter
// @Setter
public class Mission_logistique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_mission")
    private String referenceMission;

    @Column(name = "date_depart_prevue")
    private LocalDateTime dateDepartPrevue;

    @Column(name = "date_arrivee_prevue")
    private LocalDateTime dateArriveePrevue;

    @Column(name = "date_depart_reelle")
    private LocalDateTime dateDepartReelle;

    @Column(name = "date_arrivee_reelle")
    private LocalDateTime dateArriveeReelle;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "chauffeur_id")
    private Chauffeurs chauffeur;

    @ManyToOne
    @JoinColumn(name = "statut_mission_id")
    private Statuts_mission statutMission;

    @Column(name = "observations")
    private String observations;

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

    public Statuts_mission getStatutMission() {
        return statutMission;
    }

    public void setStatutMission(Statuts_mission statutMission) {
        this.statutMission = statutMission;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Chauffeurs getChauffeur() {
        return chauffeur;
    }

    public void setChauffeur(Chauffeurs chauffeur) {
        this.chauffeur = chauffeur;
    }

    // Getters et Setters
}
