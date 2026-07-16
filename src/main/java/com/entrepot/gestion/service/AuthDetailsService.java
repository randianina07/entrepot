package com.entrepot.gestion.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.AuthDetails;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.repository.UtilisateurRepository;

@Service
public class AuthDetailsService implements UserDetailsService{

    private final UtilisateurRepository utilisateurRepository;

    public AuthDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utilisateur introuvable"));

        return new AuthDetails(utilisateur);
    }
}
