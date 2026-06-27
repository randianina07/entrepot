package main.java.com.example.demo.Model;

@Entity
@Table(name = "emplacement")
public class Emplacement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String code;
    long etage_id;
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
