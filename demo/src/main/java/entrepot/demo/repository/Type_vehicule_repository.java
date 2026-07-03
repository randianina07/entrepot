package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Type_vehicule;

public interface Type_vehicule_repository extends JpaRepository <Type_vehicule , Long> {
}
