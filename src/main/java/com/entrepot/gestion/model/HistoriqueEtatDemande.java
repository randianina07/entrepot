package com.entrepot.gestion.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "historique_etat_demande")
public class HistoriqueEtatDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "demande_stockage_id", nullable = false)
    private DemandeStockage demandeStockage;

    @ManyToOne
    @JoinColumn(name = "statut_id", nullable = false)
    private StatutDemandeStockage statut;

    @Column(name = "date_statut", nullable = false)
    private LocalDateTime dateStatut;

    public HistoriqueEtatDemande() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DemandeStockage getDemandeStockage() {
        return demandeStockage;
    }

    public void setDemandeStockage(DemandeStockage demandeStockage) {
        this.demandeStockage = demandeStockage;
    }

    public StatutDemandeStockage getStatut() {
        return statut;
    }

    public void setStatut(StatutDemandeStockage statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateStatut() {
        return dateStatut;
    }

    public void setDateStatut(LocalDateTime dateStatut) {
        this.dateStatut = dateStatut;
    }
}
