package com.entrepot.gestion.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.dto.AlerteStockEmplacementDTO;
import com.entrepot.gestion.dto.LigneMouvementDTO;
import com.entrepot.gestion.dto.LigneMouvementResponseDTO;
import com.entrepot.gestion.dto.MouvementCreateDTO;
import com.entrepot.gestion.dto.MouvementDetailDTO;
import com.entrepot.gestion.dto.MouvementFiltreDTO;
import com.entrepot.gestion.dto.MouvementListDTO;
import com.entrepot.gestion.model.Emplacement;
import com.entrepot.gestion.model.FluxEntreesSorties;
import com.entrepot.gestion.model.LigneMouvement;
import com.entrepot.gestion.model.Mouvement;
import com.entrepot.gestion.model.Produit;
import com.entrepot.gestion.model.StatsClient;
import com.entrepot.gestion.model.StatutMouvement;
import com.entrepot.gestion.model.StockEmplacement;
import com.entrepot.gestion.model.TopProduit;
import com.entrepot.gestion.model.TypeMouvement;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.repository.EmplacementRepository;
import com.entrepot.gestion.repository.FluxEntreesSortiesRepository;
import com.entrepot.gestion.repository.LigneMouvementRepository;
import com.entrepot.gestion.repository.MouvementRepository;
import com.entrepot.gestion.repository.ProduitRepository;
import com.entrepot.gestion.repository.StatsClientRepository;
import com.entrepot.gestion.repository.StatutMouvementRepository;
import com.entrepot.gestion.repository.StockEmplacementRepository;
import com.entrepot.gestion.repository.TopProduitRepository;
import com.entrepot.gestion.repository.TypeMouvementRepository;
import com.entrepot.gestion.repository.UtilisateurRepository;

import jakarta.transaction.Transactional;

@Service
public class MouvementService {
    
    private final MouvementRepository mouvementRepository;
    private final LigneMouvementRepository ligneMouvementRepository;
    private final TypeMouvementRepository typeMouvementRepository;
    private final StatutMouvementRepository statutMouvementRepository;
    private final FluxEntreesSortiesRepository fluxEntreesSortiesRepository;
    
    private final EmplacementRepository emplacementRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final StockEmplacementRepository stockEmplacementRepository;
    private final StatsClientRepository statsClientRepository;
    private final TopProduitRepository topProduitRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    public MouvementService(MouvementRepository mouvementRepository, LigneMouvementRepository ligneMouvementRepository, TypeMouvementRepository typeMouvementRepository, StatutMouvementRepository statutMouvementRepository, FluxEntreesSortiesRepository fluxEntreesSortiesRepository, EmplacementRepository emplacementRepository, ProduitRepository produitRepository, UtilisateurRepository utilisateurRepository, StockEmplacementRepository stockEmplacementRepository, StatsClientRepository statsClientRepository, TopProduitRepository topProduitRepository) {
        this.mouvementRepository = mouvementRepository;
        this.ligneMouvementRepository = ligneMouvementRepository;
        this.typeMouvementRepository = typeMouvementRepository;
        this.statutMouvementRepository = statutMouvementRepository;
        this.fluxEntreesSortiesRepository = fluxEntreesSortiesRepository;
        this.emplacementRepository = emplacementRepository;
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.stockEmplacementRepository = stockEmplacementRepository;
        this.statsClientRepository = statsClientRepository;
        this.topProduitRepository = topProduitRepository;
    }
    
    public String genererCodeMouvement() {
        LocalDate today = LocalDate.now();
        String datePrefix = today.format(DATE_FORMATTER);
        
        Long count = mouvementRepository.countByDateMouvementBetween(
            today.atStartOfDay(),
            today.atTime(23, 59, 59)
        );
        
        Long sequence = (count != null ? count : 0) + 1;
        return String.format("MVT-%s-%04d", datePrefix, sequence);
    }
    
