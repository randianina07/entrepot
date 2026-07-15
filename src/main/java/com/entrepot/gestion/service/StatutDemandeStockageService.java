package com.entrepot.gestion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.StatutDemandeStockage;
import com.entrepot.gestion.repository.StatutDemandeStockageRepository;

@Service
public class StatutDemandeStockageService {

    private final StatutDemandeStockageRepository statutDemandeStockageRepository;

    public StatutDemandeStockageService(StatutDemandeStockageRepository statutDemandeStockageRepository) {
        this.statutDemandeStockageRepository = statutDemandeStockageRepository;
    }

    public List<StatutDemandeStockage> findAll() {
        return statutDemandeStockageRepository.findAll();
    }

    public Optional<StatutDemandeStockage> findById(Long id) {
        return statutDemandeStockageRepository.findById(id);
    }

    public Optional<StatutDemandeStockage> findByCode(String code) {
        return statutDemandeStockageRepository.findByCode(code);
    }

    public StatutDemandeStockage save(StatutDemandeStockage statutDemandeStockage) {
        return statutDemandeStockageRepository.save(statutDemandeStockage);
    }

    public void deleteById(Long id) {
        statutDemandeStockageRepository.deleteById(id);
    }
}
