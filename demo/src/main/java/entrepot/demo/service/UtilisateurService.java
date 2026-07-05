package entrepot.demo.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entrepot.demo.model.Role;
import entrepot.demo.model.Utilisateur;
import entrepot.demo.model.UtilisateurInfo;
import entrepot.demo.repository.RoleRepository;
import entrepot.demo.repository.UtilisateurInfoRepository;
import entrepot.demo.repository.UtilisateurRepository;

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
    public String creerClient(Utilisateur utilisateur, UtilisateurInfo utilisateurInfo) {

        // Vérification de l'email
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        // Récupération du rôle CLIENT
        Role role = roleRepository.findByCode("CLIENT")
                .orElseThrow(() -> new RuntimeException("Le rôle CLIENT n'existe pas."));

        // Génération du mot de passe
        String motDePasse = genererMotDePasse(10);

        // Initialisation de l'utilisateur
        utilisateur.setRole(role);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateur.setMotDePasseHash(passwordEncoder.encode(motDePasse));

        // Sauvegarde utilisateur
        utilisateur = utilisateurRepository.save(utilisateur);

        // Association des informations
        utilisateurInfo.setUtilisateur(utilisateur);

        // Sauvegarde informations
        utilisateurInfoRepository.save(utilisateurInfo);

        // Retour du mot de passe en clair
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

        utilisateurRepository.deleteById(info.getUtilisateur().getId());
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
}