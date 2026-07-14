package com.entrepot.gestion.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contrats")
public class ContratController {

    private final JdbcTemplate jdbcTemplate;

    public ContratController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/create")
    public String create(
            @RequestParam(required = false) Long clientId,
            Model model) {

        List<Map<String, Object>> clients = jdbcTemplate.queryForList("""
                SELECT u.id, u.email
                FROM utilisateurs u
                JOIN roles r ON r.id = u.role_id
                WHERE r.code = 'CLIENT' AND u.actif = true
                ORDER BY u.email
                """);

        List<Map<String, Object>> typesContrat = jdbcTemplate.queryForList("""
                SELECT id, code, libelle
                FROM types_contrat
                ORDER BY libelle
                """);

        model.addAttribute("clients", clients);
        model.addAttribute("typesContrat", typesContrat);

        if (clientId != null) {
            List<Map<String, Object>> selected = jdbcTemplate.queryForList("""
                    SELECT u.id, u.email
                    FROM utilisateurs u
                    WHERE u.id = ?
                    """, clientId);
            if (!selected.isEmpty()) {
                model.addAttribute("clientSelectionne", selected.get(0));
            }
        }

        return "contrats/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam Long utilisateurId,
            @RequestParam Long typeContratId,
            @RequestParam Long demandeStockageId,
            @RequestParam LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {

        try {
            jdbcTemplate.update("""
                    INSERT INTO contrats (demande_stockage_id, utilisateur_id, type_contrat_id, date_debut, date_fin, description)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, demandeStockageId, utilisateurId, typeContratId, dateDebut, dateFin, description);

            redirectAttributes.addFlashAttribute("success", "✅ Contrat créé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de créer le contrat: " + e.getMessage());
        }

        return "redirect:/contrats/create";
    }

    @GetMapping("/demande")
    public String afficherDemandeStockage(Model model) {
        List<Map<String, Object>> typesZone = jdbcTemplate.queryForList("""
                SELECT id, code, libelle
                FROM types_zone
                ORDER BY libelle
                """);

        List<Map<String, Object>> typesContrat = jdbcTemplate.queryForList("""
                SELECT id, code, libelle
                FROM types_contrat
                ORDER BY libelle
                """);

        model.addAttribute("typesZone", typesZone);
        model.addAttribute("typesContrat", typesContrat);
        return "contrats/demande";
    }

    @PostMapping("/demande")
    public String enregistrerDemande(
            @RequestParam Long typeZoneId,
            @RequestParam Long typeContratId,
            @RequestParam Double volumeEspaceM3,
            @RequestParam LocalDate dateDebut,
            @RequestParam(required = false) LocalDate dateFin,
            RedirectAttributes redirectAttributes) {

        try {
            Long utilisateurId = resolveCurrentUserId();

            Number newId = jdbcTemplate.queryForObject("""
                    INSERT INTO demandes_stockage (utilisateur_id, type_zone_id, type_contrat_id, volume_espace_m3, date_debut, date_fin)
                    VALUES (?, ?, ?, ?, ?, ?)
                    RETURNING id
                    """, Number.class, utilisateurId, typeZoneId, typeContratId, volumeEspaceM3, dateDebut, dateFin);

            if (newId != null) {
                insertDemandeStatus(newId.longValue(), "EN_ATTENTE");
            }

            redirectAttributes.addFlashAttribute("success", "✅ Demande de stockage enregistrée et mise en attente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible d'enregistrer la demande de stockage: " + e.getMessage());
        }

        return "redirect:/contrats/demande";
    }

    @GetMapping("/demandes")
    public String listeDemandes(Model model) {
        List<Map<String, Object>> demandes = jdbcTemplate.queryForList("""
                SELECT ds.id,
                       u.email AS client_email,
                       tz.libelle AS type_zone_libelle,
                       tc.libelle AS type_contrat_libelle,
                       ds.volume_espace_m3,
                       ds.date_debut,
                       ds.date_fin,
                       to_char(ds.date_debut, 'DD/MM/YYYY') AS date_debut_fmt,
                       CASE WHEN ds.date_fin IS NULL THEN '-' ELSE to_char(ds.date_fin, 'DD/MM/YYYY') END AS date_fin_fmt,
                       COALESCE(s.code, 'EN_ATTENTE') AS statut_code,
                       COALESCE(s.libelle, 'En attente') AS statut_libelle
                FROM demandes_stockage ds
                JOIN utilisateurs u ON u.id = ds.utilisateur_id
                JOIN types_zone tz ON tz.id = ds.type_zone_id
                JOIN types_contrat tc ON tc.id = ds.type_contrat_id
                LEFT JOIN LATERAL (
                    SELECT hed.statut_id
                    FROM historique_etat_demande hed
                    WHERE hed.demande_stockage_id = ds.id
                    ORDER BY hed.date_statut DESC, hed.id DESC
                    LIMIT 1
                ) last_status ON true
                LEFT JOIN statuts_demande_stockage s ON s.id = last_status.statut_id
                WHERE COALESCE(s.code, 'EN_ATTENTE') = 'EN_ATTENTE'
                ORDER BY ds.id DESC
                """);

        model.addAttribute("demandes", demandes);
        return "contrats/demandes";
    }

    @GetMapping("/nouvelle-demande")
    public String nouvelleDemande() {
        return "redirect:/contrats/demande";
    }

    @GetMapping("/demande/accepter/{id}")
    public String accepter(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateDemandeStatus(id, "ACCEPTEE", redirectAttributes, "/contrats/demandes");
    }

    @GetMapping("/demande/refuser/{id}")
    public String refuser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateDemandeStatus(id, "REFUSEE", redirectAttributes, "/contrats/demandes");
    }

    @GetMapping("/renouvellement")
    public String afficherDemandeRenouvellement(Model model) {
        List<Map<String, Object>> contrats = jdbcTemplate.queryForList("""
                SELECT c.id,
                       u.email AS client_email,
                       tz.libelle AS type_zone_libelle,
                       c.date_debut,
                       c.date_fin,
                       to_char(c.date_debut, 'DD/MM/YYYY') AS date_debut_fmt,
                       CASE WHEN c.date_fin IS NULL THEN '-' ELSE to_char(c.date_fin, 'DD/MM/YYYY') END AS date_fin_fmt
                FROM contrats c
                JOIN utilisateurs u ON u.id = c.utilisateur_id
                JOIN demandes_stockage ds ON ds.id = c.demande_stockage_id
                JOIN types_zone tz ON tz.id = ds.type_zone_id
                ORDER BY c.id DESC
                """);

        model.addAttribute("contrats", contrats);
        return "contrats/renouvellement";
    }

    @PostMapping("/renouvellement")
    public String enregistrerDemandeRenouvellement(
            @RequestParam Long contratId,
            @RequestParam LocalDate dateFin,
            RedirectAttributes redirectAttributes) {
        try {
            Number newId = jdbcTemplate.queryForObject("""
                    INSERT INTO demandes_renouvellement (contrat_id, date_debut, date_fin)
                    VALUES (?, CURRENT_DATE, ?)
                    RETURNING id
                    """, Number.class, contratId, dateFin);

            if (newId != null) {
                insertRenouvellementStatus(newId.longValue(), "EN_ATTENTE");
            }

            redirectAttributes.addFlashAttribute("success", "✅ Demande de renouvellement enregistrée et mise en attente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible d'enregistrer la demande de renouvellement: " + e.getMessage());
        }
        return "redirect:/contrats/renouvellement";
    }

    @GetMapping("/renouvellements")
    public String listeDemandesRenouvellement(Model model) {
        List<Map<String, Object>> demandes = jdbcTemplate.queryForList("""
                SELECT dr.id,
                       dr.contrat_id,
                       u.email AS client_email,
                       dr.date_debut AS date_demande,
                       c.date_fin AS ancienne_date_fin,
                       dr.date_fin AS nouvelle_date_fin,
                       to_char(dr.date_debut, 'DD/MM/YYYY') AS date_demande_fmt,
                       CASE WHEN c.date_fin IS NULL THEN '-' ELSE to_char(c.date_fin, 'DD/MM/YYYY') END AS ancienne_date_fin_fmt,
                       CASE WHEN dr.date_fin IS NULL THEN '-' ELSE to_char(dr.date_fin, 'DD/MM/YYYY') END AS nouvelle_date_fin_fmt,
                       COALESCE(sr.code, 'EN_ATTENTE') AS statut_code,
                       COALESCE(sr.libelle, 'En attente') AS statut_libelle
                FROM demandes_renouvellement dr
                JOIN contrats c ON c.id = dr.contrat_id
                JOIN utilisateurs u ON u.id = c.utilisateur_id
                LEFT JOIN LATERAL (
                    SELECT hr.statut_renouvellement_id
                    FROM historique_renouvellement hr
                    WHERE hr.demande_renouvellement_id = dr.id
                    ORDER BY hr.date_statut DESC, hr.id DESC
                    LIMIT 1
                ) last_status ON true
                LEFT JOIN statuts_renouvellement sr ON sr.id = last_status.statut_renouvellement_id
                WHERE COALESCE(sr.code, 'EN_ATTENTE') = 'EN_ATTENTE'
                ORDER BY dr.id DESC
                """);

        model.addAttribute("demandes", demandes);
        return "contrats/renouvellements";
    }

    @GetMapping("/renouvellement/accepter/{id}")
    public String accepterRenouvellement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateRenouvellementStatus(id, "ACCEPTEE", redirectAttributes, "/contrats/renouvellements");
    }

    @GetMapping("/renouvellement/refuser/{id}")
    public String refuserRenouvellement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateRenouvellementStatus(id, "REFUSEE", redirectAttributes, "/contrats/renouvellements");
    }

    private Long resolveCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            List<Long> ids = jdbcTemplate.query("SELECT id FROM utilisateurs WHERE email = ?", (rs, rowNum) -> rs.getLong("id"), authentication.getName());
            if (!ids.isEmpty()) {
                return ids.get(0);
            }
        }

        List<Long> clientIds = jdbcTemplate.query("""
                SELECT u.id
                FROM utilisateurs u
                JOIN roles r ON r.id = u.role_id
                WHERE r.code = 'CLIENT'
                ORDER BY u.id
                LIMIT 1
                """, (rs, rowNum) -> rs.getLong("id"));

        if (!clientIds.isEmpty()) {
            return clientIds.get(0);
        }

        throw new IllegalStateException("Aucun utilisateur client disponible pour créer une demande");
    }

    private void insertDemandeStatus(Long demandeId, String codeStatut) {
        jdbcTemplate.update("""
                INSERT INTO historique_etat_demande (demande_stockage_id, statut_id, date_statut)
                VALUES (?, (SELECT id FROM statuts_demande_stockage WHERE code = ?), now())
                """, demandeId, codeStatut);
    }

    private void insertRenouvellementStatus(Long demandeRenouvellementId, String codeStatut) {
        jdbcTemplate.update("""
                INSERT INTO historique_renouvellement (demande_renouvellement_id, statut_renouvellement_id, date_statut)
                VALUES (?, (SELECT id FROM statuts_renouvellement WHERE code = ?), now())
                """, demandeRenouvellementId, codeStatut);
    }

    private String updateDemandeStatus(Long demandeId, String statusCode, RedirectAttributes redirectAttributes, String redirectPath) {
        try {
            insertDemandeStatus(demandeId, statusCode);
            String libelle = "ACCEPTEE".equals(statusCode) ? "acceptée" : "refusée";
            redirectAttributes.addFlashAttribute("success", "✅ Demande de stockage " + libelle + " avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de mettre à jour la demande de stockage: " + e.getMessage());
        }
        return "redirect:" + redirectPath;
    }

    private String updateRenouvellementStatus(Long demandeId, String statusCode, RedirectAttributes redirectAttributes, String redirectPath) {
        try {
            insertRenouvellementStatus(demandeId, statusCode);
            String libelle = "ACCEPTEE".equals(statusCode) ? "accepté" : "refusé";
            redirectAttributes.addFlashAttribute("success", "✅ Renouvellement " + libelle + " avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de mettre à jour le renouvellement: " + e.getMessage());
        }
        return "redirect:" + redirectPath;
    }
}
