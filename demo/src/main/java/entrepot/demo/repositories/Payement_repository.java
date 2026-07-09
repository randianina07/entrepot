package entrepot.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Payement;

public interface Payement_repository extends JpaRepository<Payement,Long> {
    
}
