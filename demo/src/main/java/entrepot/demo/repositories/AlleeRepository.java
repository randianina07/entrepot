package entrepot.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import entrepot.demo.model.Allees;

@Repository
public interface AlleeRepository extends JpaRepository<Allees, Long> {
    
}
