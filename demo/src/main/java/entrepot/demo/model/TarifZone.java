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

    @ManyToOne
    @JoinColumn(name = "type_zone_id", nullable = false)
    private TypeZone typeZone;

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

    public TypeZone getTypeZone() {
        return typeZone;
    }

    public void setTypeZone(TypeZone typeZone) {
        this.typeZone = typeZone;
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
