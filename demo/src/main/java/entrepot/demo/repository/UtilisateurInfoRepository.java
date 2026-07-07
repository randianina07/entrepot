package entrepot.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entrepot.demo.model.UtilisateurInfo;

public interface UtilisateurInfoRepository extends JpaRepository<UtilisateurInfo, Long> {

}
