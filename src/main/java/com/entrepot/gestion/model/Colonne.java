package com.entrepot.gestion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "colonne")
public class Colonne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String libelle;
}
