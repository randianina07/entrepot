-- ============================================================================
-- VUES BI & STATISTIQUES — VERSION AMÉLIORÉE
-- Entrepôt de stockage — PostgreSQL 14+
--
-- Chaque vue correspond exactement à un graphique ou KPI du dashboard
-- Thymeleaf. Les paramètres de filtre de date sont passés via des
-- requêtes paramétrées depuis le contrôleur Spring (@RequestParam).
--
-- STRUCTURE :
--   Section A  — KPI cards (4 indicateurs top)
--   Section B  — Graphique 1 : Occupation par zone
--   Section C  — Graphique 2 : Flux entrées / sorties journaliers
--   Section D  — Graphique 3 : Recettes vs Dépenses mensuelles
--   Section E  — Graphique 4 : Performance logistique (ponctualité)
--   Section F  — Graphique 5 : Top 5 produits
--   Section G  — Graphique 6 : CA par client
--   Section H  — Vues de support (période glissante, résumés)
-- ============================================================================


-- ============================================================================
-- SECTION A — KPI CARDS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- A1. Taux d'occupation global (temps réel)
--     → KPI card "Taux d'occupation global"
--     Retourne une ligne : taux_global, variation_mensuelle
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_kpi_occupation_globale AS
WITH stock_actuel AS (
    SELECT
        COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_occupe_m3
    FROM stocks_emplacement se
    JOIN produits p ON p.id = se.produit_id
),
capacite_totale AS (
    SELECT COALESCE(SUM(e.capacite_volume_m3), 0) AS total_m3
    FROM emplacements e
    WHERE e.actif = TRUE
),
-- Occupation il y a 30 jours via les snapshots historiques
snapshot_precedent AS (
    SELECT
        COALESCE(
            (SELECT SUM(oe.capacite_occupee_m3)::NUMERIC / NULLIF(SUM(oe.capacite_totale_m3), 0) * 100
             FROM occupation_espaces oe
             WHERE oe.date_snapshot = (CURRENT_DATE - INTERVAL '30 days')::DATE),
        0) AS taux_precedent
)
SELECT
    ROUND(
        sa.volume_occupe_m3 / NULLIF(ct.total_m3, 0) * 100, 1
    ) AS taux_occupation_pct,
    ct.total_m3 AS capacite_totale_m3,
    sa.volume_occupe_m3,
    ct.total_m3 - sa.volume_occupe_m3 AS volume_libre_m3,
    -- variation en points de pourcentage vs 30j avant
    ROUND(
        (ROUND(sa.volume_occupe_m3 / NULLIF(ct.total_m3, 0) * 100, 1))
        - sp.taux_precedent, 1
    ) AS variation_30j_pts
FROM stock_actuel sa, capacite_totale ct, snapshot_precedent sp;


-- ----------------------------------------------------------------------------
-- A2. Nombre de mouvements du mois courant + variation vs mois précédent
--     → KPI card "Mouvements ce mois"
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_kpi_mouvements_mois AS
WITH mois_courant AS (
    SELECT COUNT(*) AS nb
    FROM mouvements m
    JOIN statuts_mouvement sm ON sm.id = m.statut_mouvement_id
    WHERE sm.code != 'ANNULE'
      AND DATE_TRUNC('month', m.date_mouvement) = DATE_TRUNC('month', CURRENT_DATE)
),
mois_precedent AS (
    SELECT COUNT(*) AS nb
    FROM mouvements m
    JOIN statuts_mouvement sm ON sm.id = m.statut_mouvement_id
    WHERE sm.code != 'ANNULE'
      AND DATE_TRUNC('month', m.date_mouvement) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
)
SELECT
    mc.nb AS nb_mouvements_mois,
    mp.nb AS nb_mouvements_mois_precedent,
    CASE WHEN mp.nb = 0 THEN NULL
         ELSE ROUND((mc.nb - mp.nb)::NUMERIC / mp.nb * 100, 1)
    END AS variation_pct
FROM mois_courant mc, mois_precedent mp;


-- ----------------------------------------------------------------------------
-- A3. Taux de ponctualité des livraisons (mois courant)
--     → KPI card "Taux ponctualité"
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_kpi_ponctualite_mois AS
SELECT
    COUNT(*) AS nb_livraisons_total,
    COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue) AS nb_a_temps,
    COUNT(*) FILTER (WHERE l.date_livraison > l.date_prevue)  AS nb_retard,
    ROUND(
        COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue)::NUMERIC
        / NULLIF(COUNT(*), 0) * 100, 1
    ) AS taux_ponctualite_pct
