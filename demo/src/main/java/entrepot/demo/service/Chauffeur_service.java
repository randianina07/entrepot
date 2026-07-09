package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import entrepot.demo.repository.Chauffeur_repository;
import entrepot.demo.model.Chauffeurs;
@Service
public class Chauffeur_service {
    Chauffeur_repository chauffeur_repository;

    public Chauffeur_service(Chauffeur_repository chauffeur_repository){
        this.chauffeur_repository = chauffeur_repository;
    }

    public List<Chauffeurs> listeChauffeur(){
        return chauffeur_repository.findAll();
    }

}
