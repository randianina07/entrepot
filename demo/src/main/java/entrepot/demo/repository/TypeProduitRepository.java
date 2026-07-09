package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entrepot.demo.model.TypeProduit;

@Repository
public interface TypeProduitRepository extends JpaRepository<TypeProduit, Long> {

}