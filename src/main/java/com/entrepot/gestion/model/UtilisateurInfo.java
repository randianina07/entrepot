package com.entrepot.gestion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs_info")
public class UtilisateurInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false, unique = true)
    private Utilisateur utilisateur;
    
    @Column(nullable = false)
    private String nom;
    
    private String prenom;
    
    private String numero;
    
    private String adresse;
    
    private String secteur;
    
    public UtilisateurInfo() {}
    
    public UtilisateurInfo(Utilisateur utilisateur, String nom) {
        this.utilisateur = utilisateur;
        this.nom = nom;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getSecteur() { return secteur; }
    public void setSecteur(String secteur) { this.secteur = secteur; }
}
