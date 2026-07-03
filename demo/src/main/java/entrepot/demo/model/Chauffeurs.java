package entrepot.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "chauffeurs")
public class Chauffeurs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String nom;
    String prenom;
    String telephone;
    @Column(name = "numero_permis")
    String numero_permis;
    @Column(name = "date_expiration_permis")
    LocalDateTime date_epiration_permis;
    boolean actif ;
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
    public String getNumero_permis() {
        return numero_permis;
    }
    public void setNumero_permis(String numero_permis) {
        this.numero_permis = numero_permis;
    }
    public LocalDateTime getDate_epiration_permis() {
        return date_epiration_permis;
    }
    public void setDate_epiration_permis(LocalDateTime date_epiration_permis) {
        this.date_epiration_permis = date_epiration_permis;
    }
    public boolean isActif() {
        return actif;
    }
    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
