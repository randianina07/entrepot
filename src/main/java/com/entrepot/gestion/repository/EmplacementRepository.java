package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
}
