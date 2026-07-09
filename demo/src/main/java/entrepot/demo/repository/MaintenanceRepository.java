package entrepot.demo.repository;

import entrepot.demo.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
	
}
