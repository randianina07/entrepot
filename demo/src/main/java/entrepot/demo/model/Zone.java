package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zone")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    @ManyToOne
    @JoinColumn(name = "allees_id", nullable = false)
    private Allee allee;

    @ManyToOne
    @JoinColumn(name = "type_zone_id", nullable = false)
    private TypeZone typeZone;

    private double volume_total_m3;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Allee getAllee() { return allee; }
    public void setAllee(Allee allee) { this.allee = allee; }
    public TypeZone getTypeZone() { return typeZone; }
    public void setTypeZone(TypeZone typeZone) { this.typeZone = typeZone; }
    public double getVolume_total_m3() { return volume_total_m3; }
    public void setVolume_total_m3(double volume_total_m3) { this.volume_total_m3 = volume_total_m3; }
}
