package main.java.com.example.demo.Model;

@Entity
@Table(name = "etage")
public class Etage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String libelle;
    long zone_id;

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
    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    public long getZone_id() {
        return zone_id;
    }
    public void setZone_id(long zone_id) {
        this.zone_id = zone_id;
    }
    
}
