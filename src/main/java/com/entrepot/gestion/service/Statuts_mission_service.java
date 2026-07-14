
package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.StatutMission;
import com.entrepot.gestion.repository.StatutMissionRepository;



@Service
public class Statuts_mission_service {
    private final StatutMissionRepository statuts_mission_repository ;

    public Statuts_mission_service(StatutMissionRepository statuts_mission_repository){
        this.statuts_mission_repository = statuts_mission_repository;
    }

    public List<StatutMission> listStatuts_missions(){
        return statuts_mission_repository.findAll();
    }
}