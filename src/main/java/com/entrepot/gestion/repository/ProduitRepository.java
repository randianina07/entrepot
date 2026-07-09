package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
}
