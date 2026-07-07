package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.RenouvellementContrat;
import entrepot.demo.repository.RenouvellementContratRepository;

@Service
public class RenouvellementContratService {

    private final RenouvellementContratRepository renouvellementContratRepository;

    public RenouvellementContratService(RenouvellementContratRepository renouvellementContratRepository) {
        this.renouvellementContratRepository = renouvellementContratRepository;
    }

    public List<RenouvellementContrat> findAll() {
        return renouvellementContratRepository.findAll();
    }

    public Optional<RenouvellementContrat> findById(Long id) {
        return renouvellementContratRepository.findById(id);
    }

    public List<RenouvellementContrat> findByContrat(Contrat contrat) {
        return renouvellementContratRepository.findByContrat(contrat);
    }

    public RenouvellementContrat save(RenouvellementContrat renouvellement) {
        return renouvellementContratRepository.save(renouvellement);
    }

    public void deleteById(Long id) {
        renouvellementContratRepository.deleteById(id);
    }
}
