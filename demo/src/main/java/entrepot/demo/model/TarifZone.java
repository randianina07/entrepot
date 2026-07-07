package entrepot.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "tarifs_zone")
public class TarifZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NOTE : type_zone_id est NOT NULL dans la table, mais la table
    // "types_zone" n'a pas ete fournie dans le schema que tu m'as donne
    // (seule la contrainte FK vers types_zone(id) apparait). Je laisse donc
    // l'id brut en attendant que tu me donnes le CREATE TABLE types_zone.
    @Column(name = "type_zone_id", nullable = false)
    private Long typeZoneId;

    @ManyToOne
    @JoinColumn(name = "unite_duree_id", nullable = false)
    private UniteDuree uniteDuree;

    @Column(name = "prix_m3", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixM3;

    @Column(name = "date_debut_validite", nullable = false)
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;

    public TarifZone() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeZoneId() {
        return typeZoneId;
    }

    public void setTypeZoneId(Long typeZoneId) {
        this.typeZoneId = typeZoneId;
    }

    public UniteDuree getUniteDuree() {
        return uniteDuree;
    }

    public void setUniteDuree(UniteDuree uniteDuree) {
        this.uniteDuree = uniteDuree;
    }

    public BigDecimal getPrixM3() {
        return prixM3;
    }

    public void setPrixM3(BigDecimal prixM3) {
        this.prixM3 = prixM3;
    }

    public LocalDate getDateDebutValidite() {
        return dateDebutValidite;
    }

    public void setDateDebutValidite(LocalDate dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }

    public LocalDate getDateFinValidite() {
        return dateFinValidite;
    }

    public void setDateFinValidite(LocalDate dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }
}
