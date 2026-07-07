package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.StatutRenouvellement;
import entrepot.demo.repository.StatutRenouvellementRepository;

@Service
public class StatutRenouvellementService {

    private final StatutRenouvellementRepository statutRenouvellementRepository;

    public StatutRenouvellementService(StatutRenouvellementRepository statutRenouvellementRepository) {
        this.statutRenouvellementRepository = statutRenouvellementRepository;
    }

    public List<StatutRenouvellement> findAll() {
        return statutRenouvellementRepository.findAll();
    }

    public Optional<StatutRenouvellement> findById(Long id) {
        return statutRenouvellementRepository.findById(id);
    }

    public Optional<StatutRenouvellement> findByCode(String code) {
        return statutRenouvellementRepository.findByCode(code);
    }

    public StatutRenouvellement save(StatutRenouvellement statutRenouvellement) {
        return statutRenouvellementRepository.save(statutRenouvellement);
    }

    public void deleteById(Long id) {
        statutRenouvellementRepository.deleteById(id);
    }
}
