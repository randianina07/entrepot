package entrepot.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "demandes_stockage")
public class DemandeStockage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // NOTE : type_zone_id est NOT NULL dans la table, mais la table
    // "types_zone" n'a pas ete fournie dans le schema que tu m'as donne
    // (seule la contrainte FK vers types_zone(id) apparait). Je laisse donc
    // l'id brut en attendant que tu me donnes le CREATE TABLE types_zone.
    @Column(name = "type_zone_id", nullable = false)
    private Long typeZoneId;

    @ManyToOne
    @JoinColumn(name = "type_contrat_id", nullable = false)
    private TypeContrat typeContrat;

    @Column(name = "volume_espace_m3", nullable = false, precision = 10, scale = 3)
    private BigDecimal volumeEspaceM3;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    public DemandeStockage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Long getTypeZoneId() {
        return typeZoneId;
    }

    public void setTypeZoneId(Long typeZoneId) {
        this.typeZoneId = typeZoneId;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public BigDecimal getVolumeEspaceM3() {
        return volumeEspaceM3;
    }

    public void setVolumeEspaceM3(BigDecimal volumeEspaceM3) {
        this.volumeEspaceM3 = volumeEspaceM3;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
