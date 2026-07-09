package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.TypeZone;
import entrepot.demo.repository.TypeZoneRepository;

@Service
public class TypeZoneService {

    private final TypeZoneRepository typeZoneRepository;


    public TypeZoneService(TypeZoneRepository typeZoneRepository) {
        this.typeZoneRepository = typeZoneRepository;
    }


    public List<TypeZone> findAll() {
        return typeZoneRepository.findAll();
    }


    public Optional<TypeZone> findById(Long id) {
        return typeZoneRepository.findById(id);
    }


    public TypeZone save(TypeZone typeZone) {
        return typeZoneRepository.save(typeZone);
    }


    public void deleteById(Long id) {
        typeZoneRepository.deleteById(id);
    }
}