FROM livraisons l
JOIN missions_logistiques ml ON ml.id = l.mission_id
JOIN statuts_mission sm ON sm.id = ml.statut_mission_id AND sm.code = 'TERMINEE'
WHERE l.date_livraison IS NOT NULL
  AND l.date_prevue IS NOT NULL
  AND DATE_TRUNC('month', l.date_livraison) = DATE_TRUNC('month', CURRENT_DATE);


-- ----------------------------------------------------------------------------
-- A4. Résultat net mensuel (recettes - dépenses du mois courant)
--     → KPI card "Résultat net (mois)"
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW v_kpi_resultat_net_mois AS
WITH recettes_mois AS (
    SELECT COALESCE(SUM(montant), 0) AS total
    FROM v_recettes
    WHERE DATE_TRUNC('month', date_recette) = DATE_TRUNC('month', CURRENT_DATE)
),
depenses_mois AS (
    SELECT COALESCE(SUM(montant), 0) AS total
    FROM depenses
    WHERE DATE_TRUNC('month', date_depense) = DATE_TRUNC('month', CURRENT_DATE)
),
recettes_mois_prec AS (
    SELECT COALESCE(SUM(montant), 0) AS total
    FROM v_recettes
    WHERE DATE_TRUNC('month', date_recette) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
),
depenses_mois_prec AS (
    SELECT COALESCE(SUM(montant), 0) AS total
    FROM depenses
    WHERE DATE_TRUNC('month', date_depense) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
)
SELECT
    rm.total AS total_recettes,
    dm.total AS total_depenses,
    rm.total - dm.total AS resultat_net,
    rmp.total - dmp.total AS resultat_net_mois_prec,
    CASE WHEN (rmp.total - dmp.total) = 0 THEN NULL
         ELSE ROUND(((rm.total - dm.total) - (rmp.total - dmp.total))::NUMERIC
                    / ABS(rmp.total - dmp.total) * 100, 1)
    END AS variation_pct
FROM recettes_mois rm, depenses_mois dm, recettes_mois_prec rmp, depenses_mois_prec dmp;


-- ============================================================================
-- SECTION B — GRAPHIQUE 1 : Occupation par zone (bar chart)
-- ============================================================================
-- Utilisé en temps réel (sans filtre date) ET avec filtre via occupation_espaces
-- Le contrôleur Spring appellera :
--   - v_stat_occupation_zones  → données temps réel (pas de filtre)
--   - v_stat_occupation_zones_historique(:dateDebut, :dateFin) → avec filtre
-- ----------------------------------------------------------------------------

-- B1. Occupation temps réel — remplace et enrichit l'ancienne v_stat_occupation_zones
DROP VIEW IF EXISTS v_stat_occupation_zones CASCADE;
CREATE OR REPLACE VIEW v_stat_occupation_zones AS
SELECT
    z.id AS zone_id,
    z.code AS zone_code,
    z.libelle AS zone_libelle,
    tz.code AS type_zone_code,
    tz.libelle AS type_zone_libelle,
    z.volume_total_m3 AS capacite_m3,
    COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_occupe_m3,
    z.volume_total_m3 - COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_libre_m3,
    CASE WHEN z.volume_total_m3 = 0 THEN 0
         ELSE ROUND((COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0)
                    / z.volume_total_m3) * 100, 1)
    END AS taux_occupation_pct,
    -- Couleur sémantique pour Chart.js (retournée directement au contrôleur)
    CASE
        WHEN z.volume_total_m3 = 0 THEN 'gray'
        WHEN (COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) / z.volume_total_m3) >= 0.80 THEN 'red'
        WHEN (COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) / z.volume_total_m3) >= 0.60 THEN 'orange'
        ELSE 'green'
    END AS statut_couleur
FROM zones z
JOIN types_zone tz ON tz.id = z.type_zone_id
LEFT JOIN emplacements e ON e.zone_id = z.id AND e.actif = TRUE
LEFT JOIN stocks_emplacement se ON se.emplacement_id = e.id
LEFT JOIN produits p ON p.id = se.produit_id
GROUP BY z.id, z.code, z.libelle, tz.code, tz.libelle, z.volume_total_m3
ORDER BY taux_occupation_pct DESC;


