package entrepot.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    private boolean actif = true;

    public Chauffeur() {
    }


    public Chauffeur(Long id, String nom, String prenom, String telephone, String numeroPermis, LocalDate dateExpirationPermis, boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.numeroPermis = numeroPermis;
        this.dateExpirationPermis = dateExpirationPermis;
        this.actif = actif;
    }

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

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "Chauffeur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", numeroPermis='" + numeroPermis + '\'' +
                ", dateExpirationPermis=" + dateExpirationPermis +
                ", actif=" + actif +
                '}';
    }
}