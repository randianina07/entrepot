package entrepot.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "colonne")
public class Colonne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String libelle;
}
