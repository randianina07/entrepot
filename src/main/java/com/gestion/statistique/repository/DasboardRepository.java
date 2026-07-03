package com.gestion.statistique.repository;
import com.gestion.statistique.model.DasboardKpiDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository 
public class DasboardRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DasboardRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DasboardKpiDto getKpis() {
        String sql =  "SELECT taux_occupation_pct, occ_variation_pts, nb_mouvements_mois, " +
                    "mvt_variation_pct, taux_ponctualite_pct, nb_a_temps, nb_retard, " +
                    "resultat_net, total_recettes, total_depenses, rn_variation_pct " +
                    "FROM v_dashboard_kpis";
        return jdbcTemplate.queryForObject(sql, Map.of(), new BeanPropertyRowMapper<>(DasboardKpiDto.class));
    }

}
