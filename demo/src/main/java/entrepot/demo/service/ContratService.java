package entrepot.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entrepot.demo.model.Contrat;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.repository.ContratRepository;

@Service
public class ContratService {

    private final ContratRepository contratRepository;

    public ContratService(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    public Optional<Contrat> findById(Long id) {
        return contratRepository.findById(id);
    }

    public List<Contrat> findByUtilisateur(Utilisateur utilisateur) {
        return contratRepository.findByUtilisateur(utilisateur);
    }

    public Contrat save(Contrat contrat) {
        return contratRepository.save(contrat);
    }

    public void deleteById(Long id) {
        contratRepository.deleteById(id);
    }
}
