package entrepot.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;


    @Column(name = "libelle", length = 150)
    private String libelle;


    @ManyToOne
    @JoinColumn(name = "type_zone_id", nullable = false)
    private TypeZone typeZone;


    @Column(name = "volume_total_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal volumeTotalM3;


    public Zone() {
    }


    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public TypeZone getTypeZone() {
        return typeZone;
    }

    public BigDecimal getVolumeTotalM3() {
        return volumeTotalM3;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setTypeZone(TypeZone typeZone) {
        this.typeZone = typeZone;
    }

    public void setVolumeTotalM3(BigDecimal volumeTotalM3) {
        this.volumeTotalM3 = volumeTotalM3;
    }
}