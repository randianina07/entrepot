package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.TarifZone;
import entrepot.demo.repository.TarifZoneRepository;

@Service
public class TarifZoneService {

    private final TarifZoneRepository tarifZoneRepository;

    public TarifZoneService(TarifZoneRepository tarifZoneRepository) {
        this.tarifZoneRepository = tarifZoneRepository;
    }

    public List<TarifZone> findAll() {
        return tarifZoneRepository.findAll();
    }

    public Optional<TarifZone> findById(Long id) {
        return tarifZoneRepository.findById(id);
    }

    public TarifZone save(TarifZone tarifZone) {
        return tarifZoneRepository.save(tarifZone);
    }

    public void deleteById(Long id) {
        tarifZoneRepository.deleteById(id);
    }
}
