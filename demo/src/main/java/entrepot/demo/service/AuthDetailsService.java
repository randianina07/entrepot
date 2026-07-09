package entrepot.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import entrepot.demo.model.AuthDetails;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.repository.UtilisateurRepository;

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
