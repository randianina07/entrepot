package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.UniteDuree;
import entrepot.demo.repository.UniteDureeRepository;

@Service
public class UniteDureeService {

    private final UniteDureeRepository uniteDureeRepository;

    public UniteDureeService(UniteDureeRepository uniteDureeRepository) {
        this.uniteDureeRepository = uniteDureeRepository;
    }

    public List<UniteDuree> findAll() {
        return uniteDureeRepository.findAll();
    }

    public Optional<UniteDuree> findById(Long id) {
        return uniteDureeRepository.findById(id);
    }

    public Optional<UniteDuree> findByCode(String code) {
        return uniteDureeRepository.findByCode(code);
    }

    public UniteDuree save(UniteDuree uniteDuree) {
        return uniteDureeRepository.save(uniteDuree);
    }

    public void deleteById(Long id) {
        uniteDureeRepository.deleteById(id);
    }
}
