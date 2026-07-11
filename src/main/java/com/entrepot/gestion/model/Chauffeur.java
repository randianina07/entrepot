package com.entrepot.gestion.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "chauffeurs")
public class Chauffeur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    private String prenom;
    
    private String telephone;
    
    @Column(name = "numero_permis", nullable = false, unique = true)
    private String numeroPermis;
    
    @Column(name = "date_expiration_permis")
    private LocalDate dateExpirationPermis;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    // Constructors
    public Chauffeur() {}
    
    public Chauffeur(String nom, String prenom, String telephone, String numeroPermis, 
                     LocalDate dateExpirationPermis, Boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.numeroPermis = numeroPermis;
        this.dateExpirationPermis = dateExpirationPermis;
        this.actif = actif;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getNumeroPermis() {
        return numeroPermis;
    }
    
    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }
    
    public LocalDate getDateExpirationPermis() {
        return dateExpirationPermis;
    }
    
    public void setDateExpirationPermis(LocalDate dateExpirationPermis) {
        this.dateExpirationPermis = dateExpirationPermis;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    // Helper method for full name
    public String getFullName() {
        if (prenom != null && !prenom.isEmpty()) {
            return nom + " " + prenom;
        }
        return nom;
    }
    
    // Check if permit is expired
    public boolean isPermitExpired() {
        return dateExpirationPermis != null && LocalDate.now().isAfter(dateExpirationPermis);
    }
    
    // Check if permit expires soon (within 30 days)
    public boolean isPermitExpiringSoon() {
        return dateExpirationPermis != null && 
               LocalDate.now().plusDays(30).isAfter(dateExpirationPermis) && 
               !isPermitExpired();
    }
}
