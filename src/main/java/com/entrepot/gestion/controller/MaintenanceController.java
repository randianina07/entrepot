package com.entrepot.gestion.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/maintenances")
public class MaintenanceController {

    private final JdbcTemplate jdbcTemplate;

    public MaintenanceController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @GetMapping({"", "/", "/historique", "/historique/"})
    public String historiqueMaintenances(
            @RequestParam(required = false) LocalDate dateDebutMaintenance,
            @RequestParam(required = false) LocalDate dateFinMaintenance,
            @RequestParam(required = false) Long typeMaintenanceId,
            Model model
    ) {
        StringBuilder sql = new StringBuilder("""
                SELECT mv.id,
                       mv.date_maintenance,
                       to_char(mv.date_maintenance, 'DD/MM/YYYY') AS date_maintenance_fmt,
                       mv.kilometrage,
                       mv.cout,
                       mv.description,
                       v.id AS vehicule_id,
                       v.immatriculation,
                       v.marque,
                       v.modele,
                       tm.id AS type_maintenance_id,
                       tm.libelle AS type_maintenance_libelle
                FROM maintenances_vehicule mv
                JOIN vehicules v ON v.id = mv.vehicule_id
                JOIN types_maintenance tm ON tm.id = mv.type_maintenance_id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (dateDebutMaintenance != null) {
            sql.append(" AND mv.date_maintenance >= ?");
            params.add(dateDebutMaintenance);
        }

        if (dateFinMaintenance != null) {
            sql.append(" AND mv.date_maintenance <= ?");
            params.add(dateFinMaintenance);
        }

        if (typeMaintenanceId != null) {
            sql.append(" AND mv.type_maintenance_id = ?");
            params.add(typeMaintenanceId);
        }

        sql.append(" ORDER BY mv.date_maintenance DESC, mv.id DESC");

        List<Map<String, Object>> maintenances = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        List<Map<String, Object>> typesMaintenances = jdbcTemplate.queryForList("""
                SELECT id, code, libelle
                FROM types_maintenance
                ORDER BY libelle
                """);

        model.addAttribute("maintenances", maintenances);
        model.addAttribute("dateDebutMaintenanceSelectionnee", dateDebutMaintenance);
        model.addAttribute("dateFinMaintenanceSelectionnee", dateFinMaintenance);
        model.addAttribute("typeMaintenanceIdSelectionne", typeMaintenanceId);
        model.addAttribute("typesMaintenances", typesMaintenances);

        return "historiqueMaintenances";
    }

    @GetMapping({"/ajouter", "/ajouter/"})
    public String showAddForm(Model model) {
        List<Map<String, Object>> vehicules = jdbcTemplate.queryForList("""
                SELECT id, immatriculation, marque, modele
                FROM vehicules
                ORDER BY immatriculation
                """);

        List<Map<String, Object>> typesMaintenances = jdbcTemplate.queryForList("""
                SELECT id, code, libelle
                FROM types_maintenance
                ORDER BY libelle
                """);

        model.addAttribute("vehicules", vehicules);
        model.addAttribute("typesMaintenances", typesMaintenances);
        return "formulaireAjoutMaintenances";
    }

    @PostMapping({"/ajouter", "/ajouter/"})
    public String addMaintenance(
            @RequestParam Long vehiculeId,
            @RequestParam Long typeMaintenanceId,
            @RequestParam LocalDate dateMaintenance,
            @RequestParam(required = false) BigDecimal kilometrage,
            @RequestParam(required = false) BigDecimal cout,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jdbcTemplate.update("""
                    INSERT INTO maintenances_vehicule (vehicule_id, type_maintenance_id, date_maintenance, kilometrage, cout, description)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, vehiculeId, typeMaintenanceId, dateMaintenance, kilometrage, cout, description);
            redirectAttributes.addFlashAttribute("success", "✅ Maintenance enregistrée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible d'enregistrer la maintenance: " + e.getMessage());
        }

        return "redirect:/maintenances";
    }
}
