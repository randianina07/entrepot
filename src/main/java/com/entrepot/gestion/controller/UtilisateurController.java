package com.entrepot.gestion.controller;

import com.entrepot.gestion.model.Role;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.model.UtilisateurInfo;
import com.entrepot.gestion.repository.RoleRepository;
import com.entrepot.gestion.repository.UtilisateurInfoRepository;
import com.entrepot.gestion.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
                model.addAttribute("erreur", "❌ Cet email est déjà utilisé.");
                model.addAttribute("roles", rolesInscriptionClient());
                return "utilisateur/formulaire";
            }

            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

            if (!"CLIENT".equals(role.getCode())) {
                model.addAttribute("erreur", "❌ Seule l'inscription client est autorisée depuis cette page.");
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

            model.addAttribute("message", "✅ Votre compte client a été créé avec succès.");
            model.addAttribute("email", email);
            model.addAttribute("role", role.getCode());
            
            return "utilisateur/succes";
        } catch (Exception e) {
            model.addAttribute("erreur", "❌ Impossible de créer le compte: " + e.getMessage());
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
        model.addAttribute("clients", utilisateurInfoRepository.findByUtilisateur_Role_Code("CLIENT"));
        return "client/liste";
    }

    @GetMapping("/clients/nouveau")
    public String nouveauClient() {
        return "redirect:/utilisateurs/nouveau";
    }

    @GetMapping("/clients/supprimer/{id}")
    public String supprimerClient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            UtilisateurInfo info = utilisateurInfoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + id));
            Long utilisateurId = info.getUtilisateur().getId();
            utilisateurInfoRepository.deleteById(id);
            utilisateurRepository.deleteById(utilisateurId);
            redirectAttributes.addFlashAttribute("success", "✅ Client supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de supprimer le client: " + e.getMessage());
        }
        return "redirect:/clients";
    }

    @GetMapping("/clients/modifier/{id}")
    public String afficherModification(@PathVariable Long id, Model model) {
        UtilisateurInfo client = utilisateurInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + id));
        model.addAttribute("client", client);
        return "client/modifier";
    }

    @PostMapping("/clients/modifier/{id}")
    public String modifierClient(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String secteur,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        try {
            UtilisateurInfo client = utilisateurInfoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Client non trouvé: " + id));

            client.setNom(nom);
            client.setPrenom(prenom);
            client.setNumero(numero);
            client.setAdresse(adresse);
            client.setSecteur(secteur);
            utilisateurInfoRepository.save(client);

            Utilisateur utilisateur = client.getUtilisateur();
            if (!email.equals(utilisateur.getEmail())) {
                utilisateur.setEmail(email);
                utilisateurRepository.save(utilisateur);
            }

            redirectAttributes.addFlashAttribute("success", "✅ Client modifié avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de modifier le client: " + e.getMessage());
        }
        return "redirect:/clients";
    }
}
