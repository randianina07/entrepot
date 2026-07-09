<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/UtilisateurRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.Utilisateur;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Utilisateur;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/UtilisateurRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
}
