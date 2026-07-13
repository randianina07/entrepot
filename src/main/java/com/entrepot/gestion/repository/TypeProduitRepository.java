package com.entrepot.gestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entrepot.gestion.model.TypeProduit;

@Repository
public interface TypeProduitRepository extends JpaRepository<TypeProduit, Long> {

}