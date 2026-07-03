-- ==========================================
-- SCRIPT DE DONNEES DE TEST POUR L'ENTREPOT
-- ==========================================
-- Permet de tester la pagination, les stocks, 
-- le tableau de bord et les composants metiers.
-- ==========================================

-- 1. Nettoyage des donnees existantes (optionnel mais recommande pour eviter les doublons)
TRUNCATE TABLE lignes_mouvement, mouvements, stocks_emplacement, flux_entrees_sorties, stats_clients, top_produits CASCADE;
TRUNCATE TABLE emplacements, zones, types_zone CASCADE;
TRUNCATE TABLE produits, types_produits CASCADE;
TRUNCATE TABLE utilisateurs, roles CASCADE;
TRUNCATE TABLE types_mouvement, statuts_mouvement CASCADE;

-- 2. Insertion des Referentiels

-- ROLES
INSERT INTO roles (id, code, libelle) VALUES 
(1, 'ADMIN', 'Administrateur'),
(2, 'CLIENT', 'Client'),
(3, 'RESPONSABLE_LOGISTIQUE', 'Responsable Logistique');
ALTER SEQUENCE roles_id_seq RESTART WITH 4;

-- UTILISATEURS
INSERT INTO utilisateurs (id, email, nom, mot_de_passe_hash, actif, date_creation, role_id) VALUES 
(1, 'admin@test.com', 'Admin Test', 'hash123', true, NOW(), 1),
(2, 'clientA@test.com', 'Entreprise Alpha', 'hash123', true, NOW(), 2),
(3, 'clientB@test.com', 'Entreprise Beta', 'hash123', true, NOW(), 2),
(4, 'logistique@test.com', 'Chef Logistique', 'hash123', true, NOW(), 3);
ALTER SEQUENCE utilisateurs_id_seq RESTART WITH 5;

-- TYPES ZONE
INSERT INTO types_zone (id, code, libelle, controle_temperature, acces_restreint, journalisation_acces, charge_lourde_possible) VALUES 
(1, 'FROID', 'Chambre froide', true, true, true, false),
(2, 'SEC', 'Zone de stockage sec', false, false, false, true),
(3, 'DANGEREUX', 'Produits dangereux', false, true, true, true);
ALTER SEQUENCE types_zone_id_seq RESTART WITH 4;

-- ZONES
INSERT INTO zones (id, code, volume_total_m3, type_zone_id) VALUES 
(1, 'ZONE-FROID-A', 500.0, 1),
(2, 'ZONE-SEC-B', 2000.0, 2),
(3, 'ZONE-SEC-C', 2000.0, 2);
ALTER SEQUENCE zones_id_seq RESTART WITH 4;

-- EMPLACEMENTS
INSERT INTO emplacements (id, code, capacite_volume_m3, actif, zone_id) VALUES 
(1, 'F-01', 5000.0, true, 1),
(2, 'F-02', 5000.0, true, 1),
(3, 'S-01', 5000.0, true, 2),
(4, 'S-02', 5000.0, true, 2),
(5, 'S-03', 5000.0, true, 2),
(6, 'S-04', 5000.0, true, 3),
(7, 'S-05', 5000.0, true, 3);
ALTER SEQUENCE emplacements_id_seq RESTART WITH 8;

-- TYPES PRODUITS
INSERT INTO types_produits (id, code, libelle) VALUES 
(1, 'ALIM', 'Alimentaire'),
(2, 'ELEC', 'Electronique'),
(3, 'MEUBLE', 'Meubles');
ALTER SEQUENCE types_produits_id_seq RESTART WITH 4;

-- PRODUITS
INSERT INTO produits (id, code, nom, volume_unitaire_m3, poids_unitaire_kg, actif, type_produit_id) VALUES 
(1, 'PRD-POM', 'Pommes (Cagette)', 0.05, 10.0, true, 1),
(2, 'PRD-POIS', 'Poisson surgele (Carton)', 0.08, 15.0, true, 1),
(3, 'PRD-TV', 'Television 55"', 0.15, 12.0, true, 2),
(4, 'PRD-PC', 'Ordinateur Portable', 0.02, 2.5, true, 2),
(5, 'PRD-CAN', 'Canape 3 places', 1.5, 45.0, true, 3);
ALTER SEQUENCE produits_id_seq RESTART WITH 6;

