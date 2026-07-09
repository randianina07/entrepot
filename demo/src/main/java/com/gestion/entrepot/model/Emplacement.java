package com.gestion.entrepot.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "emplacements")
public class Emplacement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 40)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;
    
    @Column(name = "capacite_volume_m3", nullable = false, precision = 10, scale = 3)
    private BigDecimal capaciteVolumeM3;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    public Emplacement() {
    }
    
    public Emplacement(Long id, String code, Zone zone, BigDecimal capaciteVolumeM3, Boolean actif) {
        this.id = id;
        this.code = code;
        this.zone = zone;
        this.capaciteVolumeM3 = capaciteVolumeM3;
        this.actif = actif;
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
    
    public Zone getZone() {
        return zone;
    }
    
    public void setZone(Zone zone) {
        this.zone = zone;
    }
    
    public BigDecimal getCapaciteVolumeM3() {
        return capaciteVolumeM3;
    }
    
    public void setCapaciteVolumeM3(BigDecimal capaciteVolumeM3) {
        this.capaciteVolumeM3 = capaciteVolumeM3;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emplacement that = (Emplacement) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "Emplacement{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", capaciteVolumeM3=" + capaciteVolumeM3 +
                ", actif=" + actif +
                '}';
    }
}
