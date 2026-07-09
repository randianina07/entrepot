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

}
