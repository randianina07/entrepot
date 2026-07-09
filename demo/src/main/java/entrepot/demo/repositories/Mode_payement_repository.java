package entrepot.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Mode_payement;

public interface Mode_payement_repository extends JpaRepository<Mode_payement,Long>{
    
}
