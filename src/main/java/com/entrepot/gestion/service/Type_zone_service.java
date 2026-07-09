package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Type_zone;
import com.entrepot.gestion.repository.Type_zone_repository;

@Service
public class Type_zone_service {
    
    @Autowired
    Type_zone_repository type_zone_repository;

    public List<Type_zone> getAllTypeZones() {
        return type_zone_repository.findAll();
        
    }

}
