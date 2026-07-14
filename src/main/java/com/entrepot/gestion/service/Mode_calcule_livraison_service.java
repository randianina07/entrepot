package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.ModeCalculeLivraison;
import entrepot.demo.repository.Mode_calcule_livraison_repository;

@Service
public class Mode_calcule_livraison_service {
    private final Mode_calcule_livraison_repository mode_calcule_livraison_repository;

    public Mode_calcule_livraison_service(Mode_calcule_livraison_repository mode_calcule_livraison_repository){
        this.mode_calcule_livraison_repository = mode_calcule_livraison_repository;
    }

    public List<ModeCalculeLivraison> modeCalculfindAll(){
        return mode_calcule_livraison_repository.findAll();
    }
}