-- B2. Occupation historique par zone (pour le filtre date → snapshot)
--     Appelée avec :dateDebut et :dateFin depuis le contrôleur
CREATE OR REPLACE VIEW v_stat_occupation_zones_historique AS
SELECT
    oe.date_snapshot,
    z.id AS zone_id,
    z.code AS zone_code,
    z.libelle AS zone_libelle,
    tz.code AS type_zone_code,
    oe.capacite_totale_m3,
    oe.capacite_occupee_m3,
    oe.taux_occupation AS taux_occupation_pct,
    CASE
        WHEN oe.taux_occupation >= 80 THEN 'red'
        WHEN oe.taux_occupation >= 60 THEN 'orange'
        ELSE 'green'
    END AS statut_couleur
FROM occupation_espaces oe
JOIN zones z ON z.id = oe.zone_id
JOIN types_zone tz ON tz.id = z.type_zone_id
-- Le filtre WHERE oe.date_snapshot BETWEEN :dateDebut AND :dateFin
-- est appliqué par le contrôleur Spring avec @Query JPQL ou requête native
ORDER BY oe.date_snapshot, taux_occupation_pct DESC;


-- ============================================================================
-- SECTION C — GRAPHIQUE 2 : Flux entrées / sorties journaliers (line chart)
-- ============================================================================

-- C1. Vue améliorée des flux journaliers (remplace v_stat_flux_journalier)
DROP VIEW IF EXISTS v_stat_flux_journalier CASCADE;
CREATE OR REPLACE VIEW v_stat_flux_journalier AS
SELECT
    DATE(m.date_mouvement)                        AS jour,
    tm.sens                                        AS type_flux,
    tm.code                                        AS type_mouvement_code,
    tm.libelle                                     AS type_mouvement_libelle,
    COUNT(DISTINCT m.id)                           AS nb_mouvements,
    COALESCE(SUM(lm.quantite), 0)                 AS quantite_totale,
    COALESCE(SUM(lm.quantite * p.volume_unitaire_m3), 0) AS volume_total_m3,
    COALESCE(SUM(lm.quantite * p.poids_unitaire_kg), 0)  AS poids_total_kg
FROM mouvements m
JOIN statuts_mouvement sm ON sm.id = m.statut_mouvement_id AND sm.code = 'VALIDE'
JOIN types_mouvement tm ON tm.id = m.type_mouvement_id
JOIN lignes_mouvement lm ON lm.mouvement_id = m.id
JOIN produits p ON p.id = lm.produit_id
GROUP BY DATE(m.date_mouvement), tm.sens, tm.code, tm.libelle
ORDER BY jour DESC, tm.sens;


-- C2. Agrégat par jour (ENTREE + SORTIE sur une même ligne) — format pivot
--     idéal pour alimenter directement deux datasets Chart.js en une requête
CREATE OR REPLACE VIEW v_stat_flux_journalier_pivot AS
SELECT
    jour,
    COALESCE(SUM(volume_total_m3) FILTER (WHERE type_flux = 'ENTREE'), 0) AS volume_entrees_m3,
    COALESCE(SUM(volume_total_m3) FILTER (WHERE type_flux = 'SORTIE'), 0) AS volume_sorties_m3,
    COALESCE(SUM(nb_mouvements) FILTER (WHERE type_flux = 'ENTREE'), 0)   AS nb_entrees,
    COALESCE(SUM(nb_mouvements) FILTER (WHERE type_flux = 'SORTIE'), 0)   AS nb_sorties,
    COALESCE(SUM(quantite_totale) FILTER (WHERE type_flux = 'ENTREE'), 0) AS qte_entrees,
    COALESCE(SUM(quantite_totale) FILTER (WHERE type_flux = 'SORTIE'), 0) AS qte_sorties
FROM v_stat_flux_journalier
GROUP BY jour
ORDER BY jour DESC;


-- -- C3. Défaut dashboard : 7 derniers jours (vue de confort, pas de paramètre)
-- CREATE OR REPLACE VIEW v_stat_flux_7j AS
-- SELECT *
-- FROM v_stat_flux_journalier_pivot
-- WHERE jour >= CURRENT_DATE - INTERVAL '6 days'
-- ORDER BY jour;

/*new view*/
CREATE OR REPLACE VIEW v_stat_flux_7j AS
WITH calendrier AS (
    SELECT generate_series(
        CURRENT_DATE - INTERVAL '6 days', 
        CURRENT_DATE, 
        '1 day'::interval
    )::date AS jour
)
SELECT 
    c.jour,
    COALESCE(p.volume_entrees_m3, 0) AS volume_entrees_m3,
    COALESCE(p.volume_sorties_m3, 0) AS volume_sorties_m3,
    COALESCE(p.nb_entrees, 0) AS nb_entrees,
    COALESCE(p.nb_sorties, 0) AS nb_sorties,
    COALESCE(p.qte_entrees, 0) AS qte_entrees,
    COALESCE(p.qte_sorties, 0) AS qte_sorties
