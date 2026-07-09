<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/repository/ProduitRepository.java
package com.gestion.entrepot.repository;

import com.gestion.entrepot.model.Produit;
========
package com.entrepot.gestion.repository;

import com.entrepot.gestion.model.Produit;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/repository/ProduitRepository.java
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
}
