package entrepot.demo.repository;

import entrepot.demo.entity.TypeMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeMaintenanceRepository extends JpaRepository<TypeMaintenance, Long> {
}