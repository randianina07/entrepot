package com.entrepot.gestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "type_zone")
public class Type_zone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "type_produit_id", referencedColumnName = "id")
    private Type_produit type_produit;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
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
    public Type_produit getType_produit() {
        return type_produit;
    }
    public void setType_produit(Type_produit type_produit) {
        this.type_produit = type_produit;
    }


}
