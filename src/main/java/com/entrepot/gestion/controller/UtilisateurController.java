package com.entrepot.gestion.controller;

import com.entrepot.gestion.model.Role;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.repository.RoleRepository;
import com.entrepot.gestion.repository.UtilisateurInfoRepository;
import com.entrepot.gestion.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurInfoRepository utilisateurInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurController(UtilisateurRepository utilisateurRepository,
                                 UtilisateurInfoRepository utilisateurInfoRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurInfoRepository = utilisateurInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/utilisateurs/nouveau")
    public String afficherFormulaire(Model model) {
        model.addAttribute("utilisateur", new Object());
        model.addAttribute("utilisateurInfo", new Object());
        model.addAttribute("erreur", null);
        model.addAttribute("roles", rolesInscriptionClient());
        return "utilisateur/formulaire";
    }

    @PostMapping("/utilisateurs/enregistrer")
    public String enregistrerClient(
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String secteur,
            @RequestParam Long roleId,
            Model model) {
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurRepository.existsByEmail(email)) {
                model.addAttribute("erreur", "Cet email est déjà utilisé.");
                model.addAttribute("roles", rolesInscriptionClient());
                return "utilisateur/formulaire";
            }

            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

            if (!"CLIENT".equals(role.getCode())) {
                model.addAttribute("erreur", "Seule l'inscription client est autorisée depuis cette page.");
                model.addAttribute("roles", rolesInscriptionClient());
                return "utilisateur/formulaire";
            }

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setMotDePasseHash(passwordEncoder.encode(motDePasse));
            utilisateur.setRole(role);
            utilisateur.setActif(true);
            utilisateur.setDateCreation(LocalDateTime.now());
            
            utilisateur = utilisateurRepository.save(utilisateur);

            // Créer les infos utilisateur
            com.entrepot.gestion.model.UtilisateurInfo utilisateurInfo = 
                new com.entrepot.gestion.model.UtilisateurInfo();
            utilisateurInfo.setUtilisateur(utilisateur);
            utilisateurInfo.setNom(nom);
            utilisateurInfo.setPrenom(prenom);
            utilisateurInfo.setNumero(numero);
            utilisateurInfo.setAdresse(adresse);
            utilisateurInfo.setSecteur(secteur);
            
            utilisateurInfoRepository.save(utilisateurInfo);

            model.addAttribute("message", "L'utilisateur a été créé avec succès.");
            model.addAttribute("email", email);
            model.addAttribute("role", role.getCode());
            
            return "utilisateur/succes";
        } catch (Exception e) {
            model.addAttribute("erreur", "Erreur lors de la création: " + e.getMessage());
            model.addAttribute("roles", rolesInscriptionClient());
            return "utilisateur/formulaire";
        }
    }

    private List<Role> rolesInscriptionClient() {
        return roleRepository.findByCode("CLIENT")
                .map(List::of)
                .orElseGet(List::of);
    }

    @GetMapping("/clients")
    public String listeClients(Model model) {
        // TODO: Intégrer les vraies données quand le service sera disponible
        model.addAttribute("clients", Collections.emptyList());
        return "client/liste";
    }

    @GetMapping("/clients/nouveau")
    public String nouveauClient() {
        return "redirect:/utilisateurs/nouveau";
    }

    @GetMapping("/clients/supprimer/{id}")
    public String supprimerClient(@PathVariable Long id) {
        // TODO: Supprimer le client avec le service
        return "redirect:/clients";
    }

    @GetMapping("/clients/modifier/{id}")
    public String afficherModification(@PathVariable Long id, Model model) {
        // TODO: Charger le client à modifier
        model.addAttribute("client", new Object());
        return "client/modifier";
    }

    @PostMapping("/clients/modifier/{id}")
    public String modifierClient(
            @PathVariable Long id,
            @ModelAttribute("client") Object client) {
        // TODO: Modifier le client avec le service
        return "redirect:/clients";
    }
}