FROM calendrier c
LEFT JOIN v_stat_flux_journalier_pivot p ON c.jour = p.jour
ORDER BY c.jour ASC;


-- ============================================================================
-- SECTION D — GRAPHIQUE 3 : Recettes vs Dépenses mensuelles (bar chart)
-- ============================================================================

-- D1. Vue améliorée — remplace v_stat_finance_mensuelle (qui avait un UNION ALL
--     non consolidé produisant des doublons de mois)
DROP VIEW IF EXISTS v_stat_finance_mensuelle CASCADE;
CREATE OR REPLACE VIEW v_stat_finance_mensuelle AS
WITH recettes_par_mois AS (
    SELECT
        DATE_TRUNC('month', date_recette)::DATE AS mois,
        SUM(montant)                             AS total_recettes,
        COUNT(*)                                 AS nb_recettes,
        source
    FROM v_recettes
    GROUP BY DATE_TRUNC('month', date_recette)::DATE, source
),
depenses_par_mois AS (
    SELECT
        DATE_TRUNC('month', date_depense)::DATE AS mois,
        cd.code                                  AS categorie_code,
        cd.libelle                               AS categorie_libelle,
        SUM(montant)                             AS total_depenses,
        COUNT(*)                                 AS nb_depenses
    FROM depenses d
    JOIN categories_depense cd ON cd.id = d.categorie_id
    GROUP BY DATE_TRUNC('month', date_depense)::DATE, cd.code, cd.libelle
),
mois_union AS (
    SELECT mois FROM recettes_par_mois
    UNION
    SELECT mois FROM depenses_par_mois
),
recettes_totales AS (
    SELECT mois, SUM(total_recettes) AS total_recettes
    FROM recettes_par_mois
    GROUP BY mois
),
depenses_totales AS (
    SELECT mois, SUM(total_depenses) AS total_depenses
    FROM depenses_par_mois
    GROUP BY mois
)
SELECT
    mu.mois,
    TO_CHAR(mu.mois, 'Mon YYYY')                         AS mois_libelle,
    COALESCE(rt.total_recettes, 0)                        AS total_recettes,
    COALESCE(dt.total_depenses, 0)                        AS total_depenses,
    COALESCE(rt.total_recettes, 0) - COALESCE(dt.total_depenses, 0) AS resultat_net,
    CASE WHEN COALESCE(rt.total_recettes, 0) = 0 THEN NULL
         ELSE ROUND(COALESCE(dt.total_depenses, 0)
                    / rt.total_recettes * 100, 1)
    END AS taux_charge_pct
FROM mois_union mu
LEFT JOIN recettes_totales rt ON rt.mois = mu.mois
LEFT JOIN depenses_totales dt ON dt.mois = mu.mois
ORDER BY mu.mois;


-- D2. Défaut dashboard : 6 derniers mois
CREATE OR REPLACE VIEW v_stat_finance_6m AS
SELECT *
FROM v_stat_finance_mensuelle
WHERE mois >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '5 months')::DATE
ORDER BY mois;


-- D3. Détail des dépenses par catégorie et par mois (pour zoom/tooltip)
CREATE OR REPLACE VIEW v_stat_depenses_par_categorie_mois AS
SELECT
    DATE_TRUNC('month', d.date_depense)::DATE AS mois,
    TO_CHAR(DATE_TRUNC('month', d.date_depense)::DATE, 'Mon YYYY') AS mois_libelle,
    cd.code  AS categorie_code,
    cd.libelle AS categorie_libelle,
    SUM(d.montant) AS total,
    COUNT(*)       AS nb_operations
FROM depenses d
JOIN categories_depense cd ON cd.id = d.categorie_id
GROUP BY
    DATE_TRUNC('month', d.date_depense)::DATE,
    TO_CHAR(DATE_TRUNC('month', d.date_depense)::DATE, 'Mon YYYY'),
    cd.code,
    cd.libelle
ORDER BY mois DESC, total DESC;


-- ============================================================================
-- SECTION E — GRAPHIQUE 4 : Performance logistique / Ponctualité (donut)
-- ============================================================================

