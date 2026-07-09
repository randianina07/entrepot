package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.TypeProduit;
import entrepot.demo.repository.TypeProduitRepository;

@Service
public class TypeProduitService {

    private final TypeProduitRepository typeProduitRepository;


    public TypeProduitService(TypeProduitRepository typeProduitRepository) {
        this.typeProduitRepository = typeProduitRepository;
    }


    public List<TypeProduit> findAll() {
        return typeProduitRepository.findAll();
    }


    public Optional<TypeProduit> findById(Long id) {
        return typeProduitRepository.findById(id);
    }


    public TypeProduit save(TypeProduit typeProduit) {
        return typeProduitRepository.save(typeProduit);
    }


    public void deleteById(Long id) {
        typeProduitRepository.deleteById(id);
    }
}