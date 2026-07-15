package com.entrepot.gestion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.UniteDuree;

public interface UniteDureeRepository extends JpaRepository<UniteDuree, Long> {

    Optional<UniteDuree> findByCode(String code);

}
