package com.gestion.statistique.repository;
import com.gestion.statistique.model.TopProduitsDto;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TopProduitsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TopProduitsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TopProduitsDto> getTopProduits() {
        String sql = "SELECT produit_id, produit_code, produit_nom, type_produit, " +
                        "total_entrees, total_sorties, total_mouvements, stock_actuel, rang " +
                        "FROM v_stat_top5_produits";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopProduitsDto.class));
    }

    public List<TopProduitsDto> getTopProduitsByPeriod(LocalDate from, LocalDate to) {
        String sql = "SELECT produit_id, produit_code, produit_nom, type_produit, " +
            "  SUM(quantite_jour) FILTER (WHERE type_flux='ENTREE') AS total_entrees, " +
            "  SUM(quantite_jour) FILTER (WHERE type_flux='SORTIE') AS total_sorties, " +
            "  SUM(quantite_jour) AS total_mouvements, " +
            "  RANK() OVER (ORDER BY SUM(quantite_jour) DESC) AS rang " +
            "FROM v_stat_top_produits_avec_periode " +
            "WHERE jour_mouvement BETWEEN :from AND :to " +
            "GROUP BY produit_id, produit_code, produit_nom, type_produit " +
            "ORDER BY rang LIMIT 5";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(TopProduitsDto.class));
    }
    
}
