
\c postgres
DROP DATABASE IF EXISTS entrepot;
CREATE DATABASE entrepot;
\c entrepot

-- =========================
-- 1. TYPE_PRODUIT
-- =========================
INSERT INTO types_produits (id, code, libelle) VALUES
(1, 'TP1', 'Alimentaire'),
(2, 'TP2', 'Electroménager');

-- =========================
-- 2. TYPE_ZONE (2 zones par type)
-- =========================
INSERT INTO type_zone (id, code, libelle, type_produit_id) VALUES
(1, 'TZ1', 'Zone Alimentaire A', 1),
(2, 'TZ2', 'Zone Alimentaire B', 1),
(3, 'TZ3', 'Zone Electro A', 2),
(4, 'TZ4', 'Zone Electro B', 2);

-- =========================
-- 3. ALLEES
-- =========================
INSERT INTO allees (id, code) VALUES
(1, 'A1'),
(2, 'A2');

-- =========================
-- 4. ZONE 
-- =========================
INSERT INTO zone (id, libelle, volume_total_m3, allees_id, type_zone_id) VALUES
(1, 'Zone A1 Alimentaire A', 500, 1, 1),
(2, 'Zone A2 Alimentaire B', 600, 2, 1),
(3, 'Zone A1 Electro A', 700, 1, 3),
(4, 'Zone A2 Electro B', 800, 2, 4);

-- =========================
-- 5. ETAGE (indépendant maintenant)
-- =========================
INSERT INTO etage (id, libelle, numero_etage) VALUES
(1, 'Etage 1', 1),
(2, 'Etage 2', 2),
(3, 'Etage 3', 3);

-- =========================
-- 6. EMPLACEMENT
-- =========================
INSERT INTO emplacement (id, code, etage_id, capacite_volume_m3, actif, charge_max, colonne) VALUES
(1, 'E1', 1, 11.5, true, 1300, 1),
(2, 'E2', 1, 11.0, false, 1300, 2),
(3, 'E3', 1, 11.0, false, 1300, 3),
(4, 'E4', 2, 11.0, true, 1300, 1),
(5, 'E5', 2, 11.0, true, 1300, 2),
(6, 'E6', 2, 11.0, true, 1300, 3),
(7, 'E7', 3, 11.0, false, 1300, 1),
(8, 'E8', 3, 11.0, true, 1300, 2),
(9, 'E9', 3, 11.0, false, 1300, 3);

-- =========================
-- 7. PRODUITS
-- =========================
INSERT INTO produits (id, code, nom, description, volume_unitaire_m3, poids_unitaire_kg, type_produit_id) VALUES
(1, 'P1', 'Riz', 'Sac de riz 50kg', 0.05, 50, 1),
(2, 'P2', 'Sucre', 'Sac de sucre 25kg', 0.03, 25, 1),
(3, 'P3', 'TV', 'Télévision 55 pouces', 0.2, 15, 2);

-- =========================
-- 8. STOCKS_EMPLACEMENT 
-- =========================
INSERT INTO stocks_emplacement (id, quantite, emplacement_id, produit_id, zone_id) VALUES
(1, 1, 1, 1, 1),
(2, 1, 4, 2, 1),
(3, 1, 6, 3, 1),
(4, 1, 8, 1, 1),
(5, 1, 5, 2, 2),
(6, 1, 6, 3, 2);