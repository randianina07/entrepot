package com.gestion.statistique.repository;
import com.gestion.statistique.model.CaClientsDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CaClientsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CaClientsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CaClientsDto> getCaClients() {
        String sql = "SELECT client_id, nom_client, prenom, email, secteur, " +
                    "nb_contrats_actifs AS nb_contrats, nb_livraisons, " +
                    "ca_stockage, ca_livraison, ca_total, rang_ca " +
                    "FROM v_stat_ca_clients LIMIT 5";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CaClientsDto.class));
    }

    public List<CaClientsDto> getCaClientsByPeriod(LocalDate from, LocalDate to) {
        String sql = "SELECT client_id, nom_client, prenom, email, " +
            "  SUM(montant) FILTER (WHERE type_ca='STOCKAGE')  AS ca_stockage, " +
            "  SUM(montant) FILTER (WHERE type_ca='LIVRAISON') AS ca_livraison, " +
            "  SUM(montant) AS ca_total " +
            "FROM v_stat_ca_clients_par_periode " +
            "WHERE date_facturation BETWEEN :from AND :to " +
            "GROUP BY client_id, nom_client, prenom, email " +
            "ORDER BY ca_total DESC LIMIT 5";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(CaClientsDto.class));
    }
    
}
