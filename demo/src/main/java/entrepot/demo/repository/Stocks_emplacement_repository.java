package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Stocks_emplacement;

public interface Stocks_emplacement_repository extends JpaRepository<Stocks_emplacement, Long> {
    
}
