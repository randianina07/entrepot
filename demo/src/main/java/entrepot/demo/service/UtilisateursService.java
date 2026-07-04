package entrepot.demo.service;


import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import entrepot.demo.repository.*;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UtilisateursService {
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurInfoRepository utilisateurInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final static String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    public UtilisateursService(UtilisateurRepository utilisateurRepository,
            UtilisateurInfoRepository utilisateurInfoRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurInfoRepository = utilisateurInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
