package entrepot.demo.controller;

import entrepot.demo.dto.*;
import entrepot.demo.model.LigneMouvement;
import entrepot.demo.model.Mouvement;
import entrepot.demo.service.MouvementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/mouvements")
public class MouvementController {
    
    private final MouvementService mouvementService;
    
    public MouvementController(MouvementService mouvementService) {
        this.mouvementService = mouvementService;
    }
    
    @GetMapping
    public ResponseEntity<List<MouvementListDTO>> listerMouvements(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) LocalDateTime dateDebut,
            @RequestParam(required = false) LocalDateTime dateFin) {
        
        MouvementFiltreDTO filtre = new MouvementFiltreDTO(type, statut, clientId, dateDebut, dateFin);
        List<MouvementListDTO> mouvements = mouvementService.listerMouvements(filtre);
        return ResponseEntity.ok(mouvements);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MouvementDetailDTO> getMouvement(@PathVariable Long id) {
        MouvementDetailDTO mouvement = mouvementService.getMouvementDetail(id);
        return ResponseEntity.ok(mouvement);
    }
    
    @PostMapping
    public ResponseEntity<Mouvement> creerMouvement(@RequestBody MouvementCreateDTO dto) {
        Long utilisateurId = 1L;
        Mouvement mouvement = mouvementService.creerMouvement(dto, utilisateurId);
        return ResponseEntity.ok(mouvement);
    }
    
    @PostMapping("/{id}/lignes")
    public ResponseEntity<LigneMouvement> ajouterLigne(
            @PathVariable Long id,
            @RequestBody LigneMouvementDTO dto) {
        LigneMouvement ligne = mouvementService.ajouterLigne(id, dto);
        return ResponseEntity.ok(ligne);
    }
    
    @DeleteMapping("/{id}/lignes/{ligneId}")
    public ResponseEntity<Void> supprimerLigne(
            @PathVariable Long id,
            @PathVariable Long ligneId) {
        mouvementService.supprimerLigne(ligneId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/valider")
    public ResponseEntity<Void> validerMouvement(@PathVariable Long id) {
        mouvementService.validerMouvement(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerMouvement(@PathVariable Long id) {
        mouvementService.annulerMouvement(id);
        return ResponseEntity.noContent().build();
    }
}
