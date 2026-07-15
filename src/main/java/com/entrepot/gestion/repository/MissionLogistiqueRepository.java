package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.MissionLogistique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionLogistiqueRepository extends JpaRepository<MissionLogistique, Long> {
    List<MissionLogistique> findByStatutMissionCode(String code);
    List<MissionLogistique> findByChauffeurId(Long chauffeurId);
    List<MissionLogistique> findByVehiculeId(Long vehiculeId);
    List<MissionLogistique> findByStatutMissionIdIn(List<Integer> of);
}
