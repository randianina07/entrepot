package entrepot.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "types_zone")
public class TypeZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "type_produit_id")
    private TypeProduit typeProduit;

    @Column(name = "controle_temperature", nullable = false)
    private boolean controleTemperature;

    @Column(name = "acces_restreint", nullable = false)
    private boolean accesRestreint;

    @Column(name = "journalisation_acces", nullable = false)
    private boolean journalisationAcces;

    @Column(name = "charge_lourde_possible", nullable = false)
    private boolean chargeLourdePossible;


    public TypeZone() {
    }


    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public TypeProduit getTypeProduit() {
        return typeProduit;
    }

    public boolean isControleTemperature() {
        return controleTemperature;
    }

    public boolean isAccesRestreint() {
        return accesRestreint;
    }

    public boolean isJournalisationAcces() {
        return journalisationAcces;
    }

    public boolean isChargeLourdePossible() {
        return chargeLourdePossible;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }

    public void setControleTemperature(boolean controleTemperature) {
        this.controleTemperature = controleTemperature;
    }

    public void setAccesRestreint(boolean accesRestreint) {
        this.accesRestreint = accesRestreint;
    }

    public void setJournalisationAcces(boolean journalisationAcces) {
        this.journalisationAcces = journalisationAcces;
    }

    public void setChargeLourdePossible(boolean chargeLourdePossible) {
        this.chargeLourdePossible = chargeLourdePossible;
    }
}