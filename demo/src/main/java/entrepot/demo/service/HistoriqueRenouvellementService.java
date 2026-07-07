package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.DemandeRenouvellement;
import entrepot.demo.model.HistoriqueRenouvellement;
import entrepot.demo.repository.HistoriqueRenouvellementRepository;

@Service
public class HistoriqueRenouvellementService {

    private final HistoriqueRenouvellementRepository historiqueRenouvellementRepository;

    public HistoriqueRenouvellementService(HistoriqueRenouvellementRepository historiqueRenouvellementRepository) {
        this.historiqueRenouvellementRepository = historiqueRenouvellementRepository;
    }

    public List<HistoriqueRenouvellement> findAll() {
        return historiqueRenouvellementRepository.findAll();
    }

    public Optional<HistoriqueRenouvellement> findById(Long id) {
        return historiqueRenouvellementRepository.findById(id);
    }

    public List<HistoriqueRenouvellement> findByDemandeRenouvellement(DemandeRenouvellement demandeRenouvellement) {
        return historiqueRenouvellementRepository.findByDemandeRenouvellement(demandeRenouvellement);
    }

    public HistoriqueRenouvellement save(HistoriqueRenouvellement historiqueRenouvellement) {
        return historiqueRenouvellementRepository.save(historiqueRenouvellement);
    }

    public void deleteById(Long id) {
        historiqueRenouvellementRepository.deleteById(id);
    }
}
