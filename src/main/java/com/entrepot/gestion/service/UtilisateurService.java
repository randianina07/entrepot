package com.entrepot.gestion.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.entrepot.gestion.model.AuthDetails;
import com.entrepot.gestion.model.Role;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.model.UtilisateurInfo;
import com.entrepot.gestion.repository.RoleRepository;
import com.entrepot.gestion.repository.UtilisateurInfoRepository;
import com.entrepot.gestion.repository.UtilisateurRepository;

@Service
@Transactional
public class UtilisateurService {

        private final UtilisateurRepository utilisateurRepository;
        private final UtilisateurInfoRepository utilisateurInfoRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789";

        private static final SecureRandom RANDOM = new SecureRandom();

        public UtilisateurService(UtilisateurRepository utilisateurRepository,
                        UtilisateurInfoRepository utilisateurInfoRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {

                this.utilisateurRepository = utilisateurRepository;
                this.utilisateurInfoRepository = utilisateurInfoRepository;
                this.roleRepository = roleRepository;
                this.passwordEncoder = passwordEncoder;
        }

        /**
         * Crée un nouveau client.
         *
         * @param utilisateur     informations du compte (email)
         * @param utilisateurInfo informations personnelles
         * @return le mot de passe généré automatiquement
         */
        public String creerUtilisateur(
                        Utilisateur utilisateur,
                        UtilisateurInfo utilisateurInfo,
                        String roleCode) {

                // Vérification email

                if (utilisateurRepository
                                .findByEmail(utilisateur.getEmail())
                                .isPresent()) {

                        throw new RuntimeException(
                                        "Cet email existe déjà.");
                }

                // Recherche du rôle choisi

                Role role = roleRepository
                                .findByCode(roleCode)
                                .orElseThrow(() -> new RuntimeException(
                                                "Rôle inexistant"));

                // Génération mot de passe

                String motDePasse = genererMotDePasse(10);

                // Configuration utilisateur

                utilisateur.setRole(role);

                utilisateur.setDateCreation(
                                LocalDateTime.now());

                utilisateur.setMotDePasseHash(
                                passwordEncoder.encode(motDePasse));

                // Sauvegarde compte

                utilisateur = utilisateurRepository.save(utilisateur);

                // Association profil

                utilisateurInfo.setUtilisateur(utilisateur);

                utilisateurInfoRepository.save(
                                utilisateurInfo);

                return motDePasse;
        }

        /**
         * Génère un mot de passe aléatoire.
         */
        private String genererMotDePasse(int longueur) {

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < longueur; i++) {
                        sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
                }

                return sb.toString();
        }

        public List<UtilisateurInfo> listeClients() {
                return utilisateurInfoRepository.findAll();
        }

        @Transactional
        public void supprimerClient(Long id) {

                UtilisateurInfo info = utilisateurInfoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Client introuvable"));

                utilisateurInfoRepository.delete(info);
                utilisateurInfoRepository.flush();

                utilisateurRepository.deleteById(info.getUtilisateur().getId());
                utilisateurRepository.flush();
        }

        public UtilisateurInfo trouverClient(Long id) {

                return utilisateurInfoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        }

        @Transactional
        public void modifierClient(Long id, UtilisateurInfo nouveauClient) {

                UtilisateurInfo ancienClient = utilisateurInfoRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Client introuvable"));

                ancienClient.setNom(nouveauClient.getNom());
                ancienClient.setPrenom(nouveauClient.getPrenom());
                ancienClient.setNumero(nouveauClient.getNumero());
                ancienClient.setAdresse(nouveauClient.getAdresse());
                ancienClient.setSecteur(nouveauClient.getSecteur());

                ancienClient.getUtilisateur().setEmail(
                                nouveauClient.getUtilisateur().getEmail());

                utilisateurInfoRepository.save(ancienClient);
        }

        public Utilisateur findById(Long id) {
                return utilisateurRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        }

        public List<Utilisateur> listeClientsUtilisateur() {
                return utilisateurRepository.findByRoleCode("CLIENT");
        }

        /**
         * Retourne l'utilisateur actuellement connecté.
         */
        public Utilisateur getUtilisateurConnecte() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !authentication.isAuthenticated()
                                || authentication.getPrincipal().equals("anonymousUser")) {

                        throw new RuntimeException("Aucun utilisateur connecté");
                }

                AuthDetails details = (AuthDetails) authentication.getPrincipal();

                return details.getUtilisateur();
        }

        /**
         * Retourne le profil de l'utilisateur connecté.
         */
        public UtilisateurInfo getProfil() {

                Utilisateur utilisateur = getUtilisateurConnecte();

                return utilisateurInfoRepository
                                .findByUtilisateurId(utilisateur.getId())
                                .orElseThrow(() -> new RuntimeException("Profil introuvable"));
        }

        public boolean estUtilisateurAdminConnecte() {

                Utilisateur utilisateur = getUtilisateurConnecte();

                return utilisateurRepository.findById(utilisateur.getId())
                                .map(u -> u.getRole() != null
                                                && "ADMIN".equals(u.getRole().getCode()))
                                .orElse(false);
        }

        @Transactional
        public void changerMotDePasse(
                        String ancienMotDePasse,
                        String nouveauMotDePasse,
                        boolean verificationAncien) {

                Utilisateur utilisateur = getUtilisateurConnecte();

                Utilisateur utilisateurEnBase = utilisateurRepository.findById(utilisateur.getId())
                                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

                if (verificationAncien) {

                        if (!passwordEncoder.matches(
                                        ancienMotDePasse,
                                        utilisateurEnBase.getMotDePasseHash())) {

                                throw new BadCredentialsException(
                                                "Ancien mot de passe incorrect");
                        }
                }

                String nouveauHash = passwordEncoder.encode(nouveauMotDePasse);

                utilisateurEnBase.setMotDePasseHash(nouveauHash);

                utilisateurRepository.saveAndFlush(utilisateurEnBase);
        }
}
