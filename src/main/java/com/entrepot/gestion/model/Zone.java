package com.entrepot.gestion.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "zones")
public class Zone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    private String libelle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_zone_id", nullable = false)
    private TypeZone typeZone;
    
    @Column(name = "volume_total_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal volumeTotalM3;
    
    public Zone() {
    }
    
    public Zone(Long id, String code, String libelle, TypeZone typeZone, BigDecimal volumeTotalM3) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.typeZone = typeZone;
        this.volumeTotalM3 = volumeTotalM3;
    }
    
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
    
    public TypeZone getTypeZone() {
        return typeZone;
    }
    
    public void setTypeZone(TypeZone typeZone) {
        this.typeZone = typeZone;
    }
    
    public BigDecimal getVolumeTotalM3() {
        return volumeTotalM3;
    }
    
    public void setVolumeTotalM3(BigDecimal volumeTotalM3) {
        this.volumeTotalM3 = volumeTotalM3;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone that = (Zone) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "Zone{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                ", volumeTotalM3=" + volumeTotalM3 +
                '}';
    }
}
