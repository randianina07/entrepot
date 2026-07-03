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

    @Column(name = "date_renouvellement")
    private LocalDate dateRenouvellement;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    public RenouvellementContrat() {
    }

    public Long getId() {
        return id;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public LocalDate getDateRenouvellement() {
        return dateRenouvellement;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public void setDateRenouvellement(LocalDate dateRenouvellement) {
        this.dateRenouvellement = dateRenouvellement;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}