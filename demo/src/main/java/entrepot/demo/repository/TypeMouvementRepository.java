package entrepot.demo.repository;

import entrepot.demo.model.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeMouvementRepository extends JpaRepository<TypeMouvement, Long> {
}
