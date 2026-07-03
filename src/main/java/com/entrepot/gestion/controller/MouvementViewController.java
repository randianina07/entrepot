package com.entrepot.gestion.controller;

import com.entrepot.gestion.dto.*;
import com.entrepot.gestion.model.*;
import com.entrepot.gestion.service.MouvementService;
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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/mouvements")
public class MouvementViewController {

    @Autowired
    private MouvementService mouvementService;

    @GetMapping("/liste")
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

    @GetMapping("/nouveau/entree")
    public String formulaireEntree(Model model) {
        model.addAttribute("dto", new MouvementCreateDTO());
        model.addAttribute("type", "ENTREE");
        model.addAttribute("clients", mouvementService.getAllClients());
        model.addAttribute("produits", mouvementService.getAllProduits());
        model.addAttribute("emplacements", mouvementService.getAllEmplacements());
        return "mouvements/form-entree";
    }

    @GetMapping("/nouveau/sortie")
    public String formulaireSortie(Model model) {
        model.addAttribute("dto", new MouvementCreateDTO());
        model.addAttribute("type", "SORTIE");
        model.addAttribute("clients", mouvementService.getAllClients());
        model.addAttribute("produits", mouvementService.getAllProduits());
        model.addAttribute("emplacements", mouvementService.getAllEmplacements());
        return "mouvements/form-sortie";
    }

    @PostMapping("/nouveau")
    public String creerMouvement(
            @ModelAttribute MouvementCreateDTO dto,
            @RequestParam String type,
            RedirectAttributes redirectAttributes) {

        try {
            Long mouvementId = mouvementService.creerMouvementAvecLignes(dto, type);
            redirectAttributes.addFlashAttribute("success", "Mouvement cree avec succès (ID: " + mouvementId + ")");
            return "redirect:/mouvements/" + mouvementId + "/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la creation: " + e.getMessage());
            if ("ENTREE".equals(type)) {
                return "redirect:/mouvements/nouveau/entree";
            } else {
                return "redirect:/mouvements/nouveau/sortie";
            }
        }
    }

    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(Model model) {
        // Statistiques du jour
        LocalDateTime debutJour = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finJour = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        long entreesAujourdhui = mouvementService.countMouvementsByTypeAndDate("ENTREE", debutJour, finJour);
        long sortiesAujourdhui = mouvementService.countMouvementsByTypeAndDate("SORTIE", debutJour, finJour);
        long enAttenteValidation = mouvementService.countMouvementsByStatut("BROUILLON");

        model.addAttribute("entreesAujourdhui", entreesAujourdhui);
        model.addAttribute("sortiesAujourdhui", sortiesAujourdhui);
        model.addAttribute("enAttenteValidation", enAttenteValidation);

        // 5 derniers mouvements
        List<MouvementListDTO> derniersMouvements = mouvementService.getDerniersMouvements(5);
        model.addAttribute("derniersMouvements", derniersMouvements);

        // Alertes stock bas
        model.addAttribute("alertesStock", mouvementService.getEmplacementsStockBas());

        return "mouvements/dashboard";
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

    @GetMapping("/export/csv")
    public ResponseEntity<String> exporterCSV(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {

        MouvementFiltreDTO filtre = new MouvementFiltreDTO();
        filtre.setType(type);
        filtre.setStatut(statut);
        filtre.setClientId(clientId);
        filtre.setDateDebutStr(dateDebut);
        filtre.setDateFinStr(dateFin);

        List<MouvementListDTO> mouvements = mouvementService.exporterMouvements(filtre);

        String csv = generateCSV(mouvements);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "mouvements_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    private String generateCSV(List<MouvementListDTO> mouvements) {
        StringBuilder csv = new StringBuilder();
        csv.append("Code;Date;Type;Statut;Client;Nb Lignes;Operateur\n");

        for (MouvementListDTO mvt : mouvements) {
            csv.append(mvt.getCode()).append(";");
            csv.append(mvt.getDateMouvement() != null ? mvt.getDateMouvement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "").append(";");
            csv.append(mvt.getTypeMouvement()).append(";");
            csv.append(mvt.getStatutMouvement()).append(";");
            csv.append(mvt.getClient()).append(";");
            csv.append(mvt.getNbLignes()).append(";");
            csv.append(mvt.getOperateur()).append("\n");
        }

        return csv.toString();
    }
}
