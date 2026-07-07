package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.repository.ContratRepository;
import entrepot.demo.repository.DemandeStockageRepository;
import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.HistoriqueEtatDemande;
import entrepot.demo.model.StatutDemandeStockage;

@Service
public class ContratService {

    private final ContratRepository contratRepository;
    private final DemandeStockageRepository demandeStockageRepository;
    private final StatutDemandeStockageService statutDemandeStockageService;
    private final HistoriqueEtatDemandeService historiqueEtatDemandeService;

    public ContratService(
            ContratRepository contratRepository,
            DemandeStockageRepository demandeStockageRepository,
            StatutDemandeStockageService statutDemandeStockageService,
            HistoriqueEtatDemandeService historiqueEtatDemandeService) {

        this.contratRepository = contratRepository;
        this.demandeStockageRepository = demandeStockageRepository;
        this.statutDemandeStockageService = statutDemandeStockageService;
        this.historiqueEtatDemandeService = historiqueEtatDemandeService;
    }

    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    public Optional<Contrat> findById(Long id) {
        return contratRepository.findById(id);
    }

    public List<Contrat> findByUtilisateur(Utilisateur utilisateur) {
        return contratRepository.findByUtilisateur(utilisateur);
    }

    public Contrat save(Contrat contrat) {
        return contratRepository.save(contrat);
    }

    public void deleteById(Long id) {
        contratRepository.deleteById(id);
    }

    @Transactional
    public void accepterDemande(Long demandeId) {
        DemandeStockage demande = demandeStockageRepository.findById(demandeId).orElseThrow();
        StatutDemandeStockage accepte = statutDemandeStockageService.findByCode("ACCEPTEE").orElseThrow();
        HistoriqueEtatDemande historique = new HistoriqueEtatDemande();

        historique.setDemandeStockage(demande);
        historique.setStatut(accepte);
        historique.setDateStatut(LocalDateTime.now());

        historiqueEtatDemandeService.save(historique);

        Contrat contrat = new Contrat();

        contrat.setDemandeStockage(demande);
        contrat.setUtilisateur(demande.getUtilisateur());
        contrat.setTypeZone(demande.getTypeZone());
        contrat.setTypeContrat(demande.getTypeContrat());
        contrat.setVolumeEspaceM3(demande.getVolumeEspaceM3());
        contrat.setDateCreation(LocalDateTime.now());
        contrat.setDateDebut(demande.getDateDebut());
        contrat.setDateFin(demande.getDateFin());
        contrat.setDescription("Contrat créé automatiquement suite à une demande acceptée");
        contratRepository.save(contrat);
    }
}
