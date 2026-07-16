package com.entrepot.gestion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.UniteDuree;
import com.entrepot.gestion.repository.UniteDureeRepository;

@Service
public class UniteDureeService {

    private final UniteDureeRepository uniteDureeRepository;

    public UniteDureeService(UniteDureeRepository uniteDureeRepository) {
        this.uniteDureeRepository = uniteDureeRepository;
    }

    public List<UniteDuree> findAll() {
        return uniteDureeRepository.findAll();
    }

    public Optional<UniteDuree> findById(Long id) {
        return uniteDureeRepository.findById(id);
    }

    public Optional<UniteDuree> findByCode(String code) {
        return uniteDureeRepository.findByCode(code);
    }

    public UniteDuree save(UniteDuree uniteDuree) {
        return uniteDureeRepository.save(uniteDuree);
    }

    public void deleteById(Long id) {
        uniteDureeRepository.deleteById(id);
    }
}
