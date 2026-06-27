package entrepot.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import entrepot.demo.model.Stocks_emplacement;

public interface Stocks_emplacement_repository extends JpaRepository<Stocks_emplacement, Long> {
    
    @Query(value = "SELECT * FROM stocks_emplacement WHERE zone_id = ?1", nativeQuery = true)
    List<Stocks_emplacement> findByZoneId(Long id);

}
