package entrepot.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entrepot.demo.model.Type_zone;
import entrepot.demo.repository.Type_zone_repository;

@Service
public class Type_zone_service {
    
    @Autowired
    Type_zone_repository type_zone_repository;

    public List<Type_zone> getAllTypeZones() {
        return type_zone_repository.findAll();
        
    }

}
