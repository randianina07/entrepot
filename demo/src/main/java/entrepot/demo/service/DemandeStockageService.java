package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.HistoriqueEtatDemande;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.repository.DemandeStockageRepository;

@Service
public class DemandeStockageService {

    private final DemandeStockageRepository demandeStockageRepository;

    public DemandeStockageService(DemandeStockageRepository demandeStockageRepository) {
        this.demandeStockageRepository = demandeStockageRepository;
    }

    public List<DemandeStockage> findAll() {
        return demandeStockageRepository.findAll();
    }

    public Optional<DemandeStockage> findById(Long id) {
        return demandeStockageRepository.findById(id);
    }

    public List<DemandeStockage> findByUtilisateur(Utilisateur utilisateur) {
        return demandeStockageRepository.findByUtilisateur(utilisateur);
    }

    public DemandeStockage save(DemandeStockage demandeStockage) {
        return demandeStockageRepository.save(demandeStockage);
    }

    public void deleteById(Long id) {
        demandeStockageRepository.deleteById(id);
    }

    public List<DemandeStockage> demandesEnAttente(
            List<DemandeStockage> demandes,
            HistoriqueEtatDemandeService historiqueService
    ) {

        return demandes.stream().filter(demande -> {
            HistoriqueEtatDemande h = historiqueService.dernierStatut(demande);
            return h != null && h.getStatut().getCode().equals("EN_ATTENTE");
        }).toList();
    }
}
