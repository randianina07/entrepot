package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payement")
public class Payement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    Utilisateur client;

    double prix_payer;
    
    @ManyToOne
    @JoinColumn(name = "id_mode_de_payement" , referencedColumnName = "id")
    Mode_payement mode_de_payement;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public double getPrix_payer() {
        return prix_payer;
    }

    public void setPrix_payer(double prix_payer) {
        this.prix_payer = prix_payer;
    }

    public Mode_payement getMode_de_payement() {
        return mode_de_payement;
    }

    public void setMode_de_payement(Mode_payement mode_de_payement) {
        this.mode_de_payement = mode_de_payement;
    }

}
