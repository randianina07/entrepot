package com.entrepot.gestion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.AbonnementStockage;
import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.repository.AbonnementStockageRepository;

@Service
public class AbonnementStockageService {

    private final AbonnementStockageRepository abonnementStockageRepository;

    public AbonnementStockageService(AbonnementStockageRepository abonnementStockageRepository) {
        this.abonnementStockageRepository = abonnementStockageRepository;
    }

    public List<AbonnementStockage> findAll() {
        return abonnementStockageRepository.findAll();
    }

    public Optional<AbonnementStockage> findById(Long id) {
        return abonnementStockageRepository.findById(id);
    }

    public Optional<AbonnementStockage> findByContrat(Contrat contrat) {
        return abonnementStockageRepository.findByContrat(contrat);
    }

    public Optional<AbonnementStockage> findByUtilisateur(Utilisateur utilisateur) {
        return abonnementStockageRepository.findByUtilisateur(utilisateur);
    }

    public AbonnementStockage save(AbonnementStockage abonnementStockage) {
        return abonnementStockageRepository.save(abonnementStockage);
    }

    public void deleteById(Long id) {
        abonnementStockageRepository.deleteById(id);
    }
}