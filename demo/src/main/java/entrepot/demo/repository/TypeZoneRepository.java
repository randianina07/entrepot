package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entrepot.demo.model.TypeZone;

@Repository
public interface TypeZoneRepository extends JpaRepository<TypeZone, Long> {

}