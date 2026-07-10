package com.entrepot.gestion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "statuts_vehicule")
public class StatutVehicule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String libelle;
    
    // Constructors
    public StatutVehicule() {}
    
    public StatutVehicule(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }
    
    // Getters and Setters
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
}
