package entrepot.demo.model;

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

    // NOTE : demande_renouvellement_id est NOT NULL dans le nouveau schema,
    // mais tu ne m'as pas fourni d'entite DemandeRenouvellement : je laisse
    // donc l'id brut en attendant que tu crees cette entite.
    @Column(name = "demande_renouvellement_id", nullable = false)
    private Long demandeRenouvellementId;

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

    public Long getDemandeRenouvellementId() {
        return demandeRenouvellementId;
    }

    public void setDemandeRenouvellementId(Long demandeRenouvellementId) {
        this.demandeRenouvellementId = demandeRenouvellementId;
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
