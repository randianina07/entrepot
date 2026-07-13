package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Zone;
import com.entrepot.gestion.repository.Zone_repository;

@Service
public class Zone_service {
    
    @Autowired
    Zone_repository zone_repository;

    public List<Zone> getZonesByTypeZoneId(long id) {

        return zone_repository.findByTypeZoneId(id);
    
    }

}
