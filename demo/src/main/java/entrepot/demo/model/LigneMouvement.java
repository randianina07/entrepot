package entrepot.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "lignes_mouvement")
public class LigneMouvement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mouvement_id", nullable = false)
    private Mouvement mouvement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_source_id")
    private Emplacement emplacementSource;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emplacement_dest_id")
    private Emplacement emplacementDest;
    
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantite;
    
    public LigneMouvement() {
    }
    
    public LigneMouvement(Long id, Mouvement mouvement, Produit produit, Emplacement emplacementSource, Emplacement emplacementDest, BigDecimal quantite) {
        this.id = id;
        this.mouvement = mouvement;
        this.produit = produit;
        this.emplacementSource = emplacementSource;
        this.emplacementDest = emplacementDest;
        this.quantite = quantite;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Mouvement getMouvement() {
        return mouvement;
    }
    
    public void setMouvement(Mouvement mouvement) {
        this.mouvement = mouvement;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public Emplacement getEmplacementSource() {
        return emplacementSource;
    }
    
    public void setEmplacementSource(Emplacement emplacementSource) {
        this.emplacementSource = emplacementSource;
    }
    
    public Emplacement getEmplacementDest() {
        return emplacementDest;
    }
    
    public void setEmplacementDest(Emplacement emplacementDest) {
        this.emplacementDest = emplacementDest;
    }
    
    public BigDecimal getQuantite() {
        return quantite;
    }
    
    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LigneMouvement that = (LigneMouvement) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "LigneMouvement{" +
                "id=" + id +
                ", quantite=" + quantite +
                '}';
    }
}
