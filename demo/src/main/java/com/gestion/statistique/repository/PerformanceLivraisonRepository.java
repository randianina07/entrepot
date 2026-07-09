package com.gestion.statistique.repository;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.gestion.statistique.model.PerformanceLivraisonDto;

@Repository
public class PerformanceLivraisonRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PerformanceLivraisonRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PerformanceLivraisonDto getPerformanceLivraison() {
        String sql = "SELECT nb_total, nb_livrees, nb_a_temps, nb_retard, " +
                    "nb_en_attente, taux_ponctualite_pct, retard_moyen_heures " +
                    "FROM v_stat_performance_livraison";
        try {
            return jdbcTemplate.queryForObject(sql, Map.of(), new BeanPropertyRowMapper<>(PerformanceLivraisonDto.class));
        } catch (EmptyResultDataAccessException ex) {
            return new PerformanceLivraisonDto();
        }
    }

    public PerformanceLivraisonDto getPerformanceLivraisonByPeriod(LocalDate from, LocalDate to) {
        String sql =  "SELECT COUNT(*) AS nb_total, " +
                "  COUNT(*) FILTER (WHERE l.date_livraison IS NOT NULL) AS nb_livrees, " +
                "  COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue) AS nb_a_temps, " +
                "  COUNT(*) FILTER (WHERE l.date_livraison > l.date_prevue) AS nb_retard, " +
                "  COUNT(*) FILTER (WHERE l.date_livraison IS NULL) AS nb_en_attente, " +
                "  ROUND(COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue)::NUMERIC " +
                "    / NULLIF(COUNT(*) FILTER (WHERE l.date_livraison IS NOT NULL),0)*100,1) AS taux_ponctualite_pct " +
                "FROM livraisons l " +
                "JOIN missions_logistiques ml ON ml.id = l.mission_id " +
                "JOIN statuts_mission sm ON sm.id = ml.statut_mission_id AND sm.code = 'TERMINEE' " +
                "WHERE l.date_prevue IS NOT NULL " +
                "  AND l.date_livraison::DATE BETWEEN :from AND :to";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("from", from);
        params.addValue("to", to);
        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(PerformanceLivraisonDto.class));
        } catch (EmptyResultDataAccessException ex) {
            return new PerformanceLivraisonDto();
        }
    }
}
