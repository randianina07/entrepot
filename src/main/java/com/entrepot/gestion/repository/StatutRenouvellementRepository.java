package com.entrepot.gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.StatutRenouvellement;

public interface StatutRenouvellementRepository extends JpaRepository<StatutRenouvellement, Long> {

    Optional<StatutRenouvellement> findByCode(String code);

}
