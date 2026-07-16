package com.entrepot.gestion.config;

import com.entrepot.gestion.model.Role;
import com.entrepot.gestion.model.Utilisateur;
import com.entrepot.gestion.model.UtilisateurInfo;
import com.entrepot.gestion.repository.RoleRepository;
import com.entrepot.gestion.repository.UtilisateurInfoRepository;
import com.entrepot.gestion.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@entrepot.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private final RoleRepository roleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurInfoRepository utilisateurInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                          UtilisateurRepository utilisateurRepository,
                          UtilisateurInfoRepository utilisateurInfoRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurInfoRepository = utilisateurInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        ensureRolesExist();
        ensureAdminUser();
    }

    private void ensureRolesExist() {
        if (roleRepository.count() > 0) {
            return;
        }

        createRole("ADMIN", "Administrateur");
        createRole("GESTIONNAIRE", "Gestionnaire d'entrepot");
        createRole("RESPONSABLE_LOGISTIQUE", "Responsable logistique");
        createRole("COMPTABLE", "Comptable");
        createRole("CLIENT", "Client");
    }

    private void createRole(String code, String libelle) {
        Role role = new Role();
        role.setCode(code);
        role.setLibelle(libelle);
        roleRepository.save(role);
    }

    private void ensureAdminUser() {
        Utilisateur admin = utilisateurRepository.findByEmail(ADMIN_EMAIL).orElse(null);
        if (admin == null) {
            createAdminUser();
            return;
        }

        boolean needsUpdate = false;
        if (!isBcryptHash(admin.getMotDePasseHash())
                || !passwordEncoder.matches(ADMIN_PASSWORD, admin.getMotDePasseHash())) {
            admin.setMotDePasseHash(passwordEncoder.encode(ADMIN_PASSWORD));
            needsUpdate = true;
        }
        if (!Boolean.TRUE.equals(admin.getActif())) {
            admin.setActif(true);
            needsUpdate = true;
        }
        if (needsUpdate) {
            utilisateurRepository.save(admin);
        }
    }

    private void createAdminUser() {
        Role adminRole = roleRepository.findByCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));

        Utilisateur admin = new Utilisateur();
        admin.setEmail(ADMIN_EMAIL);
        admin.setMotDePasseHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(adminRole);
        admin.setActif(true);
        admin.setDateCreation(LocalDateTime.now());
        admin = utilisateurRepository.save(admin);

        if (!utilisateurInfoRepository.existsByUtilisateurId(admin.getId())) {
            UtilisateurInfo adminInfo = new UtilisateurInfo();
            adminInfo.setUtilisateur(admin);
            adminInfo.setNom("Admin");
            adminInfo.setPrenom("Système");
            adminInfo.setNumero("0000000000");
            adminInfo.setAdresse("Antananarivo");
            adminInfo.setSecteur("IT");
            utilisateurInfoRepository.save(adminInfo);
        }
    }

    private boolean isBcryptHash(String hash) {
        return hash != null && (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
    }
}
