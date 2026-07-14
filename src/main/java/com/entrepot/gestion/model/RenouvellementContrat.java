package com.entrepot.gestion.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "renouvellements_contrat")
public class RenouvellementContrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @ManyToOne
    @JoinColumn(name = "demande_renouvellement_id", nullable = false)
    private DemandeRenouvellement demandeRenouvellement;

    @Column(name = "date_renouvellement", nullable = false)
    private LocalDate dateRenouvellement;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    public RenouvellementContrat() {
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

    public DemandeRenouvellement getDemandeRenouvellement() {
        return demandeRenouvellement;
    }

    public void setDemandeRenouvellement(DemandeRenouvellement demandeRenouvellement) {
        this.demandeRenouvellement = demandeRenouvellement;
    }

    public LocalDate getDateRenouvellement() {
        return dateRenouvellement;
    }

    public void setDateRenouvellement(LocalDate dateRenouvellement) {
        this.dateRenouvellement = dateRenouvellement;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
