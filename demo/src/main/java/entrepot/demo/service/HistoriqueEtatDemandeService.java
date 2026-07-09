package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.HistoriqueEtatDemande;
import entrepot.demo.repository.HistoriqueEtatDemandeRepository;

@Service
public class HistoriqueEtatDemandeService {

    private final HistoriqueEtatDemandeRepository historiqueEtatDemandeRepository;

    public HistoriqueEtatDemandeService(HistoriqueEtatDemandeRepository historiqueEtatDemandeRepository) {
        this.historiqueEtatDemandeRepository = historiqueEtatDemandeRepository;
    }

    public List<HistoriqueEtatDemande> findAll() {
        return historiqueEtatDemandeRepository.findAll();
    }

    public Optional<HistoriqueEtatDemande> findById(Long id) {
        return historiqueEtatDemandeRepository.findById(id);
    }

    public List<HistoriqueEtatDemande> findByDemandeStockage(DemandeStockage demandeStockage) {
        return historiqueEtatDemandeRepository.findByDemandeStockage(demandeStockage);
    }

    public HistoriqueEtatDemande save(HistoriqueEtatDemande historiqueEtatDemande) {
        return historiqueEtatDemandeRepository.save(historiqueEtatDemande);
    }

    public void deleteById(Long id) {
        historiqueEtatDemandeRepository.deleteById(id);
    }

    public List<HistoriqueEtatDemande> findByDemandeStockageOrderByDateStatutDesc(DemandeStockage demande) {
        return historiqueEtatDemandeRepository.findByDemandeStockageOrderByDateStatutDesc(demande);
    }


    public HistoriqueEtatDemande dernierStatut(DemandeStockage demande) {
        return findByDemandeStockageOrderByDateStatutDesc(demande).stream().findFirst().orElse(null);
    }
}
