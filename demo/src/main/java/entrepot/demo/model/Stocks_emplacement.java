package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "stocks_emplacement")
public class Stocks_emplacement {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    double quantite;

    @ManyToOne
    @JoinColumn(name = "emplacement_id", referencedColumnName = "id")
    Emplacement emplacement;
    
    @ManyToOne
    @JoinColumn(name = "produit_id", referencedColumnName = "id")
    Produits produit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public Emplacement getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(Emplacement emplacement) {
        this.emplacement = emplacement;
    }

    public Produits getProduit() {
        return produit;
    }

    public void setProduit(Produits produit) {
        this.produit = produit;
    }
    
}
