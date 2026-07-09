package entrepot.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import entrepot.demo.model.Etage;

public interface EtageRepository extends JpaRepository<Etage, Long> {
    
}
