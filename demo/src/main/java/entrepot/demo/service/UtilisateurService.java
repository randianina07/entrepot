package entrepot.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entrepot.demo.model.Utilisateur;
import entrepot.demo.repositories.UtilisateurRepository;

@Service
public class UtilisateurService {
    
    @Autowired
    UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> getAllclient() {

        return utilisateurRepository.findAllById_role();

    }

}
