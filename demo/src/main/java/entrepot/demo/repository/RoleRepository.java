package entrepot.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCode(String code);

}
