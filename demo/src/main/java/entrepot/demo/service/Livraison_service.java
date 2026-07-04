package entrepot.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entrepot.demo.model.Livraison;
import entrepot.demo.model.Tarif_livraison;
import entrepot.demo.repository.Livraison_repository;
import entrepot.demo.repository.Tarif_livraison_repository;

@Service
public class Livraison_service {
    private final Livraison_repository livraison_repository;
    private final Tarif_livraison_repository tarif_livraison_repository;

    public Livraison_service(Livraison_repository livraison_repository,
            Tarif_livraison_repository tarif_livraison_repository) {
        this.livraison_repository = livraison_repository;
        this.tarif_livraison_repository = tarif_livraison_repository;
    }

    public List<Livraison> findallLivraisons() {
        return livraison_repository.findAll();
    }

    public Livraison findById(Long id){
        return livraison_repository.findById(id)
            .orElseThrow(()-> new RuntimeException("La livraison n'existe pas"));
    }

    public List<Livraison> findByMissionId(Long missionId) {
        return livraison_repository.findByMissionId(missionId);
    }

    public List<Livraison> findByVehiculeId(Long vehiculeId) {
        return livraison_repository.findByMissionVehiculeId(vehiculeId);
    }

    @Transactional
    public void configurerLivraison(Long livraisonId, Long tarifId, LocalDate datePrevue, LocalTime heurePrevue) {
        Livraison livraison = findById(livraisonId);
        Tarif_livraison tarif = tarif_livraison_repository.findById(tarifId)
                .orElseThrow(() -> new RuntimeException("Tarif livraison introuvable"));

        if (livraison.getZoneLivraison() == null
                || tarif.getZoneLivraison() == null
                || !livraison.getZoneLivraison().getId().equals(tarif.getZoneLivraison().getId())) {
            throw new RuntimeException("Le tarif choisi ne correspond pas a la zone de la livraison");
        }

        LocalDateTime dateHeurePrevue = LocalDateTime.of(datePrevue, heurePrevue);
        livraison.setDatePrevue(dateHeurePrevue);

        double poids = livraison.getPoidsTotal() == null ? 0.0 : livraison.getPoidsTotal();
        double volume = livraison.getVolumeTotal() == null ? 0.0 : livraison.getVolumeTotal();
        double prixKg = tarif.getPrixParKg() == null ? 0.0 : tarif.getPrixParKg();
        double prixM3 = tarif.getPrixParm() == null ? 0.0 : tarif.getPrixParm();
        double baseZone = tarif.getZoneLivraison().getTarifBase() == null ? 0.0 : tarif.getZoneLivraison().getTarifBase();

        livraison.setMontantLivraison((poids * prixKg) + (volume * prixM3) + baseZone);
        livraison_repository.save(livraison);
    }
}