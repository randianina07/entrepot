package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
}
