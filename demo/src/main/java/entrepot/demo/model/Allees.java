package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "allees")
public class Allees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String libelle;
}   
