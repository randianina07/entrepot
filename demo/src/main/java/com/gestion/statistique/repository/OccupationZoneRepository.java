package com.gestion.statistique.repository;
import com.gestion.statistique.model.OccupationZoneDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class OccupationZoneRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OccupationZoneRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OccupationZoneDto> getOccupationZones() {
        String sql ="SELECT zone_id, zone_code, zone_libelle, type_zone_code, " +
                 "capacite_m3, volume_occupe_m3, volume_libre_m3, " +
                 "taux_occupation_pct, statut_couleur " +
                 "FROM v_stat_occupation_zones";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OccupationZoneDto.class));
    }

    public List<OccupationZoneDto> getOccupationZonesByPeriod(LocalDate from , LocalDate to) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);
        String sql =  "SELECT zone_id, zone_code, zone_libelle, type_zone_code, " +
                    "capacite_totale_m3 AS capacite_m3, capacite_occupee_m3 AS volume_occupe_m3, " +
                     "taux_occupation_pct, statut_couleur " +
                    "FROM v_stat_occupation_zones_historique " +
                    "WHERE date_snapshot BETWEEN :from AND :to " +
                    "ORDER BY date_snapshot DESC, taux_occupation_pct DESC";
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(OccupationZoneDto.class));
    }
}
