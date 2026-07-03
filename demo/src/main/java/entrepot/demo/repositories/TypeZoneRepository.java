package entrepot.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import entrepot.demo.model.TypeZone;

public interface TypeZoneRepository extends JpaRepository<TypeZone, Long> {
    
}
