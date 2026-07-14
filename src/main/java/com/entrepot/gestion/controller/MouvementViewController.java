package com.entrepot.gestion.controller;

import com.entrepot.gestion.dto.*;
import com.entrepot.gestion.model.*;
import com.entrepot.gestion.service.EmplacementService;
import com.entrepot.gestion.service.MouvementService;
import com.entrepot.gestion.service.PDFService;
import com.entrepot.gestion.service.ProduitService;
import com.entrepot.gestion.service.Stocks_emplacement_service;
import com.entrepot.gestion.service.Zone_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/mouvements")
public class MouvementViewController {

    @Autowired
    private MouvementService mouvementService;
    
    @Autowired
    private PDFService pdfService;

    @Autowired 
    private Zone_service zone_service;

    @Autowired 
    private EmplacementService emplacementService;

    @Autowired
    private ProduitService produit_service;

    @Autowired
    private Stocks_emplacement_service stock_emplacement_Service;

    @GetMapping({"", "/liste"})
    public String listeMouvements(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        MouvementFiltreDTO filtre = new MouvementFiltreDTO();
        filtre.setType(type);
        filtre.setStatut(statut);
        filtre.setClientId(clientId);
        filtre.setDateDebutStr(dateDebut);
        filtre.setDateFinStr(dateFin);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateMouvement"));
        Page<MouvementListDTO> mouvementsPage = mouvementService.rechercherMouvements(filtre, pageable);

        model.addAttribute("mouvements", mouvementsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mouvementsPage.getTotalPages());
        model.addAttribute("totalElements", mouvementsPage.getTotalElements());
        model.addAttribute("filtre", filtre);

        // Ajouter les donnees pour les filtres
        model.addAttribute("clients", mouvementService.getAllClients());
        model.addAttribute("types", mouvementService.getAllTypesMouvement());
        model.addAttribute("statuts", mouvementService.getAllStatutsMouvement());

        return "mouvements/liste";
    }

    @GetMapping("/{id}/detail")
    public String detailMouvement(@PathVariable Long id, Model model) {
        MouvementDetailDTO mouvement = mouvementService.getMouvementDetail(id);
        model.addAttribute("mouvement", mouvement);
        return "mouvements/detail";
    }

    @GetMapping({"/nouvelle-entree", "/nouveau/entree"})
    public String formulaireEntree(Model model) {
        model.addAttribute("dto", new MouvementCreateDTO());
        model.addAttribute("type", "ENTREE");
        model.addAttribute("clients", mouvementService.getAllClients());
        model.addAttribute("produits", mouvementService.getAllProduits());
        model.addAttribute("emplacements", mouvementService.getAllEmplacements());
        model.addAttribute("zones" , zone_service.findAll());
        return "mouvements/form-entree";
    }

    @GetMapping({"/nouvelle-sortie", "/nouveau/sortie"})
    public String formulaireSortie(Model model) {
        model.addAttribute("dto", new MouvementCreateDTO());
        model.addAttribute("type", "SORTIE");
        model.addAttribute("clients", mouvementService.getAllClients());
        model.addAttribute("produits", mouvementService.getAllProduits());
        model.addAttribute("emplacements", mouvementService.getAllEmplacements());
        return "mouvements/form-sortie";
    }

    @GetMapping("/transfert")
    public String formulaireTransfert(Model model) {
        model.addAttribute("dto", new MouvementCreateDTO());
        model.addAttribute("type", "TRANSFERT");
        model.addAttribute("clients", mouvementService.getAllClients());
        model.addAttribute("produits", mouvementService.getAllProduits());
        model.addAttribute("emplacements", mouvementService.getAllEmplacements());
        return "mouvements/form-transfert";
    }