-- E1. Vue améliorée — remplace v_stat_performance_livraison
DROP VIEW IF EXISTS v_stat_performance_livraison CASCADE;
CREATE OR REPLACE VIEW v_stat_performance_livraison AS
SELECT
    COUNT(*)                                                                AS nb_total,
    COUNT(*) FILTER (WHERE l.date_livraison IS NOT NULL)                   AS nb_livrees,
    COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue)              AS nb_a_temps,
    COUNT(*) FILTER (WHERE l.date_livraison > l.date_prevue)               AS nb_retard,
    COUNT(*) FILTER (WHERE l.date_livraison IS NULL)                       AS nb_en_attente,
    ROUND(
        COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue)::NUMERIC
        / NULLIF(COUNT(*) FILTER (WHERE l.date_livraison IS NOT NULL), 0) * 100, 1
    )                                                                      AS taux_ponctualite_pct,
    ROUND(AVG(
        EXTRACT(EPOCH FROM (l.date_livraison - l.date_prevue)) / 3600
    ) FILTER (WHERE l.date_livraison > l.date_prevue), 2)                  AS retard_moyen_heures,
    ROUND(AVG(
        EXTRACT(EPOCH FROM (l.date_livraison - l.date_prevue)) / 3600
    ), 2)                                                                  AS ecart_moyen_heures
FROM livraisons l
JOIN missions_logistiques ml ON ml.id = l.mission_id
JOIN statuts_mission sm ON sm.id = ml.statut_mission_id AND sm.code = 'TERMINEE'
WHERE l.date_prevue IS NOT NULL;


-- E2. Performance par zone de livraison (drill-down)
CREATE OR REPLACE VIEW v_stat_performance_par_zone_livraison AS
SELECT
    zl.libelle AS zone_libelle,
    zl.commune,
    COUNT(*) FILTER (WHERE l.date_livraison IS NOT NULL)      AS nb_livrees,
    COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue) AS nb_a_temps,
    COUNT(*) FILTER (WHERE l.date_livraison > l.date_prevue)  AS nb_retard,
    ROUND(
        COUNT(*) FILTER (WHERE l.date_livraison <= l.date_prevue)::NUMERIC
        / NULLIF(COUNT(*) FILTER (WHERE l.date_livraison IS NOT NULL), 0) * 100, 1
    ) AS taux_ponctualite_pct
FROM livraisons l
JOIN zones_livraison zl ON zl.id = l.zone_livraison_id
JOIN missions_logistiques ml ON ml.id = l.mission_id
JOIN statuts_mission sm ON sm.id = ml.statut_mission_id AND sm.code = 'TERMINEE'
WHERE l.date_prevue IS NOT NULL
GROUP BY zl.id, zl.libelle, zl.commune
ORDER BY nb_livrees DESC;


-- E3. Performance par chauffeur
CREATE OR REPLACE VIEW v_stat_performance_par_chauffeur AS
SELECT
    c.nom || ' ' || COALESCE(c.prenom, '') AS chauffeur,
    COUNT(DISTINCT ml.id)                   AS nb_missions,
    COUNT(l.id) FILTER (WHERE l.date_livraison IS NOT NULL) AS nb_livraisons,
    COUNT(l.id) FILTER (WHERE l.date_livraison <= l.date_prevue) AS nb_a_temps,
    ROUND(
        COUNT(l.id) FILTER (WHERE l.date_livraison <= l.date_prevue)::NUMERIC
        / NULLIF(COUNT(l.id) FILTER (WHERE l.date_livraison IS NOT NULL), 0) * 100, 1
    ) AS taux_ponctualite_pct
FROM chauffeurs c
JOIN missions_logistiques ml ON ml.chauffeur_id = c.id
JOIN statuts_mission sm ON sm.id = ml.statut_mission_id AND sm.code = 'TERMINEE'
LEFT JOIN livraisons l ON l.mission_id = ml.id AND l.date_prevue IS NOT NULL
GROUP BY c.id, c.nom, c.prenom
ORDER BY nb_missions DESC;


-- ============================================================================
-- SECTION F — GRAPHIQUE 5 : Top 5 produits (bar chart horizontal)
-- ============================================================================

