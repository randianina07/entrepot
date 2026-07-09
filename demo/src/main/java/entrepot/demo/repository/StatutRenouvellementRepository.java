package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.StatutRenouvellement;

public interface StatutRenouvellementRepository extends JpaRepository<StatutRenouvellement, Long> {

    Optional<StatutRenouvellement> findByCode(String code);

}
