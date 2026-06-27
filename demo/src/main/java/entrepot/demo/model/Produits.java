package entrepot.demo.model;

import jakarta.persistence.Table;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Table(name = "produits")
public class Produits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String code;
    String nom;
    String description;
    double volume_unitaire_m3;
    double poids_unitaire_kg;

    @ManyToOne
    @JoinColumn(name = "type_produit_id", referencedColumnName = "id")
    Type_produit type_produit;
    
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
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getVolume_unitaire_m3() {
        return volume_unitaire_m3;
    }
    public void setVolume_unitaire_m3(double volume_unitaire_m3) {
        this.volume_unitaire_m3 = volume_unitaire_m3;
    }
    public double getPoids_unitaire_kg() {
        return poids_unitaire_kg;
    }
    public void setPoids_unitaire_kg(double poids_unitaire_kg) {
        this.poids_unitaire_kg = poids_unitaire_kg;
    }
    public Type_produit getType_produit() {
        return type_produit;
    }
    public void setType_produit(Type_produit type_produit) {
        this.type_produit = type_produit;
    }

}