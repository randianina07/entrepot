package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.DemandeRenouvellement;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.repository.ContratRepository;
import entrepot.demo.repository.DemandeRenouvellementRepository;
import entrepot.demo.repository.DemandeStockageRepository;
import entrepot.demo.repository.HistoriqueRenouvellementRepository;
import entrepot.demo.repository.RenouvellementContratRepository;
import entrepot.demo.repository.StatutRenouvellementRepository;
import entrepot.demo.model.DemandeStockage;
import entrepot.demo.model.HistoriqueEtatDemande;
import entrepot.demo.model.HistoriqueRenouvellement;
import entrepot.demo.model.RenouvellementContrat;
import entrepot.demo.model.StatutDemandeStockage;
import entrepot.demo.model.StatutRenouvellement;

@Service
public class ContratService {

    private final ContratRepository contratRepository;
    private final DemandeStockageRepository demandeStockageRepository;
    private final StatutDemandeStockageService statutDemandeStockageService;
    private final HistoriqueEtatDemandeService historiqueEtatDemandeService;
    private final DemandeRenouvellementRepository demandeRenouvellementRepository;
    private final StatutRenouvellementRepository statutRenouvellementRepository;
    private final HistoriqueRenouvellementRepository historiqueRenouvellementRepository;
    private final RenouvellementContratRepository renouvellementContratRepository;

    public ContratService(
            ContratRepository contratRepository,
            DemandeStockageRepository demandeStockageRepository,
            StatutDemandeStockageService statutDemandeStockageService,
            HistoriqueEtatDemandeService historiqueEtatDemandeService,
            DemandeRenouvellementRepository demandeRenouvellementRepository,
            StatutRenouvellementRepository statutRenouvellementRepository,
            HistoriqueRenouvellementRepository historiqueRenouvellementRepository,
            RenouvellementContratRepository renouvellementContratRepository) {

        this.contratRepository = contratRepository;
        this.demandeStockageRepository = demandeStockageRepository;
        this.statutDemandeStockageService = statutDemandeStockageService;
        this.historiqueEtatDemandeService = historiqueEtatDemandeService;
        this.demandeRenouvellementRepository = demandeRenouvellementRepository;
        this.statutRenouvellementRepository = statutRenouvellementRepository;
        this.historiqueRenouvellementRepository = historiqueRenouvellementRepository;
        this.renouvellementContratRepository = renouvellementContratRepository;
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

    @Transactional
    public void accepterRenouvellement(Long demandeId) {
        DemandeRenouvellement demande = demandeRenouvellementRepository.findById(demandeId).orElseThrow();
        Contrat contrat = demande.getContrat();

        contrat.setDateFin(demande.getDateFin());
        contratRepository.save(contrat);

        StatutRenouvellement accepte = statutRenouvellementRepository.findByCode("ACCEPTEE").orElseThrow();
        HistoriqueRenouvellement historique = new HistoriqueRenouvellement();

        historique.setDemandeRenouvellement(demande);
        historique.setStatutRenouvellement(accepte);
        historique.setDateStatut(LocalDateTime.now());

        historiqueRenouvellementRepository.save(historique);

        RenouvellementContrat renouvellement = new RenouvellementContrat();

        renouvellement.setContrat(contrat);
        renouvellement.setDemandeRenouvellement(demande);
        renouvellement.setDateRenouvellement(LocalDate.now());
        renouvellement.setDateFin(demande.getDateFin());

        renouvellementContratRepository.save(renouvellement);
    }
}
