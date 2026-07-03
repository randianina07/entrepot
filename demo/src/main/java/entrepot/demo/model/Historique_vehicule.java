package entrepot.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.Setter;

@Entity
@Table(name = "historique_vehicule")
// @Getter
// @Setter
public class Historique_vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    Mission_logistique mission;

    LocalDateTime date_depart;
    LocalDateTime date_arrivee;
    Double kilometrage_Depart;
    Double kilometrage_arrivee;

    Double distance_parcourue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Mission_logistique getMission() {
        return mission;
    }

    public void setMission(Mission_logistique mission) {
        this.mission = mission;
    }

    public LocalDateTime getDate_depart() {
        return date_depart;
    }

    public void setDate_depart(LocalDateTime date_depart) {
        this.date_depart = date_depart;
    }

    public LocalDateTime getDate_arrivee() {
        return date_arrivee;
    }

    public void setDate_arrivee(LocalDateTime date_arrivee) {
        this.date_arrivee = date_arrivee;
    }

    public Double getKilometrage_Depart() {
        return kilometrage_Depart;
    }

    public void setKilometrage_Depart(Double kilometrage_Depart) {
        this.kilometrage_Depart = kilometrage_Depart;
    }

    public Double getKilometrage_arrivee() {
        return kilometrage_arrivee;
    }

    public void setKilometrage_arrivee(Double kilometrage_arrivee) {
        this.kilometrage_arrivee = kilometrage_arrivee;
    }

    public Double getDistance_parcourue() {
        return distance_parcourue;
    }

    public void setDistance_parcourue(Double distance_parcourue) {
        this.distance_parcourue = distance_parcourue;
    }
}
