package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.UniteDuree;

public interface UniteDureeRepository extends JpaRepository<UniteDuree, Long> {

    Optional<UniteDuree> findByCode(String code);

}
