package com.entrepot.gestion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.TarifZone;
import com.entrepot.gestion.repository.TarifZoneRepository;

@Service
public class TarifZoneService {

    private final TarifZoneRepository tarifZoneRepository;

    public TarifZoneService(TarifZoneRepository tarifZoneRepository) {
        this.tarifZoneRepository = tarifZoneRepository;
    }

    public List<TarifZone> findAll() {
        return tarifZoneRepository.findAll();
    }

    public Optional<TarifZone> findById(Long id) {
        return tarifZoneRepository.findById(id);
    }

    public TarifZone save(TarifZone tarifZone) {
        return tarifZoneRepository.save(tarifZone);
    }

    public void deleteById(Long id) {
        tarifZoneRepository.deleteById(id);
    }
}
