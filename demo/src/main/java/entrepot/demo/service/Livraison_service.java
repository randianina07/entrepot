package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Livraison;
import entrepot.demo.repository.Livraison_repository;

@Service
public class Livraison_service {
    private final Livraison_repository livraison_repository;

    public Livraison_service(Livraison_repository livraison_repository){
        this.livraison_repository = livraison_repository;
    }

    public List<Livraison> findallLivraisons() {
        return livraison_repository.findAll();
    }

    public Livraison findById(Long id){
        return livraison_repository.findById(id)
            .orElseThrow(()-> new RuntimeException("La livraison n'existe pas"));
    }
}