package com.entrepot.gestion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.entrepot.gestion.model.AbonnementStockage;
import com.entrepot.gestion.model.Contrat;
import com.entrepot.gestion.model.DemandeRenouvellement;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.repository.AbonnementStockageRepository;
import com.entrepot.gestion.repository.ContratRepository;
import com.entrepot.gestion.repository.DemandeRenouvellementRepository;
import com.entrepot.gestion.repository.DemandeStockageRepository;
import com.entrepot.gestion.repository.HistoriqueRenouvellementRepository;
import com.entrepot.gestion.repository.RenouvellementContratRepository;
import com.entrepot.gestion.repository.StatutRenouvellementRepository;
import com.entrepot.gestion.model.DemandeStockage;
import com.entrepot.gestion.model.HistoriqueEtatDemande;
import com.entrepot.gestion.model.HistoriqueRenouvellement;
import com.entrepot.gestion.model.RenouvellementContrat;
import com.entrepot.gestion.model.StatutDemandeStockage;
import com.entrepot.gestion.model.StatutRenouvellement;

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
    private final AbonnementStockageRepository abonnementStockageRepository;
    private final UtilisateurService utilisateurService;

    public ContratService(
            ContratRepository contratRepository,
            DemandeStockageRepository demandeStockageRepository,
            StatutDemandeStockageService statutDemandeStockageService,
            HistoriqueEtatDemandeService historiqueEtatDemandeService,
            DemandeRenouvellementRepository demandeRenouvellementRepository,
            StatutRenouvellementRepository statutRenouvellementRepository,
            HistoriqueRenouvellementRepository historiqueRenouvellementRepository,
            RenouvellementContratRepository renouvellementContratRepository,
            AbonnementStockageRepository abonnementStockageRepository,
            UtilisateurService utilisateurService) {

        this.contratRepository = contratRepository;
        this.demandeStockageRepository = demandeStockageRepository;
        this.statutDemandeStockageService = statutDemandeStockageService;
        this.historiqueEtatDemandeService = historiqueEtatDemandeService;
        this.demandeRenouvellementRepository = demandeRenouvellementRepository;
        this.statutRenouvellementRepository = statutRenouvellementRepository;
        this.historiqueRenouvellementRepository = historiqueRenouvellementRepository;
        this.renouvellementContratRepository = renouvellementContratRepository;
        this.abonnementStockageRepository = abonnementStockageRepository;
        this.utilisateurService = utilisateurService;
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
    public Contrat accepterDemande(Long demandeId) {

        DemandeStockage demande = demandeStockageRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande inexistante"));
        Contrat contrat = new Contrat();

        contrat.setDemandeStockage(demande);
        contrat.setUtilisateur(demande.getUtilisateur());
        contrat.setTypeZone(demande.getTypeZone());
        contrat.setTypeContrat(demande.getTypeContrat());
        contrat.setVolumeEspaceM3(demande.getVolumeEspaceM3());
        contrat.setQuantiteEmplacement(demande.getQuantiteEmplacement());
        contrat.setDateCreation(LocalDateTime.now());
        contrat.setDateDebut(demande.getDateDebut());
        contrat.setDateFin(demande.getDateFin());
        contrat.setDureeMois(null);

        if ("ABONNE".equals(demande.getTypeContrat().getCode())) {
            contrat.setDureeMois(demande.getDureeMois());
        }

        Contrat contratSauvegarde = contratRepository.save(contrat);

        if ("ABONNE".equals(demande.getTypeContrat().getCode())) {
            if (demande.getDureeMois() == null) {
                throw new RuntimeException("La durée est obligatoire pour un abonnement");
            }

            AbonnementStockage abonnement = new AbonnementStockage();

            abonnement.setUtilisateur(demande.getUtilisateur());
            abonnement.setContrat(contratSauvegarde);
            abonnement.setTypeZone(demande.getTypeZone());
            abonnement.setDureeMois(demande.getDureeMois());
            abonnementStockageRepository.save(abonnement);
        }

        StatutDemandeStockage acceptee = statutDemandeStockageService.findByCode("ACCEPTEE")
                .orElseThrow(() -> new RuntimeException("Statut ACCEPTEE introuvable"));
        HistoriqueEtatDemande historique = new HistoriqueEtatDemande();

        historique.setDemandeStockage(demande);
        historique.setStatut(acceptee);
        historique.setDateStatut(LocalDateTime.now());

        historiqueEtatDemandeService.save(historique);
        return contratSauvegarde;
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

    public List<Contrat> mesContrats(LocalDate dateDebut) {

        Utilisateur utilisateur = utilisateurService.getUtilisateurConnecte();

        if (dateDebut == null) {
            return contratRepository.findByUtilisateurIdOrderByDateDebutDesc(
                    utilisateur.getId());
        }

        return contratRepository.findByUtilisateurIdAndDateDebut(
                utilisateur.getId(),
                dateDebut);
    }
}
