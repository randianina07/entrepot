package entrepot.demo.model;

import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

@Entity
@Table(name = "vehicules")
//@Getter
//@Setter
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String immatriculation;

    private String marque;

    private String modele;

    private int annee;

    @Column(name = "capacite_volume_m3")
    private double capaciteVolume;

    @Column(name = "capacite_charge_kg")
    private double capaciteChargekg;

    @Column(name = "kilometrage_actuel")
    private double kilometrage;

    @ManyToOne
    @JoinColumn(name = "type_vehicule_id")
    private Type_vehicule typeVehicule;

    @ManyToOne
    @JoinColumn(name = "statut_vehicule_id")
    private Statut_vehicule statut;

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

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public double getCapaciteVolume() {
        return capaciteVolume;
    }

    public void setCapaciteVolume(double capaciteVolume) {
        this.capaciteVolume = capaciteVolume;
    }

    public double getCapaciteChargekg() {
        return capaciteChargekg;
    }

    public void setCapaciteChargekg(double capaciteChargekg) {
        this.capaciteChargekg = capaciteChargekg;
    }

    public double getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(double kilometrage) {
        this.kilometrage = kilometrage;
    }

    public Type_vehicule getTypeVehicule() {
        return typeVehicule;
    }

    public void setTypeVehicule(Type_vehicule typeVehicule) {
        this.typeVehicule = typeVehicule;
    }

    public Statut_vehicule getStatut() {
        return statut;
    }

    public void setStatut(Statut_vehicule statut) {
        this.statut = statut;
    }
}
