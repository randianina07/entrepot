package com.entrepot.gestion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entrepot.gestion.model.Livraison;


public interface Livraison_repository extends JpaRepository<Livraison, Long> {
	List<Livraison> findByMissionId(Long missionId);
	List<Livraison> findByMissionVehiculeId(Long vehiculeId);
	List<Livraison> findByMissionIsNullAndDateLivraisonIsNull();
	List<Livraison> findByMissionStatutMissionCodeAndDateLivraisonIsNull(String code);
}


