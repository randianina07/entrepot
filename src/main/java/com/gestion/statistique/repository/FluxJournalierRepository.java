package com.gestion.statistique.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.gestion.statistique.model.FluxJournalierDto;

@Repository
public class FluxJournalierRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FluxJournalierRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FluxJournalierDto> getFluxJournalier7jours(){
        String sql = "SELECT jour, volume_entrees_m3, volume_sorties_m3, " +
                "nb_entrees, nb_sorties, qte_entrees, qte_sorties " +
                "FROM v_stat_flux_7j ORDER BY jour";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FluxJournalierDto.class));
    }

    public List<FluxJournalierDto> getFluxJournalierHistorique(){
        String sql = "SELECT jour, volume_entrees_m3, volume_sorties_m3, " +
                "nb_entrees, nb_sorties, qte_entrees, qte_sorties " +
                "FROM v_stat_flux_journalier_pivot ORDER BY jour";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FluxJournalierDto.class));
    }

    public List<FluxJournalierDto> getFluxByPeriod(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT jour, volume_entrees_m3, volume_sorties_m3, " +
                    "nb_entrees, nb_sorties, qte_entrees, qte_sorties " +
                    "FROM v_stat_flux_journalier_pivot " +
                    "WHERE jour BETWEEN :from AND :to ORDER BY jour";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", startDate)
                .addValue("to", endDate);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(FluxJournalierDto.class));
    }
}
