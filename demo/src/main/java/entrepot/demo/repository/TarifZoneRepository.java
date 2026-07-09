package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.TarifZone;

public interface TarifZoneRepository extends JpaRepository<TarifZone, Long> {

}
