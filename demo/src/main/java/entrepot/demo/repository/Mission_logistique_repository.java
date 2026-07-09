package entrepot.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Mission_logistique;

public interface Mission_logistique_repository extends JpaRepository <Mission_logistique , Long>{
    List<Mission_logistique> findByStatutMissionIdIn(List<Integer> ids);
    Optional<Mission_logistique> findByReferenceMission(String referenceMission);
}
