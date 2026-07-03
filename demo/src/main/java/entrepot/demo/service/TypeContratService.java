package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.TypeContrat;
import entrepot.demo.repository.TypeContratRepository;

@Service
public class TypeContratService {

    private final TypeContratRepository typeContratRepository;

    public TypeContratService(TypeContratRepository typeContratRepository) {
        this.typeContratRepository = typeContratRepository;
    }

    public List<TypeContrat> findAll() {
        return typeContratRepository.findAll();
    }

    public Optional<TypeContrat> findById(Long id) {
        return typeContratRepository.findById(id);
    }

    public Optional<TypeContrat> findByCode(String code) {
        return typeContratRepository.findByCode(code);
    }

    public TypeContrat save(TypeContrat typeContrat) {
        return typeContratRepository.save(typeContrat);
    }

    public void deleteById(Long id) {
        typeContratRepository.deleteById(id);
    }
}