-- F1. Vue améliorée — remplace v_stat_top_produits
DROP VIEW IF EXISTS v_stat_top_produits CASCADE;
CREATE OR REPLACE VIEW v_stat_top_produits AS
SELECT
    p.id AS produit_id,
    p.code AS produit_code,
    p.nom AS produit_nom,
    tp.libelle AS type_produit,
    COALESCE(SUM(lm.quantite) FILTER (
        WHERE tm.sens = 'ENTREE' AND sm.code = 'VALIDE'
    ), 0) AS total_entrees,
    COALESCE(SUM(lm.quantite) FILTER (
        WHERE tm.sens = 'SORTIE' AND sm.code = 'VALIDE'
    ), 0) AS total_sorties,
    COALESCE(SUM(lm.quantite) FILTER (WHERE sm.code = 'VALIDE'), 0) AS total_mouvements,
    COALESCE(SUM(se.quantite), 0) AS stock_actuel,
    COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_stocke_m3,
    RANK() OVER (ORDER BY COALESCE(SUM(lm.quantite) FILTER (WHERE sm.code = 'VALIDE'), 0) DESC) AS rang
FROM produits p
LEFT JOIN types_produits tp ON tp.id = p.type_produit_id
LEFT JOIN lignes_mouvement lm ON lm.produit_id = p.id
LEFT JOIN mouvements m ON m.id = lm.mouvement_id
LEFT JOIN statuts_mouvement sm ON sm.id = m.statut_mouvement_id
LEFT JOIN types_mouvement tm ON tm.id = m.type_mouvement_id
LEFT JOIN stocks_emplacement se ON se.produit_id = p.id
GROUP BY p.id, p.code, p.nom, tp.libelle
ORDER BY rang;


-- F2. Top 5 produits (vue de confort pour le dashboard)
CREATE OR REPLACE VIEW v_stat_top5_produits AS
SELECT * FROM v_stat_top_produits WHERE rang <= 5;


-- F3. Top produits avec filtre période (utilisé avec requête paramétrée Spring)
--     Le contrôleur filtre via : WHERE DATE(m.date_mouvement) BETWEEN :debut AND :fin
CREATE OR REPLACE VIEW v_stat_top_produits_avec_periode AS
SELECT
    p.id AS produit_id,
    p.code AS produit_code,
    p.nom AS produit_nom,
    tp.libelle AS type_produit,
    DATE(m.date_mouvement) AS jour_mouvement,
    tm.sens AS type_flux,
    COALESCE(SUM(lm.quantite), 0) AS quantite_jour,
    COALESCE(SUM(lm.quantite * p.volume_unitaire_m3), 0) AS volume_jour_m3
FROM produits p
LEFT JOIN types_produits tp ON tp.id = p.type_produit_id
LEFT JOIN lignes_mouvement lm ON lm.produit_id = p.id
LEFT JOIN mouvements m ON m.id = lm.mouvement_id
LEFT JOIN statuts_mouvement sm ON sm.id = m.statut_mouvement_id AND sm.code = 'VALIDE'
LEFT JOIN types_mouvement tm ON tm.id = m.type_mouvement_id
GROUP BY p.id, p.code, p.nom, tp.libelle, DATE(m.date_mouvement), tm.sens
ORDER BY jour_mouvement DESC, quantite_jour DESC;


-- ============================================================================
-- SECTION G — GRAPHIQUE 6 : CA par client (bar chart)
-- ============================================================================

-- G1. Vue améliorée — remplace v_stat_ca_clients
DROP VIEW IF EXISTS v_stat_ca_clients CASCADE;
CREATE OR REPLACE VIEW v_stat_ca_clients AS
SELECT
    u.id AS client_id,
    COALESCE(ui.nom, u.email) AS nom_client,
    ui.prenom,
    u.email,
    ui.secteur,
    COUNT(DISTINCT c.id)                                    AS nb_contrats_actifs,
    COUNT(DISTINCT l.id)                                    AS nb_livraisons,
    COALESCE(SUM(f.prix_facture), 0)                        AS ca_stockage,
    COALESCE(SUM(fl.montant_final), 0)                      AS ca_livraison,
    COALESCE(SUM(f.prix_facture), 0)
        + COALESCE(SUM(fl.montant_final), 0)                AS ca_total,
    -- Volume moyen stocké (pour insight commercial)
    COALESCE(AVG(se.quantite * p.volume_unitaire_m3), 0)    AS volume_moyen_stocke_m3,
    RANK() OVER (
        ORDER BY COALESCE(SUM(f.prix_facture), 0) + COALESCE(SUM(fl.montant_final), 0) DESC
    ) AS rang_ca
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id AND r.code = 'CLIENT'
LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
LEFT JOIN contrats c ON c.utilisateur_id = u.id
LEFT JOIN factures f ON f.contrat_id = c.id
LEFT JOIN livraisons l ON l.client_id = u.id
LEFT JOIN facturation_livraison fl ON fl.livraison_id = l.id
LEFT JOIN mouvements mv ON mv.client_id = u.id
LEFT JOIN lignes_mouvement lm ON lm.mouvement_id = mv.id
LEFT JOIN stocks_emplacement se ON se.produit_id = lm.produit_id
LEFT JOIN produits p ON p.id = se.produit_id
GROUP BY u.id, ui.nom, ui.prenom, u.email, ui.secteur
ORDER BY ca_total DESC;


