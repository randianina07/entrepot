package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.TypeContrat;

public interface TypeContratRepository extends JpaRepository<TypeContrat, Long> {

    Optional<TypeContrat> findByCode(String code);

}