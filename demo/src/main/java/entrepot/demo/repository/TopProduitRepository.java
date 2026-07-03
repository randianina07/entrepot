package entrepot.demo.repository;

import entrepot.demo.model.TopProduit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TopProduitRepository extends JpaRepository<TopProduit, Long> {
    
    Optional<TopProduit> findByDateSnapshotAndProduitId(LocalDate dateSnapshot, Long produitId);
}
