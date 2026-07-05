package com.gestion.statistique.repository;
import com.gestion.statistique.model.FinanceMensuelleDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class FinanceRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FinanceRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FinanceMensuelleDto> getFinance6mois() {
        String sql ="SELECT mois, mois_libelle, total_recettes, total_depenses, " +
                    "resultat_net, taux_charge_pct FROM v_stat_finance_6m ORDER BY mois";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FinanceMensuelleDto.class));
    }

    public List<FinanceMensuelleDto> getFinanceHistorique() {
        String sql ="SELECT mois, mois_libelle, total_recettes, total_depenses, " +
                    "resultat_net, taux_charge_pct FROM v_stat_finance_mensuelle ORDER BY mois";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FinanceMensuelleDto.class));
    }

    public List<FinanceMensuelleDto> getFinanceByPeriod(LocalDate from, LocalDate to) {
        String sql ="SELECT mois, mois_libelle, total_recettes, total_depenses, " +
                    "resultat_net, taux_charge_pct FROM v_stat_finance_mensuelle " +
                    "WHERE mois BETWEEN :from AND :to ORDER BY mois";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(FinanceMensuelleDto.class));
    }
}
