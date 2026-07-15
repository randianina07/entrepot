package com.entrepot.gestion.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "factures")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(name = "volume_espace_m3", nullable = false, precision = 10, scale = 3)
    private BigDecimal volumeEspaceM3;

    @Column(name = "prix_facture", nullable = false, precision = 14, scale = 2)
    private BigDecimal prixFacture;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    public Facture() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public BigDecimal getVolumeEspaceM3() {
        return volumeEspaceM3;
    }

    public void setVolumeEspaceM3(BigDecimal volumeEspaceM3) {
        this.volumeEspaceM3 = volumeEspaceM3;
    }

    public BigDecimal getPrixFacture() {
        return prixFacture;
    }

    public void setPrixFacture(BigDecimal prixFacture) {
        this.prixFacture = prixFacture;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }
}
