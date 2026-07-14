package com.entrepot.gestion.security;

import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.repository.UtilisateurRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entrepot.gestion.model.AuthDetails;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            throw new DisabledException("Le compte est désactivé: " + email);
        }

        String roleCode = utilisateur.getRole().getCode();

        return new AuthDetails(utilisateur);
    }
}
