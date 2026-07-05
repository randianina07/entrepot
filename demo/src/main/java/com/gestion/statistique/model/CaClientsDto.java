package com.gestion.statistique.model;

import java.math.BigDecimal;

public class CaClientsDto {
    private Long clientId;
    private String nomClient;
    private String prenom;
    private String email;
    private String secteur;
    private Long nbContrats;
    private Long nbLivraisons;
    private BigDecimal caStockage;
    private BigDecimal caLivraison;
    private BigDecimal caTotal;
    private Long rangCa;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public Long getNbContrats() {
        return nbContrats;
    }

    public void setNbContrats(Long nbContrats) {
        this.nbContrats = nbContrats;
    }

    public Long getNbLivraisons() {
        return nbLivraisons;
    }

    public void setNbLivraisons(Long nbLivraisons) {
        this.nbLivraisons = nbLivraisons;
    }

    public BigDecimal getCaStockage() {
        return caStockage;
    }

    public void setCaStockage(BigDecimal caStockage) {
        this.caStockage = caStockage;
    }

    public BigDecimal getCaLivraison() {
        return caLivraison;
    }

    public void setCaLivraison(BigDecimal caLivraison) {
        this.caLivraison = caLivraison;
    }

    public BigDecimal getCaTotal() {
        return caTotal;
    }

    public void setCaTotal(BigDecimal caTotal) {
        this.caTotal = caTotal;
    }

    public Long getRangCa() {
        return rangCa;
    }

    public void setRangCa(Long rangCa) {
        this.rangCa = rangCa;
    }
}
