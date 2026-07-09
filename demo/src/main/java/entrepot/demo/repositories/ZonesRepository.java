package entrepot.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import entrepot.demo.model.Zones;

public interface ZonesRepository extends JpaRepository<Zones, Long> {
    
}
