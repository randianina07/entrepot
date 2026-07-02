package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "emplacement")
public class Emplacement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String codes;
    
    @ManyToOne
    @JoinColumn(name = "etage_id")
    Etage etage;

    @ManyToOne
    @JoinColumn(name = "colonne_id")
    Colonne colonne;

    @ManyToOne
    @JoinColumn(name = "allee_id")
    Allee allee;

    double capacite_volume_m3;
    boolean actif;
    double charge_max;
    
    public Allee getAllee() {
        return allee;
    }
    public void setAllee(Allee allee) {
        this.allee = allee;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCodes() {
        return codes;
    }
    public void setCodes(String code) {
        this.codes = code;
    }
    public double getCapacite_volume_m3() {
        return capacite_volume_m3;
    }
    public void setCapacite_volume_m3(double capacite_volume_m3) {
        this.capacite_volume_m3 = capacite_volume_m3;
    }
    public boolean isActif() {
        return actif;
    }
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    public Etage getEtage() {
        return etage;
    }
    public void setEtage(Etage etage) {
        this.etage = etage;
    }
}
