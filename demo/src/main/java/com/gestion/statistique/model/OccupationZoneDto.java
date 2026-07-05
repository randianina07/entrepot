package com.gestion.statistique.model;
import java.math.BigDecimal;

public class OccupationZoneDto {
    private Long      zoneId;
    private String    zoneCode;
    private String    zoneLibelle;
    private String    typeZoneCode;
    private BigDecimal capaciteM3;
    private BigDecimal volumeOccupeM3;
    private BigDecimal volumeLibreM3;
    private BigDecimal tauxOccupationPct;
    private String    statutCouleur;

    // Getters / Setters
    public Long getZoneId() {
        return zoneId;
    }
    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }
    public String getZoneCode() {
        return zoneCode;
    }
    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }
    public String getZoneLibelle() {
        return zoneLibelle;
    }
    public void setZoneLibelle(String zoneLibelle) {
        this.zoneLibelle = zoneLibelle;
    }
    public String getTypeZoneCode() {
        return typeZoneCode;
    }
    public void setTypeZoneCode(String typeZoneCode) {
        this.typeZoneCode = typeZoneCode;
    }
    public BigDecimal getCapaciteM3() {
        return capaciteM3;
    }
    public void setCapaciteM3(BigDecimal capaciteM3) {
        this.capaciteM3 = capaciteM3;
    }
    public BigDecimal getVolumeOccupeM3() {
        return volumeOccupeM3;
    }
    public void setVolumeOccupeM3(BigDecimal volumeOccupeM3) {
        this.volumeOccupeM3 = volumeOccupeM3;
    }
    public BigDecimal getVolumeLibreM3() {
        return volumeLibreM3;
    }
    public void setVolumeLibreM3(BigDecimal volumeLibreM3) {
        this.volumeLibreM3 = volumeLibreM3;
    }
    public BigDecimal getTauxOccupationPct() {
        return tauxOccupationPct;
    }
    public void setTauxOccupationPct(BigDecimal tauxOccupationPct) {
        this.tauxOccupationPct = tauxOccupationPct;
    }
    public String getStatutCouleur() {
        return statutCouleur;
    }
    public void setStatutCouleur(String statutCouleur) {
        this.statutCouleur = statutCouleur;
    }
}