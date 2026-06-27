package entrepot.demo.Model;

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

    String code;
    
    @ManyToOne
    @JoinColumn(name = "etage_id")
    Etage etage;
    double capacite_volume_m3;
    boolean actif;
    double charge_max;

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
    public long getZone_id() {
        return zone_id;
    }
    public void setZone_id(long zone_id) {
        this.zone_id = zone_id;
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

}
