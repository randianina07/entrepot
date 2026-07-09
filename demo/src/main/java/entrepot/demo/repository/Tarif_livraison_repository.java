package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.Tarif_livraison;

public interface Tarif_livraison_repository extends JpaRepository <Tarif_livraison , Long> {
}
