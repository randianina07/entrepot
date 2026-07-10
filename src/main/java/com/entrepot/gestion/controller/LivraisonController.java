package com.entrepot.gestion.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/livraisons")
public class LivraisonController {

    private final JdbcTemplate jdbcTemplate;

    public LivraisonController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/livraison")
    public String listeLivraisons(Model model) {
        List<Map<String, Object>> listeLivraison = jdbcTemplate.queryForList("""
                SELECT l.id,
                       u.email AS client_email,
                       l.adresse_livraison,
                       zl.libelle AS zone_libelle,
                       zl.commune AS zone_commune,
                       zl.tarif_base AS zone_tarif_base,
                       l.poids_total,
                       l.volume_total,
                       l.date_prevue,
                       l.date_livraison,
                       to_char(l.date_prevue, 'DD/MM/YYYY HH24:MI') AS date_prevue_fmt,
                       CASE WHEN l.date_livraison IS NULL THEN '-' ELSE to_char(l.date_livraison, 'DD/MM/YYYY HH24:MI') END AS date_livraison_fmt,
                       l.montant_livraison
                FROM livraisons l
                JOIN utilisateurs u ON u.id = l.client_id
                LEFT JOIN zones_livraison zl ON zl.id = l.zone_livraison_id
                ORDER BY l.id DESC
                """);

        model.addAttribute("listeLivraison", listeLivraison);
        return "livraisons/livraison";
    }

    @GetMapping("/config_livraison")
    public String configLivraison(@RequestParam Long id, Model model, RedirectAttributes redirectAttributes) {
        List<Map<String, Object>> livraisons = jdbcTemplate.queryForList("""
                SELECT l.id,
                       u.email AS client_email,
                       l.adresse_livraison,
                       l.zone_livraison_id,
                       zl.libelle AS zone_libelle,
                       zl.commune AS zone_commune,
                       zl.tarif_base AS zone_tarif_base,
                       l.poids_total,
                       l.volume_total,
                       l.date_prevue,
                       l.date_livraison,
                       to_char(l.date_prevue, 'DD/MM/YYYY HH24:MI') AS date_prevue_fmt,
                       CASE WHEN l.date_livraison IS NULL THEN '-' ELSE to_char(l.date_livraison, 'DD/MM/YYYY HH24:MI') END AS date_livraison_fmt,
                       l.montant_livraison
                FROM livraisons l
                JOIN utilisateurs u ON u.id = l.client_id
                LEFT JOIN zones_livraison zl ON zl.id = l.zone_livraison_id
                WHERE l.id = ?
                """, id);

        if (livraisons.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "❌ Livraison introuvable.");
            return "redirect:/livraisons/livraison";
        }

        Map<String, Object> livraison = livraisons.get(0);

        List<Map<String, Object>> tarifs = jdbcTemplate.queryForList("""
                SELECT t.id,
                       t.zone_livraison_id,
                       t.prix_base,
                       t.prix_par_kg,
                       t.prix_par_m3,
                       m.libelle AS mode_libelle
                FROM tarifs_livraison t
                LEFT JOIN modes_calcul_livraison m ON m.id = t.mode_calcul_id
                WHERE t.zone_livraison_id = ?
                  AND (t.date_fin_validite IS NULL OR t.date_fin_validite >= CURRENT_DATE)
                ORDER BY t.id DESC
                """, livraison.get("zone_livraison_id"));

        model.addAttribute("livraison", livraison);
        model.addAttribute("tarif_livraison", tarifs);
        model.addAttribute("mode_calcule", List.of());

        return "livraisons/config_livraison";
    }

    @PostMapping("/config_livraison")
    public String saveConfigLivraison(
            @RequestParam Long livraisonId,
            @RequestParam Long tarifId,
            @RequestParam String datePrevue,
            @RequestParam String heurePrevue,
            RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> livraison = jdbcTemplate.queryForMap("""
                    SELECT l.poids_total, l.volume_total, zl.tarif_base AS zone_tarif_base
                    FROM livraisons l
                    LEFT JOIN zones_livraison zl ON zl.id = l.zone_livraison_id
                    WHERE l.id = ?
                    """, livraisonId);

            Map<String, Object> tarif = jdbcTemplate.queryForMap("""
                    SELECT prix_base, prix_par_kg, prix_par_m3
                    FROM tarifs_livraison
                    WHERE id = ?
                    """, tarifId);

            BigDecimal poids = asBigDecimal(livraison.get("poids_total"));
            BigDecimal volume = asBigDecimal(livraison.get("volume_total"));
            BigDecimal zoneBase = asBigDecimal(livraison.get("zone_tarif_base"));

            BigDecimal prixBase = asBigDecimal(tarif.get("prix_base"));
            BigDecimal prixKg = asBigDecimal(tarif.get("prix_par_kg"));
            BigDecimal prixM3 = asBigDecimal(tarif.get("prix_par_m3"));

            BigDecimal montant = prixBase
                    .add(zoneBase)
                    .add(poids.multiply(prixKg))
                    .add(volume.multiply(prixM3));

            LocalDateTime dateHeurePrevue = LocalDate.parse(datePrevue).atTime(LocalTime.parse(heurePrevue));

            jdbcTemplate.update("""
                    UPDATE livraisons
                    SET date_prevue = ?, montant_livraison = ?
                    WHERE id = ?
                    """, dateHeurePrevue, montant, livraisonId);

            redirectAttributes.addFlashAttribute("success", "✅ Livraison configurée avec succès (date prévue et montant mis à jour).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de configurer la livraison: " + e.getMessage());
        }

        return "redirect:/livraisons/livraison";
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal b) {
            return b;
        }
        if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return new BigDecimal(value.toString());
    }
}