-- G2. CA par client avec filtre période
--     Le contrôleur ajoute : WHERE f.date_emission BETWEEN :debut AND :fin
--     OR fl.date_facturation::DATE BETWEEN :debut AND :fin
CREATE OR REPLACE VIEW v_stat_ca_clients_par_periode AS
SELECT
    u.id AS client_id,
    COALESCE(ui.nom, u.email) AS nom_client,
    ui.prenom,
    u.email,
    f.date_emission AS date_facturation,
    'STOCKAGE'::VARCHAR(20) AS type_ca,
    f.prix_facture AS montant
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id AND r.code = 'CLIENT'
LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
LEFT JOIN contrats c ON c.utilisateur_id = u.id
JOIN factures f ON f.contrat_id = c.id
UNION ALL
SELECT
    u.id,
    COALESCE(ui.nom, u.email),
    ui.prenom,
    u.email,
    fl.date_facturation::DATE,
    'LIVRAISON'::VARCHAR(20),
    fl.montant_final
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id AND r.code = 'CLIENT'
LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
JOIN livraisons l ON l.client_id = u.id
JOIN facturation_livraison fl ON fl.livraison_id = l.id;


-- ============================================================================
-- SECTION H — VUES DE SUPPORT ADDITIONNELLES
-- ============================================================================

-- H1. Résumé global pour le dashboard (une seule requête, toutes les KPIs)
--     Utile pour le contrôleur DashboardController.java qui peut tout charger
--     en un seul appel avant de peupler le model Thymeleaf
CREATE OR REPLACE VIEW v_dashboard_kpis AS
SELECT
    -- Occupation
    occ.taux_occupation_pct,
    occ.variation_30j_pts          AS occ_variation_pts,
    -- Mouvements mois
    mvt.nb_mouvements_mois,
    mvt.variation_pct              AS mvt_variation_pct,
    -- Ponctualité
    pct.taux_ponctualite_pct,
    pct.nb_a_temps,
    pct.nb_retard,
    -- Résultat net
    rn.resultat_net,
    rn.total_recettes,
    rn.total_depenses,
    rn.variation_pct               AS rn_variation_pct
FROM v_kpi_occupation_globale occ,
     v_kpi_mouvements_mois mvt,
     v_kpi_ponctualite_mois pct,
     v_kpi_resultat_net_mois rn;


