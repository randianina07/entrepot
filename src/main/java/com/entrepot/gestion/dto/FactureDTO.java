package com.entrepot.gestion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FactureDTO {

    private Long contratId;
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientAdresse;
    private String clientTelephone;
    private String typeZone;
    private String typeContrat;
    private BigDecimal volumeM3;
    private Integer quantiteEmplacement;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateFacture;
    private int dureeMois;
    private int dureeJours;
    private BigDecimal prixM3Mois;
    private BigDecimal prixM3Jour;
    private BigDecimal totalMois;
    private BigDecimal totalJours;
    private BigDecimal totalGeneral;
    private String dureeAffichage;

    public FactureDTO() {
    }

    public Long getContratId() {
        return contratId;
    }

    public void setContratId(Long contratId) {
        this.contratId = contratId;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getClientPrenom() {
        return clientPrenom;
    }

    public void setClientPrenom(String clientPrenom) {
        this.clientPrenom = clientPrenom;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientAdresse() {
        return clientAdresse;
    }

    public void setClientAdresse(String clientAdresse) {
        this.clientAdresse = clientAdresse;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
    }

    public String getTypeZone() {
        return typeZone;
    }

    public void setTypeZone(String typeZone) {
        this.typeZone = typeZone;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public BigDecimal getVolumeM3() {
        return volumeM3;
    }

    public void setVolumeM3(BigDecimal volumeM3) {
        this.volumeM3 = volumeM3;
    }

    public Integer getQuantiteEmplacement() {
        return quantiteEmplacement;
    }

    public void setQuantiteEmplacement(Integer quantiteEmplacement) {
        this.quantiteEmplacement = quantiteEmplacement;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDate getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }

    public int getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(int dureeMois) {
        this.dureeMois = dureeMois;
    }

    public int getDureeJours() {
        return dureeJours;
    }

    public void setDureeJours(int dureeJours) {
        this.dureeJours = dureeJours;
    }

    public BigDecimal getPrixM3Mois() {
        return prixM3Mois;
    }

    public void setPrixM3Mois(BigDecimal prixM3Mois) {
        this.prixM3Mois = prixM3Mois;
    }

    public BigDecimal getPrixM3Jour() {
        return prixM3Jour;
    }

    public void setPrixM3Jour(BigDecimal prixM3Jour) {
        this.prixM3Jour = prixM3Jour;
    }

    public BigDecimal getTotalMois() {
        return totalMois;
    }

    public void setTotalMois(BigDecimal totalMois) {
        this.totalMois = totalMois;
    }

    public BigDecimal getTotalJours() {
        return totalJours;
    }

    public void setTotalJours(BigDecimal totalJours) {
        this.totalJours = totalJours;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(BigDecimal totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public String getDureeAffichage() {
        return dureeAffichage;
    }

    public void setDureeAffichage(String dureeAffichage) {
        this.dureeAffichage = dureeAffichage;
    }
}