    @PostMapping("/nouveau")
    public String creerMouvement(
            @ModelAttribute MouvementCreateDTO dto,
            @RequestParam String type,
            RedirectAttributes redirectAttributes) {

        try {

            for (LigneMouvementDTO ligneDTO : dto.getLignes()) {
                if ("ENTREE".equals(type)) {
                    Emplacement emp = emplacementService.findById(ligneDTO.getEmplacementDestId());
                    Zone zone = zone_service.findById(ligneDTO.getIdZone());
                    Produit p = produit_service.findById(ligneDTO.getProduitId());
                    
                    BigDecimal volumeOccupe = BigDecimal.ZERO;

                    for (StockEmplacement stk : stock_emplacement_Service.findAll()) {
                        if (stk.getEmplacement() != null
                            && stk.getZone() != null
                            && stk.getEmplacement().getId().equals(emp.getId())
                            && stk.getZone().getId().equals(zone.getId())) {

                            volumeOccupe = volumeOccupe.add(
                                stk.getQuantite().multiply(stk.getProduit().getVolumeUnitaireM3())
                            );
                        }
                    }

                    BigDecimal volumeNecessaire =
                        p.getVolumeUnitaireM3().multiply(ligneDTO.getQuantite());

                    BigDecimal capacite = emp.getCapaciteVolumeM3();

                    if (volumeOccupe.add(volumeNecessaire).compareTo(capacite) > 0) {
                        throw new Exception("Espace plein");
                    }

                    StockEmplacement stk = new StockEmplacement();
                    stk.setEmplacement(emp);
                    stk.setQuantite(ligneDTO.getQuantite());
                    stk.setProduit(p);
                    stk.setZone(zone);

                    stock_emplacement_Service.save(stk);
                } else if ("SORTIE".equals(type)) {
                    Emplacement emp = emplacementService.findById(ligneDTO.getEmplacementSourceId());
                    Zone zone = zone_service.findById(ligneDTO.getIdZone());
                    Produit p = produit_service.findById(ligneDTO.getProduitId());

                    List<StockEmplacement> stocks = stock_emplacement_Service
                        .findByEmplacementIdAndProduitIdAndZoneId(emp.getId(), p.getId(), zone.getId());

                    if (stocks.isEmpty()) {
                        throw new Exception("Stock correspondant introuvable");
                    }

                    BigDecimal quantiteRestante = ligneDTO.getQuantite();
                    BigDecimal stockTotal = BigDecimal.ZERO;

                    for (StockEmplacement stock : stocks) {
                        stockTotal = stockTotal.add(stock.getQuantite());
                    }

                    if (stockTotal.compareTo(quantiteRestante) < 0) {
                        throw new Exception("Stock insuffisant pour la sortie");
                    }

                    for (StockEmplacement stock : stocks) {
                        if (quantiteRestante.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }

                        BigDecimal quantiteStock = stock.getQuantite();

                        if (quantiteStock.compareTo(quantiteRestante) <= 0) {
                            quantiteRestante = quantiteRestante.subtract(quantiteStock);
                            stock_emplacement_Service.delete(stock);
                        } else {
                            stock.setQuantite(quantiteStock.subtract(quantiteRestante));
                            stock_emplacement_Service.save(stock);
                            quantiteRestante = BigDecimal.ZERO;
                        }
                    }
                }
            }

            Long mouvementId = mouvementService.creerMouvementAvecLignes(dto, type);
            redirectAttributes.addFlashAttribute("success", "Mouvement cree avec succès (ID: " + mouvementId + ")");
            return "redirect:/mouvements/" + mouvementId + "/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la creation: " + e.getMessage());
            if ("ENTREE".equals(type)) {
                return "redirect:/mouvements/nouvelle-entree";
            } else if ("SORTIE".equals(type)) {
                return "redirect:/mouvements/nouvelle-sortie";
            } else {
                return "redirect:/mouvements/transfert";
            }
        }
    }

    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(Model model) {
        LocalDateTime debutJour = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        long entreesAujourdhui = mouvementService.countMouvementsByTypeAndDate("ENTREE", debutJour, finJour);
        long sortiesAujourdhui = mouvementService.countMouvementsByTypeAndDate("SORTIE", debutJour, finJour);
        long enAttenteValidation = mouvementService.countMouvementsByStatut("BROUILLON");

        model.addAttribute("entreesAujourdhui", entreesAujourdhui);
        model.addAttribute("sortiesAujourdhui", sortiesAujourdhui);
        model.addAttribute("enAttenteValidation", enAttenteValidation);

        List<MouvementListDTO> derniersMouvements = mouvementService.getDerniersMouvements(5);
        model.addAttribute("derniersMouvements", derniersMouvements);

        model.addAttribute("alertesStock", mouvementService.getEmplacementsStockBas());

        return "dashboard";
    }

    @PostMapping("/{id}/valider")
    public String validerMouvement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mouvementService.validerMouvement(id);
            redirectAttributes.addFlashAttribute("success", "Mouvement valide avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la validation: " + e.getMessage());
        }
        return "redirect:/mouvements/" + id + "/detail";
    }

    @PostMapping("/{id}/annuler")
    public String annulerMouvement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mouvementService.annulerMouvement(id);
            redirectAttributes.addFlashAttribute("success", "Mouvement annule avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation: " + e.getMessage());
        }
        return "redirect:/mouvements/" + id + "/detail";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exporterPDF(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {

        try {
            MouvementFiltreDTO filtre = new MouvementFiltreDTO();
            filtre.setType(type);
            filtre.setStatut(statut);
            filtre.setClientId(clientId);
            filtre.setDateDebutStr(dateDebut);
            filtre.setDateFinStr(dateFin);

            List<MouvementListDTO> mouvements = mouvementService.exporterMouvements(filtre);

            byte[] pdf = pdfService.generateMouvementsPDF(mouvements);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "mouvements_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/zones-produit/{idProduit}")
    @ResponseBody
    public List<Zone> getZonesParProduit(@PathVariable Long idProduit) {
        return zone_service.getZonesParProduit(idProduit);
    }
}
