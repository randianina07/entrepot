package com.entrepot.gestion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.DemandeRenouvellement;
import com.entrepot.gestion.repository.DemandeRenouvellementRepository;

@Service
public class DemandeRenouvellementService {

    private final DemandeRenouvellementRepository demandeRenouvellementRepository;

    public DemandeRenouvellementService(DemandeRenouvellementRepository demandeRenouvellementRepository) {
        this.demandeRenouvellementRepository = demandeRenouvellementRepository;
    }

    public List<DemandeRenouvellement> findAll() {
        return demandeRenouvellementRepository.findAll();
    }

    public Optional<DemandeRenouvellement> findById(Long id) {
        return demandeRenouvellementRepository.findById(id);
    }

    public List<DemandeRenouvellement> findByContrat(Contrat contrat) {
        return demandeRenouvellementRepository.findByContrat(contrat);
    }

    public DemandeRenouvellement save(DemandeRenouvellement demandeRenouvellement) {
        return demandeRenouvellementRepository.save(demandeRenouvellement);
    }

    public void deleteById(Long id) {
        demandeRenouvellementRepository.deleteById(id);
    }
}
