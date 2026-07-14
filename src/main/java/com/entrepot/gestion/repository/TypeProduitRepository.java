package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.TypeProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeProduitRepository extends JpaRepository<TypeProduit, Long> {
}
