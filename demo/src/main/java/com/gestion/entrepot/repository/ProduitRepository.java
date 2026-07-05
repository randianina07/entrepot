package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
}
