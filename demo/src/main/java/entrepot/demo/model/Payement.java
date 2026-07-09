package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payement")
public class Payement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    long client_id;
    double prix_payer;
    int id_mode_de_payement;

}
