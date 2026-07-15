package com.entrepot.gestion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "abonnements_stockage")
public class AbonnementStockage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false, unique = true)
    private Contrat contrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_zone_id", nullable = false)
    private TypeZone typeZone;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    public AbonnementStockage() {
    }

    public Long getId() {
        return id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public TypeZone getTypeZone() {
        return typeZone;
    }

    public Integer getDureeMois() {
        return dureeMois;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public void setTypeZone(TypeZone typeZone) {
        this.typeZone = typeZone;
    }

    public void setDureeMois(Integer dureeMois) {
        this.dureeMois = dureeMois;
    }
}