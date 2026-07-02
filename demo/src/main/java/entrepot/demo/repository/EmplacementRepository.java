package entrepot.demo.repository;

import entrepot.demo.model.Emplacement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
}
