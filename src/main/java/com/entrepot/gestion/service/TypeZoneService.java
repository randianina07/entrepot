package com.entrepot.gestion.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.TypeZone;
import com.entrepot.gestion.repository.TypeZoneRepository;

@Service
public class TypeZoneService {

    private final TypeZoneRepository typeZoneRepository;

    public TypeZoneService(TypeZoneRepository typeZoneRepository) {
        this.typeZoneRepository = typeZoneRepository;
    }

    public List<TypeZone> getAll() {
        return typeZoneRepository.findAll();
    }
}