    @Transactional
    public Mouvement creerMouvement(MouvementCreateDTO dto, Long utilisateurId) {
        TypeMouvement typeMouvement = typeMouvementRepository.findById(dto.getTypeMouvementId())
            .orElseThrow(() -> new RuntimeException("Type de mouvement non trouvé"));

        StatutMouvement statutBrouillon = statutMouvementRepository.findByCode("BROUILLON")
            .or(() -> statutMouvementRepository.findByCode("EN_ATTENTE"))
            .orElseThrow(() -> new RuntimeException("Statut initial introuvable (BROUILLON / EN_ATTENTE)"));
        
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Utilisateur client = null;
        if (dto.getClientId() != null) {
            client = utilisateurRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        }
        
        Mouvement mouvement = new Mouvement();
        mouvement.setCode(genererCodeMouvement());
        mouvement.setDateMouvement(LocalDateTime.now());
        mouvement.setTypeMouvement(typeMouvement);
        mouvement.setStatutMouvement(statutBrouillon);
        mouvement.setClient(client);
        mouvement.setUtilisateur(utilisateur);
        mouvement.setNotes(dto.getNotes());
        
        return mouvementRepository.save(mouvement);
    }
    
    @Transactional
    public LigneMouvement ajouterLigne(Long mouvementId, LigneMouvementDTO dto) {
        Mouvement mouvement = mouvementRepository.findById(mouvementId)
            .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));

        return creerEtSauverLigne(mouvement, dto);
    }
    
    @Transactional
    public void supprimerLigne(Long ligneMouvementId) {
        ligneMouvementRepository.deleteById(ligneMouvementId);
    }
    
    public void verifierStockSuffisant(Long emplacementId, Long produitId, BigDecimal quantite) {
        StockEmplacement stock = stockEmplacementRepository
            .findByEmplacementIdAndProduitId(emplacementId, produitId)
            .orElse(null);
        
        if (stock == null || stock.getQuantite().compareTo(quantite) < 0) {
            throw new RuntimeException("Stock insuffisant pour ce produit à cet emplacement");
        }
    }
    
    public void verifierCapaciteEmplacement(Long emplacementId, BigDecimal volumeAjoute) {
        Emplacement emplacement = emplacementRepository.findById(emplacementId)
            .orElseThrow(() -> new RuntimeException("Emplacement non trouvé"));
        
        BigDecimal volumeOccupe = stockEmplacementRepository
            .sumVolumeByEmplacementId(emplacementId);
        
        if (volumeOccupe == null) {
            volumeOccupe = BigDecimal.ZERO;
        }
        
        BigDecimal volumeTotal = volumeOccupe.add(volumeAjoute);
        
        if (volumeTotal.compareTo(emplacement.getCapaciteVolumeM3()) > 0) {
            throw new RuntimeException("Capacité de l'emplacement dépassée");
        }
    }
    
    @Transactional
    public void validerMouvement(Long mouvementId) {
        Mouvement mouvement = mouvementRepository.findById(mouvementId)
            .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));
        
        StatutMouvement statutValide = statutMouvementRepository.findByCode("VALIDE")
            .orElseThrow(() -> new RuntimeException("Statut VALIDE non trouvé"));
        
        String statutActuel = mouvement.getStatutMouvement() != null ? mouvement.getStatutMouvement().getCode() : null;
        boolean peutValider = "BROUILLON".equals(statutActuel)
                || "EN_ATTENTE".equals(statutActuel)
                || "EN_CONTROLE".equals(statutActuel);

        if (!peutValider) {
            throw new RuntimeException("Seuls les mouvements en attente/brouillon peuvent être validés");
        }
        
        for (LigneMouvement ligne : mouvement.getLignes()) {
            String sens = mouvement.getTypeMouvement().getSens();
            
            if (sens.equals("SORTIE")) {
                verifierStockSuffisant(
                    ligne.getEmplacementSource().getId(),
                    ligne.getProduit().getId(),
                    ligne.getQuantite()
                );
                mettreAJourStockSortie(ligne);
            } else if (sens.equals("ENTREE")) {
                mettreAJourStockEntree(ligne);
            } else if (mouvement.getTypeMouvement().getCode().equals("TRANSFERT_INTERNE")) {
                mettreAJourStockTransfert(ligne);
            }
        }
        
        mouvement.setStatutMouvement(statutValide);
        mouvementRepository.save(mouvement);
        
        enregistrerFlux(mouvement);
        mettreAJourStatsClient(mouvement.getClient() != null ? mouvement.getClient().getId() : null);
        mettreAJourTopProduits();
    }
    
    @Transactional
    public void annulerMouvement(Long mouvementId) {
        Mouvement mouvement = mouvementRepository.findById(mouvementId)
            .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));
        
        StatutMouvement statutAnnule = statutMouvementRepository.findByCode("ANNULE")
            .orElseThrow(() -> new RuntimeException("Statut ANNULE non trouvé"));
        
        if (mouvement.getStatutMouvement().getCode().equals("VALIDE")) {
            for (LigneMouvement ligne : mouvement.getLignes()) {
                rollbackStock(ligne);
            }
        }
        
        mouvement.setStatutMouvement(statutAnnule);
        mouvementRepository.save(mouvement);
    }
    
    @Transactional
    public void mettreAJourStockEntree(LigneMouvement ligne) {
        StockEmplacement stock = stockEmplacementRepository
            .findByEmplacementIdAndProduitId(
                ligne.getEmplacementDest().getId(),
                ligne.getProduit().getId()
            )
            .orElse(null);
        
        if (stock == null) {
            stock = new StockEmplacement();
            stock.setId(null);
            stock.setEmplacement(ligne.getEmplacementDest());
            stock.setProduit(ligne.getProduit());
            stock.setQuantite(ligne.getQuantite());
            stock = stockEmplacementRepository.saveAndFlush(stock);
        } else {
            stock.setQuantite(stock.getQuantite().add(ligne.getQuantite()));
            stockEmplacementRepository.saveAndFlush(stock);
        }
    }
    
    @Transactional
    public void mettreAJourStockSortie(LigneMouvement ligne) {
        StockEmplacement stock = stockEmplacementRepository
            .findByEmplacementIdAndProduitId(
                ligne.getEmplacementSource().getId(),
                ligne.getProduit().getId()
            )
            .orElseThrow(() -> new RuntimeException("Stock non trouvé"));
        
        stock.setQuantite(stock.getQuantite().subtract(ligne.getQuantite()));
        
        if (stock.getQuantite().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Stock insuffisant");
        }
        
        stockEmplacementRepository.save(stock);
    }
    
    @Transactional
    public void mettreAJourStockTransfert(LigneMouvement ligne) {
        mettreAJourStockSortie(ligne);
        mettreAJourStockEntree(ligne);
    }
    
    @Transactional
    public void rollbackStock(LigneMouvement ligne) {
        String sens = ligne.getMouvement().getTypeMouvement().getSens();
        
        if (sens.equals("ENTREE")) {
            mettreAJourStockSortie(ligne);
        } else if (sens.equals("SORTIE")) {
            mettreAJourStockEntree(ligne);
        } else if (ligne.getMouvement().getTypeMouvement().getCode().equals("TRANSFERT_INTERNE")) {
            StockEmplacement stockSource = stockEmplacementRepository
                .findByEmplacementIdAndProduitId(
                    ligne.getEmplacementSource().getId(),
                    ligne.getProduit().getId()
                )
                .orElseThrow(() -> new RuntimeException("Stock source non trouvé"));
            
            stockSource.setQuantite(stockSource.getQuantite().add(ligne.getQuantite()));
            stockEmplacementRepository.save(stockSource);
            
            StockEmplacement stockDest = stockEmplacementRepository
                .findByEmplacementIdAndProduitId(
                    ligne.getEmplacementDest().getId(),
                    ligne.getProduit().getId()
                )
                .orElseThrow(() -> new RuntimeException("Stock destination non trouvé"));
            
            stockDest.setQuantite(stockDest.getQuantite().subtract(ligne.getQuantite()));
            
            if (stockDest.getQuantite().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Stock destination insuffisant pour rollback");
            }
            
            stockEmplacementRepository.save(stockDest);
        }
    }
    
    @Transactional
    public void enregistrerFlux(Mouvement mouvement) {
        LocalDate date = mouvement.getDateMouvement().toLocalDate();
        String typeFlux = mouvement.getTypeMouvement().getSens();
        String typeDetail = mouvement.getTypeMouvement().getCode();
        
        BigDecimal quantiteTotale = BigDecimal.ZERO;
        BigDecimal volumeTotal = BigDecimal.ZERO;
        
        for (LigneMouvement ligne : mouvement.getLignes()) {
            quantiteTotale = quantiteTotale.add(ligne.getQuantite());
            
            BigDecimal volumeLigne = ligne.getQuantite().multiply(
                ligne.getProduit().getVolumeUnitaireM3()
            );
            volumeTotal = volumeTotal.add(volumeLigne);
        }
        
        FluxEntreesSorties flux = new FluxEntreesSorties();
        flux.setDate(date);
        flux.setTypeFlux(typeFlux);
        flux.setTypeDetail(typeDetail);
        flux.setQuantite(quantiteTotale);
        flux.setVolumeM3(volumeTotal);
        flux.setMouvement(mouvement);
        
        fluxEntreesSortiesRepository.save(flux);
    }
    
    @Transactional
    public void mettreAJourStatsClient(Long clientId) {
        if (clientId == null) {
            return;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate debutMois = today.withDayOfMonth(1);
        
        List<Mouvement> mouvements = mouvementRepository.findByClient_IdAndDateMouvementBetween(
            clientId, debutMois.atStartOfDay(), today.atTime(23, 59, 59)
        );
        
        int nbEntrees = (int) mouvements.stream()
            .filter(m -> m.getTypeMouvement().getSens().equals("ENTREE"))
            .count();
        
        int nbSorties = (int) mouvements.stream()
            .filter(m -> m.getTypeMouvement().getSens().equals("SORTIE"))
            .count();
        
        // TODO: Implement client volume calculation when contrats relationship is added
        BigDecimal volumeStocke = BigDecimal.ZERO;
        
        if (volumeStocke == null) {
            volumeStocke = BigDecimal.ZERO;
        }
        
        StatsClient stats = statsClientRepository.findByClientIdAndDateDebutAndDateFin(
            clientId, debutMois, today
        ).orElse(new StatsClient());
        
        Utilisateur client = utilisateurRepository.findById(clientId).orElse(null);
        stats.setClient(client);
        stats.setDateDebut(debutMois);
        stats.setDateFin(today);
        stats.setNbEntrees(nbEntrees);
        stats.setNbSorties(nbSorties);
        stats.setVolumeStockeM3(volumeStocke);
        
        statsClientRepository.save(stats);
    }
    
    @Transactional
    public void mettreAJourTopProduits() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        
        List<Object[]> results = ligneMouvementRepository
            .sumQuantiteByProduitGroupedByProduit(startOfDay);
        
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Long produitId = (Long) row[0];
            BigDecimal quantiteTotale = (BigDecimal) row[1];
            
            TopProduit topProduit = topProduitRepository
                .findByDateSnapshotAndProduitId(today, produitId)
                .orElse(new TopProduit());
            
            topProduit.setDateSnapshot(today);
            topProduit.setRang(i + 1);
            topProduit.setQuantiteTotale(quantiteTotale);
            
            Produit produit = produitRepository.findById(produitId).orElse(null);
            if (produit != null) {
                topProduit.setProduit(produit);
            }
            
            topProduitRepository.save(topProduit);
        }
    }
    
    public List<MouvementListDTO> listerMouvements(MouvementFiltreDTO filtre) {
        List<Mouvement> mouvements;
        
        // Convertir les dates en String vers LocalDateTime si nécessaire
        if (filtre != null) {
            if (filtre.getDateDebutStr() != null && !filtre.getDateDebutStr().isEmpty() && filtre.getDateDebut() == null) {
                filtre.setDateDebut(java.time.LocalDate.parse(filtre.getDateDebutStr()).atStartOfDay());
            }
            if (filtre.getDateFinStr() != null && !filtre.getDateFinStr().isEmpty() && filtre.getDateFin() == null) {
                filtre.setDateFin(java.time.LocalDate.parse(filtre.getDateFinStr()).atTime(23, 59, 59));
            }
        }
        
        if (filtre != null && filtre.getType() != null && !filtre.getType().isEmpty()) {
            mouvements = mouvementRepository.findByTypeMouvement_Sens(filtre.getType());
        } else if (filtre != null && filtre.getStatut() != null && !filtre.getStatut().isEmpty()) {
            mouvements = mouvementRepository.findByStatutMouvement_Code(filtre.getStatut());
        } else if (filtre != null && filtre.getClientId() != null) {
            mouvements = mouvementRepository.findByClient_Id(filtre.getClientId());
        } else if (filtre != null && filtre.getDateDebut() != null && filtre.getDateFin() != null) {
            mouvements = mouvementRepository.findByDateMouvementBetween(
                filtre.getDateDebut(), filtre.getDateFin()
            );
        } else {
            mouvements = mouvementRepository.findAll();
        }
        
        return mouvements.stream().map(this::mapToListDTO).collect(Collectors.toList());
    }
    
    public MouvementDetailDTO getMouvementDetail(Long id) {
        Mouvement mouvement = mouvementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mouvement non trouvé"));
        
        return mapToDetailDTO(mouvement);
    }
    
    private MouvementListDTO mapToListDTO(Mouvement mouvement) {
        MouvementListDTO dto = new MouvementListDTO();
        dto.setId(mouvement.getId());
        dto.setCode(mouvement.getCode());
        dto.setDateMouvement(mouvement.getDateMouvement());
        dto.setTypeMouvementCode(mouvement.getTypeMouvement().getCode());
        dto.setTypeMouvementLibelle(mouvement.getTypeMouvement().getLibelle());
        dto.setStatutMouvementCode(mouvement.getStatutMouvement().getCode());
        dto.setStatutMouvementLibelle(mouvement.getStatutMouvement().getLibelle());
        dto.setClientEmail(mouvement.getClient() != null ? mouvement.getClient().getEmail() : null);
        dto.setUtilisateurEmail(mouvement.getUtilisateur().getEmail());
        
        // Additional fields for template compatibility
        dto.setTypeMouvement(mouvement.getTypeMouvement().getCode());
        dto.setStatutMouvement(mouvement.getStatutMouvement().getCode());
        dto.setClient(mouvement.getClient() != null ? mouvement.getClient().getDisplayName() : null);
        dto.setNbLignes(mouvement.getLignes() != null ? mouvement.getLignes().size() : 0);
        dto.setOperateur(mouvement.getUtilisateur() != null ? mouvement.getUtilisateur().getDisplayName() : null);
        
        return dto;
    }
    
    private MouvementDetailDTO mapToDetailDTO(Mouvement mouvement) {
        MouvementDetailDTO dto = new MouvementDetailDTO();
        dto.setId(mouvement.getId());
        dto.setCode(mouvement.getCode());
        dto.setDateMouvement(mouvement.getDateMouvement());
        dto.setTypeMouvementId(mouvement.getTypeMouvement().getId());
        dto.setTypeMouvementCode(mouvement.getTypeMouvement().getCode());
        dto.setTypeMouvementLibelle(mouvement.getTypeMouvement().getLibelle());
        dto.setSens(mouvement.getTypeMouvement().getSens());
        dto.setStatutMouvementId(mouvement.getStatutMouvement().getId());
        dto.setStatutMouvementCode(mouvement.getStatutMouvement().getCode());
        dto.setStatutMouvementLibelle(mouvement.getStatutMouvement().getLibelle());
        dto.setClientId(mouvement.getClient() != null ? mouvement.getClient().getId() : null);
        dto.setClientEmail(mouvement.getClient() != null ? mouvement.getClient().getEmail() : null);
        dto.setUtilisateurId(mouvement.getUtilisateur().getId());
        dto.setUtilisateurEmail(mouvement.getUtilisateur().getEmail());
        dto.setNotes(mouvement.getNotes());
        
        List<LigneMouvementResponseDTO> lignesDTO = mouvement.getLignes().stream()
            .map(this::mapLigneToDTO)
            .collect(Collectors.toList());
        dto.setLignes(lignesDTO);
        
        return dto;
    }
    
    private LigneMouvementResponseDTO mapLigneToDTO(LigneMouvement ligne) {
        LigneMouvementResponseDTO dto = new LigneMouvementResponseDTO();
        dto.setId(ligne.getId());
        dto.setProduitId(ligne.getProduit().getId());
        dto.setProduitNom(ligne.getProduit().getNom());
        dto.setEmplacementSourceId(ligne.getEmplacementSource() != null ? ligne.getEmplacementSource().getId() : null);
        dto.setEmplacementSourceCode(ligne.getEmplacementSource() != null ? ligne.getEmplacementSource().getCode() : null);
        dto.setEmplacementDestId(ligne.getEmplacementDest() != null ? ligne.getEmplacementDest().getId() : null);
        dto.setEmplacementDestCode(ligne.getEmplacementDest() != null ? ligne.getEmplacementDest().getCode() : null);
        dto.setQuantite(ligne.getQuantite());
        return dto;
    }
    
    // Additional methods for MouvementViewController
    
    public List<Utilisateur> getAllClients() {
        return utilisateurRepository.findAll();
    }
    
    public List<TypeMouvement> getAllTypesMouvement() {
        return typeMouvementRepository.findAll();
    }
    
    public List<StatutMouvement> getAllStatutsMouvement() {
        return statutMouvementRepository.findAll();
    }
    
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }
    
    public List<Emplacement> getAllEmplacements() {
        return emplacementRepository.findAll();
    }
    
    public Page<MouvementListDTO> rechercherMouvements(MouvementFiltreDTO filtre, Pageable pageable) {
        List<Mouvement> mouvements;
        
        // Convertir les dates en String vers LocalDateTime si nécessaire
        if (filtre != null) {
            if (filtre.getDateDebutStr() != null && !filtre.getDateDebutStr().isEmpty() && filtre.getDateDebut() == null) {
                filtre.setDateDebut(java.time.LocalDate.parse(filtre.getDateDebutStr()).atStartOfDay());
            }
            if (filtre.getDateFinStr() != null && !filtre.getDateFinStr().isEmpty() && filtre.getDateFin() == null) {
                filtre.setDateFin(java.time.LocalDate.parse(filtre.getDateFinStr()).atTime(23, 59, 59));
            }
        }
        
        if (filtre != null && filtre.getType() != null && !filtre.getType().isEmpty()) {
            mouvements = mouvementRepository.findByTypeMouvement_Code(filtre.getType());
        } else if (filtre != null && filtre.getStatut() != null && !filtre.getStatut().isEmpty()) {
            mouvements = mouvementRepository.findByStatutMouvement_Code(filtre.getStatut());
        } else if (filtre != null && filtre.getClientId() != null) {
            mouvements = mouvementRepository.findByClient_Id(filtre.getClientId());
        } else if (filtre != null && filtre.getDateDebut() != null && filtre.getDateFin() != null) {
            mouvements = mouvementRepository.findByDateMouvementBetween(
                filtre.getDateDebut(), filtre.getDateFin()
            );
        } else {
            mouvements = mouvementRepository.findAll();
        }
        
        // Sort by dateMouvement descending
        List<MouvementListDTO> dtoList = mouvements.stream()
            .sorted((a, b) -> {
                if (a.getDateMouvement() == null) return 1;
                if (b.getDateMouvement() == null) return -1;
                return b.getDateMouvement().compareTo(a.getDateMouvement());
            })
            .map(this::mapToListDTO)
            .collect(Collectors.toList());
        
        // Apply pagination slice
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        
        List<MouvementListDTO> pageContent = start >= dtoList.size() 
            ? List.of() 
            : dtoList.subList(start, end);
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, dtoList.size());
    }
    
    @Transactional
    public Long creerMouvementAvecLignes(MouvementCreateDTO dto, String type) {
        TypeMouvement typeMouvement = typeMouvementRepository.findByCode(type)
            .or(() -> typeMouvementRepository.findFirstBySensOrderByIdAsc(type))
            .orElseThrow(() -> new RuntimeException("Type de mouvement non trouvé: " + type));

        dto.setTypeMouvementId(typeMouvement.getId());
        
        Long utilisateurId = 1L; // TODO: Get from authenticated user
        Mouvement mouvement = creerMouvement(dto, utilisateurId);

        if (dto.getLignes() != null) {
            for (LigneMouvementDTO ligneDTO : dto.getLignes()) {
                if (ligneDTO == null || ligneDTO.getProduitId() == null || ligneDTO.getQuantite() == null) {
                    continue;
                }
                creerEtSauverLigne(mouvement, ligneDTO);
            }
        }
        
        return mouvement.getId();
    }

    private LigneMouvement creerEtSauverLigne(Mouvement mouvement, LigneMouvementDTO dto) {
        Produit produit = produitRepository.findById(dto.getProduitId())
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Emplacement emplacementSource = null;
        if (dto.getEmplacementSourceId() != null) {
            emplacementSource = emplacementRepository.findById(dto.getEmplacementSourceId())
                .orElseThrow(() -> new RuntimeException("Emplacement source non trouvé"));
        }

        Emplacement emplacementDest = null;
        if (dto.getEmplacementDestId() != null) {
            emplacementDest = emplacementRepository.findById(dto.getEmplacementDestId())
                .orElseThrow(() -> new RuntimeException("Emplacement destination non trouvé"));
        }

        LigneMouvement ligne = new LigneMouvement();
        ligne.setMouvement(mouvement);
        ligne.setProduit(produit);
        ligne.setEmplacementSource(emplacementSource);
        ligne.setEmplacementDest(emplacementDest);
        ligne.setQuantite(dto.getQuantite());

        return ligneMouvementRepository.save(ligne);
    }
    
    public long countMouvementsByTypeAndDate(String type, LocalDateTime debut, LocalDateTime fin) {
        List<Mouvement> mouvements = mouvementRepository.findByDateMouvementBetween(debut, fin);
        return mouvements.stream()
            .filter(m -> m.getTypeMouvement() != null && type.equals(m.getTypeMouvement().getSens()))
            .filter(m -> m.getStatutMouvement() != null && "VALIDE".equals(m.getStatutMouvement().getCode()))
            .count();
    }
    
    public long countMouvementsByStatut(String statut) {
        List<Mouvement> mouvements = mouvementRepository.findByStatutMouvement_Code(statut);

        if (!mouvements.isEmpty() || !"BROUILLON".equals(statut)) {
            return mouvements.size();
        }

        long enAttente = mouvementRepository.findByStatutMouvement_Code("EN_ATTENTE").size();
        long enControle = mouvementRepository.findByStatutMouvement_Code("EN_CONTROLE").size();
        return enAttente + enControle;
    }
    
    public List<MouvementListDTO> getDerniersMouvements(int limit) {
        List<Mouvement> mouvements = mouvementRepository.findTop5ByOrderByDateMouvementDesc();
        return mouvements.stream()
            .limit(limit)
            .map(this::mapToListDTO)
            .collect(Collectors.toList());
    }
    
    public List<AlerteStockEmplacementDTO> getEmplacementssature() {
        List<Emplacement> allEmplacements = emplacementRepository.findAll();
        return allEmplacements.stream()
            .filter(emp -> {
                BigDecimal volumeOccupe = stockEmplacementRepository.sumVolumeByEmplacementId(emp.getId());
                if (volumeOccupe == null) volumeOccupe = BigDecimal.ZERO;
                BigDecimal capacite = emp.getCapaciteVolumeM3();
                if (capacite == null) capacite = BigDecimal.ZERO;
                return capacite.compareTo(BigDecimal.ZERO) > 0
                && volumeOccupe.compareTo(capacite.multiply(new BigDecimal("0.9"))) >= 0;
            })
            .map(emp -> {
                BigDecimal volumeOccupe = stockEmplacementRepository.sumVolumeByEmplacementId(emp.getId());
                if (volumeOccupe == null) {
                    volumeOccupe = BigDecimal.ZERO;
                }

                BigDecimal capacite = emp.getCapaciteVolumeM3();
                if (capacite == null) capacite = BigDecimal.ZERO;

                BigDecimal pourcentage = capacite.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : volumeOccupe.multiply(new BigDecimal("100")).divide(capacite, 2, java.math.RoundingMode.HALF_UP);

                return new AlerteStockEmplacementDTO(
                    emp.getCode(),
                    pourcentage,
                    volumeOccupe,
                    capacite
                );
            })
            .collect(Collectors.toList());
    }
    
    public BigDecimal getStockDisponible(Long produitId, Long emplacementId) {
        return stockEmplacementRepository
            .findByEmplacementIdAndProduitId(emplacementId, produitId)
            .map(StockEmplacement::getQuantite)
            .orElse(BigDecimal.ZERO);
    }
    
    public List<MouvementListDTO> exporterMouvements(MouvementFiltreDTO filtre) {
        return listerMouvements(filtre);
    }
}