-- H2. Activité récente (fil d'activité temps réel pour éventuels widgets futurs)
CREATE OR REPLACE VIEW v_activite_recente AS
SELECT
    m.id,
    m.code AS reference,
    m.date_mouvement,
    tm.libelle AS type_mouvement,
    tm.sens,
    sm.libelle AS statut,
    COALESCE(ui.nom || ' ' || COALESCE(ui.prenom,''), u.email) AS operateur,
    COUNT(lm.id) AS nb_lignes,
    COALESCE(SUM(lm.quantite), 0) AS quantite_totale
FROM mouvements m
JOIN types_mouvement tm ON tm.id = m.type_mouvement_id
JOIN statuts_mouvement sm ON sm.id = m.statut_mouvement_id
JOIN utilisateurs u ON u.id = m.utilisateur_id
LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
LEFT JOIN lignes_mouvement lm ON lm.mouvement_id = m.id
GROUP BY m.id, m.code, m.date_mouvement, tm.libelle, tm.sens, sm.libelle,
         ui.nom, ui.prenom, u.email
ORDER BY m.date_mouvement DESC;


-- H3. Stock par type de zone (pour la section "Capacité disponible" éventuelle)
CREATE OR REPLACE VIEW v_stock_par_type_zone AS
SELECT
    tz.code AS type_zone_code,
    tz.libelle AS type_zone_libelle,
    COUNT(DISTINCT z.id) AS nb_zones,
    COUNT(DISTINCT e.id) AS nb_emplacements,
    SUM(e.capacite_volume_m3) AS capacite_totale_m3,
    COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_occupe_m3,
    SUM(e.capacite_volume_m3) - COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_libre_m3
FROM types_zone tz
JOIN zones z ON z.type_zone_id = tz.id
JOIN emplacements e ON e.zone_id = z.id AND e.actif = TRUE
LEFT JOIN stocks_emplacement se ON se.emplacement_id = e.id
LEFT JOIN produits p ON p.id = se.produit_id
GROUP BY tz.id, tz.code, tz.libelle
ORDER BY volume_occupe_m3 DESC;


-- ============================================================================
-- INDEX COMPLÉMENTAIRES pour accélérer les requêtes BI avec filtres de date
-- ============================================================================

-- Accélère les filtres date sur mouvements (très sollicité par les vues C)
CREATE INDEX IF NOT EXISTS idx_mouvements_date_mouvement
    ON mouvements(date_mouvement);

-- Accélère les filtres date sur factures
CREATE INDEX IF NOT EXISTS idx_factures_date_emission
    ON factures(date_emission);

-- Accélère les filtres date sur depenses
CREATE INDEX IF NOT EXISTS idx_depenses_date_depense
    ON depenses(date_depense);

-- Accélère les filtres date sur livraisons (vues E)
CREATE INDEX IF NOT EXISTS idx_livraisons_date_livraison
    ON livraisons(date_livraison);

CREATE INDEX IF NOT EXISTS idx_livraisons_date_prevue
    ON livraisons(date_prevue);

-- Accélère les recherches de snapshots (vue B2)
CREATE INDEX IF NOT EXISTS idx_occupation_espaces_zone_date
    ON occupation_espaces(zone_id, date_snapshot);

-- Droits necessaires pour l'application Spring Boot.
-- A executer avec le proprietaire des vues ou un superutilisateur PostgreSQL.
GRANT SELECT ON
    v_kpi_occupation_globale,
    v_kpi_mouvements_mois,
    v_kpi_ponctualite_mois,
    v_kpi_resultat_net_mois,
    v_stat_occupation_zones,
    v_stat_occupation_zones_historique,
    v_stat_flux_journalier,
    v_stat_flux_journalier_pivot,
    v_stat_flux_7j,
    v_stat_finance_mensuelle,
    v_stat_finance_6m,
    v_stat_depenses_par_categorie_mois,
    v_stat_performance_livraison,
    v_stat_performance_par_zone_livraison,
    v_stat_performance_par_chauffeur,
    v_stat_top_produits,
    v_stat_top5_produits,
    v_stat_top_produits_avec_periode,
    v_stat_ca_clients,
    v_stat_ca_clients_par_periode,
    v_dashboard_kpis,
    v_activite_recente,
    v_stock_par_type_zone
TO postgres;

-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================
-- RÉCAPITULATIF DES VUES ET LEUR MAPPING DASHBOARD
-- ============================================================================
--
-- Vue SQL                              | Graphique / KPI Thymeleaf
-- -------------------------------------|------------------------------------------
-- v_kpi_occupation_globale             | KPI card 1 — Taux d'occupation global
-- v_kpi_mouvements_mois                | KPI card 2 — Mouvements ce mois
-- v_kpi_ponctualite_mois               | KPI card 3 — Taux ponctualité
-- v_kpi_resultat_net_mois              | KPI card 4 — Résultat net (mois)
-- v_dashboard_kpis                     | Toutes KPIs en une requête (contrôleur)
-- v_stat_occupation_zones              | Graphique 1 — Occupation par zone (TR)
-- v_stat_occupation_zones_historique   | Graphique 1 — avec filtre date
-- v_stat_flux_7j                       | Graphique 2 — Flux 7 derniers jours
-- v_stat_flux_journalier_pivot         | Graphique 2 — avec filtre date
-- v_stat_finance_6m                    | Graphique 3 — Recettes/Dépenses 6 mois
-- v_stat_finance_mensuelle             | Graphique 3 — avec filtre date
-- v_stat_performance_livraison         | Graphique 4 — Ponctualité (donut)
-- v_stat_performance_par_zone_livraison| Graphique 4 — drill-down par zone
-- v_stat_top5_produits                 | Graphique 5 — Top 5 produits (défaut)
-- v_stat_top_produits_avec_periode     | Graphique 5 — avec filtre date
-- v_stat_ca_clients                    | Graphique 6 — CA par client
-- v_stat_ca_clients_par_periode        | Graphique 6 — avec filtre date
-- v_activite_recente                   | Fil d'activité (optionnel)
-- v_stock_par_type_zone                | Widget capacité disponible (optionnel)
-- ============================================================================
