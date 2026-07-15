package com.entrepot.gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.TypeContrat;

public interface TypeContratRepository extends JpaRepository<TypeContrat, Long> {

    Optional<TypeContrat> findByCode(String code);

}
