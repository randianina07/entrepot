package com.entrepot.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "contrats")
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "demande_stockage_id", nullable = false)
    private DemandeStockage demandeStockage;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "type_zone_id", nullable = false)
    private TypeZone typeZone;

    @ManyToOne
    @JoinColumn(name = "type_contrat_id", nullable = false)
    private TypeContrat typeContrat;

    @Column(name = "volume_espace_m3", nullable = false, precision = 10, scale = 3)
    private BigDecimal volumeEspaceM3;

    @Column(name = "quantite_emplacement", nullable = false)
    private Integer quantiteEmplacement;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duree_mois")
    private Integer dureeMois;

    public Contrat() {
    }


    public Long getId() {
        return id;
    }

    public DemandeStockage getDemandeStockage() {
        return demandeStockage;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public TypeZone getTypeZone() {
        return typeZone;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public BigDecimal getVolumeEspaceM3() {
        return volumeEspaceM3;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public String getDescription() {
        return description;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setDemandeStockage(DemandeStockage demandeStockage) {
        this.demandeStockage = demandeStockage;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setTypeZone(TypeZone typeZone) {
        this.typeZone = typeZone;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public void setVolumeEspaceM3(BigDecimal volumeEspaceM3) {
        this.volumeEspaceM3 = volumeEspaceM3;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantiteEmplacement() {
        return quantiteEmplacement;
    }

    public void setQuantiteEmplacement(Integer quantiteEmplacement) {
        this.quantiteEmplacement = quantiteEmplacement;
    }

    public Integer getDureeMois() {
        return dureeMois;
    }
    
    public void setDureeMois(Integer dureeMois) {
        this.dureeMois = dureeMois;
    }
}