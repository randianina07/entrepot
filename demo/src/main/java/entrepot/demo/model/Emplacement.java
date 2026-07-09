package entrepot.demo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "emplacement")
public class Emplacement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String code;

    @ManyToOne
    @JoinColumn(name = "etage_id", referencedColumnName = "id")
    Etage etage;

    @ManyToOne
    @JoinColumn(name = "allee_id", referencedColumnName = "id")
    Allee allee;

    public Allee getAllee() {
        return allee;
    }
    public void setAllee(Allee allee) {
        this.allee = allee;
    }
    double capacite_volume_m3;
    boolean actif;
    double charge_max;

    int colonne;

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
    public double getCharge_max() {
        return charge_max;
    }
    public void setCharge_max(double charge_max) {
        this.charge_max = charge_max;
    }
    public int getColonne() {
        return colonne;
    }
    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

}
