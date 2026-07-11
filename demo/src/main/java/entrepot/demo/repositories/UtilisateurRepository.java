package entrepot.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import entrepot.demo.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {


    @Query(value = "SELECT * FROM utilisateurs WHERE role_id = 5", nativeQuery = true)
    public List<Utilisateur> findAllById_role();
    
} 
