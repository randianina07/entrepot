package entrepot.demo.model;

// import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.Setter;

@Entity
@Table(name = "tarifs_livraison")
// @Getter
// @Setter
public class Tarif_livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "zone_livraison_id")
    ZoneLivraison zoneLivraison;

    @ManyToOne
    @JoinColumn(name = "mode_calcul_id")
    ModeCalculeLivraison modeCalculeLivraison;

    Double prixBase;
    Double prixParKg;
    Double prixParm;
    LocalDateTime debutValidite;
    LocalDateTime finValidite;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public ZoneLivraison getZoneLivraison() {
        return zoneLivraison;
    }
    public void setZoneLivraison(ZoneLivraison zoneLivraison) {
        this.zoneLivraison = zoneLivraison;
    }
    public ModeCalculeLivraison getModeCalculeLivraison() {
        return modeCalculeLivraison;
    }
    public void setModeCalculeLivraison(ModeCalculeLivraison modeCalculeLivraison) {
        this.modeCalculeLivraison = modeCalculeLivraison;
    }
    public Double getPrixBase() {
        return prixBase;
    }
    public void setPrixBase(Double prixBase) {
        this.prixBase = prixBase;
    }
    public Double getPrixParKg() {
        return prixParKg;
    }
    public void setPrixParKg(Double prixParKg) {
        this.prixParKg = prixParKg;
    }
    public Double getPrixParm() {
        return prixParm;
    }
    public void setPrixParm(Double prixParm) {
        this.prixParm = prixParm;
    }
    public LocalDateTime getDebutValidite() {
        return debutValidite;
    }
    public void setDebutValidite(LocalDateTime debutValidite) {
        this.debutValidite = debutValidite;
    }
    public LocalDateTime getFinValidite() {
        return finValidite;
    }
    public void setFinValidite(LocalDateTime finValidite) {
        this.finValidite = finValidite;
    }
}
