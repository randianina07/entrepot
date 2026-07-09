
package entrepot.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Statuts_mission;
import entrepot.demo.repository.Statuts_mission_repository;

@Service
public class Statuts_mission_service {
    private final Statuts_mission_repository statuts_mission_repository ;

    public Statuts_mission_service(Statuts_mission_repository statuts_mission_repository){
        this.statuts_mission_repository = statuts_mission_repository;
    }

    public List<Statuts_mission> listStatuts_missions(){
        return statuts_mission_repository.findAll();
    }
}