package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.StatutDemandeStockage;

public interface StatutDemandeStockageRepository extends JpaRepository<StatutDemandeStockage, Long> {

    Optional<StatutDemandeStockage> findByCode(String code);

}
