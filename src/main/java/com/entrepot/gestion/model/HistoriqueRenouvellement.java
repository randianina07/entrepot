package com.entrepot.gestion.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "historique_renouvellement")
public class HistoriqueRenouvellement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "demande_renouvellement_id", nullable = false)
    private DemandeRenouvellement demandeRenouvellement;

    @ManyToOne
    @JoinColumn(name = "statut_renouvellement_id", nullable = false)
    private StatutRenouvellement statutRenouvellement;

    @Column(name = "date_statut", nullable = false)
    private LocalDateTime dateStatut;

    public HistoriqueRenouvellement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DemandeRenouvellement getDemandeRenouvellement() {
        return demandeRenouvellement;
    }

    public void setDemandeRenouvellement(DemandeRenouvellement demandeRenouvellement) {
        this.demandeRenouvellement = demandeRenouvellement;
    }

    public StatutRenouvellement getStatutRenouvellement() {
        return statutRenouvellement;
    }

    public void setStatutRenouvellement(StatutRenouvellement statutRenouvellement) {
        this.statutRenouvellement = statutRenouvellement;
    }

    public LocalDateTime getDateStatut() {
        return dateStatut;
    }

    public void setDateStatut(LocalDateTime dateStatut) {
        this.dateStatut = dateStatut;
    }
}