-- TYPES MOUVEMENT
INSERT INTO types_mouvement (id, code, libelle, sens) VALUES 
(1, 'ENTREE', 'Entree de stock', 'ENTREE'),
(2, 'SORTIE', 'Sortie de stock', 'SORTIE'),
(3, 'TRANSFERT_INTERNE', 'Transfert interne', 'SORTIE'),
(4, 'RETOUR', 'Retour', 'ENTREE');
ALTER SEQUENCE types_mouvement_id_seq RESTART WITH 5;

-- STATUTS MOUVEMENT
INSERT INTO statuts_mouvement (id, code, libelle, ordre) VALUES 
(1, 'BROUILLON', 'Brouillon', 1),
(2, 'VALIDE', 'Valide', 2),
(3, 'ANNULE', 'Annule', 3);
ALTER SEQUENCE statuts_mouvement_id_seq RESTART WITH 4;

-- 3. Generation de Mouvements (pour tester la pagination)
-- Utilisation d'un bloc DO PostgreSQL pour generer un grand volume de donnees
DO $$
DECLARE
    i INTEGER;
    mvt_id INTEGER;
    mvt_type INTEGER;
    mvt_statut INTEGER;
    client_id INTEGER;
    prod_id INTEGER;
    emp_id INTEGER;
    sens VARCHAR;
BEGIN
    FOR i IN 1..45 LOOP
        -- Calcul des variables aleatoires / cycliques
        mvt_type := (i % 2) + 1; -- 1 (ENTREE) ou 2 (SORTIE)
        mvt_statut := (i % 3) + 1; -- 1 (BROUILLON), 2 (VALIDE), 3 (ANNULE)
        client_id := (i % 2) + 2; -- 2 ou 3
        
        -- Creation du mouvement
        INSERT INTO mouvements (id, code, date_mouvement, type_mouvement_id, statut_mouvement_id, client_id, utilisateur_id, notes) 
        VALUES (i, 'MVT-TEST-' || LPAD(i::text, 4, '0'), NOW() - (i || ' hours')::INTERVAL, mvt_type, mvt_statut, client_id, 4, 'Mouvement de test genere #' || i)
        RETURNING id INTO mvt_id;
        
        -- Creation de la ligne de mouvement
        prod_id := (i % 5) + 1;
        emp_id := (i % 7) + 1;
        
        IF mvt_type = 1 THEN
            -- ENTREE
            INSERT INTO lignes_mouvement (mouvement_id, quantite, produit_id, emplacement_dest_id)
            VALUES (mvt_id, (i * 2.5), prod_id, emp_id);
        ELSE
            -- SORTIE
            INSERT INTO lignes_mouvement (mouvement_id, quantite, produit_id, emplacement_source_id)
            VALUES (mvt_id, (i * 1.5), prod_id, emp_id);
        END IF;
        
    END LOOP;
END $$;

-- Mise à jour des sequences après les insertions manuelles et generées
SELECT setval('mouvements_id_seq', (SELECT MAX(id) FROM mouvements));
SELECT setval('lignes_mouvement_id_seq', (SELECT MAX(id) FROM lignes_mouvement));

-- 4. Initialisation du stock reel (stocks_emplacement)
-- On met des quantites importantes pour ne pas avoir de blocages lors des tests de sorties
INSERT INTO stocks_emplacement (emplacement_id, produit_id, quantite) VALUES 
(1, 2, 500.0),
(2, 2, 100.0),
(3, 1, 1000.0),
(3, 3, 250.0),
(4, 4, 300.0),
(5, 5, 20.0),
(6, 4, 150.0),
(7, 1, 500.0);
ALTER SEQUENCE stocks_emplacement_id_seq RESTART WITH 9;

-- 5. Flux entrees/sorties de test (pour le Dashboard et Stats)
INSERT INTO flux_entrees_sorties (date, type_flux, type_detail, quantite, volume_m3, mouvement_id) VALUES 
(CURRENT_DATE, 'ENTREE', 'ENTREE', 50.0, 10.0, 1),
(CURRENT_DATE, 'SORTIE', 'SORTIE', 20.0, 4.0, 2),
(CURRENT_DATE - INTERVAL '1 day', 'ENTREE', 'ENTREE', 100.0, 20.0, 3),
(CURRENT_DATE - INTERVAL '2 days', 'SORTIE', 'SORTIE', 10.0, 2.0, 4);
