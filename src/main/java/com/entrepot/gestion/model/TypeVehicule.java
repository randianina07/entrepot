package com.entrepot.gestion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "types_vehicule")
public class TypeVehicule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String libelle;
    
    private String description;
    
    // Constructors
    public TypeVehicule() {}
    
    public TypeVehicule(String code, String libelle, String description) {
        this.code = code;
        this.libelle = libelle;
        this.description = description;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
