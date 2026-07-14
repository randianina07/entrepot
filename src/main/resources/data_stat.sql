-- ====================================================
-- 0. NETTOYAGE ET REMISE À ZÉRO
-- ====================================================
TRUNCATE TABLE
    flux_entrees_sorties,
    lignes_mouvement,
    stocks_emplacement,
    top_produits,
    stats_clients,
    missions_logistiques,
    mouvements,
    vehicules,
    chauffeurs,
    utilisateurs_info,
    utilisateurs,
    produits,
    emplacement,
    zone,
    type_zone,
    type_produit,
    allees,
    allee,
    etage,
    roles,
    statuts_mission,
    statuts_vehicule,
    types_vehicule,
    statuts_mouvement,
    types_mouvement
RESTART IDENTITY CASCADE;

-- ====================================================
-- 1. CONFIGURATION UTILISATEURS & RÔLES
-- ====================================================
INSERT INTO roles (code, libelle) VALUES
    ('ADMIN', 'Administrateur'),
    ('GESTIONNAIRE', 'Gestionnaire'),
    ('RESPONSABLE_LOGISTIQUE', 'Responsable Logistique'),
    ('CLIENT', 'Client');

INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, date_creation, actif) VALUES
    ('admin@entrepot.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', (SELECT id FROM roles WHERE code = 'ADMIN'), '2026-07-01 08:00:00', true),
    ('client1@test.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', (SELECT id FROM roles WHERE code = 'CLIENT'), '2026-07-01 08:05:00', true);

INSERT INTO utilisateurs_info (utilisateur_id, nom, prenom, numero, adresse, secteur) VALUES
    ((SELECT id FROM utilisateurs WHERE email = 'admin@entrepot.com'), 'Admin', 'System', '001234567', 'Adresse Admin', 'Centre'),
    ((SELECT id FROM utilisateurs WHERE email = 'client1@test.com'), 'Client', 'Test', '002345678', 'Adresse Client', 'Nord');

-- ====================================================
-- 2. STRUCTURE PHYSIQUE ET CONFIGURATION PRODUITS
-- ====================================================
INSERT INTO etage (libelle, numero_etage) VALUES
    ('Etage 1', 1),
    ('Etage 2', 2),
    ('Etage 3', 3);

INSERT INTO allee (libelle) VALUES
    ('Allee Principale'),
    ('Allee Secondaire');

INSERT INTO allees (code) VALUES
    ('A01'),
    ('A02');

INSERT INTO type_produit (code, libelle) VALUES
    ('ALIMENTAIRE', 'Alimentaire'),
    ('INDUSTRIEL', 'Industriel');

INSERT INTO type_zone (code, libelle, type_produit_id) VALUES
    ('ETA', 'Etagere', (SELECT id FROM type_produit WHERE code = 'ALIMENTAIRE')),
    ('CHF', 'Chambre Froide', (SELECT id FROM type_produit WHERE code = 'INDUSTRIEL')),
    ('SEC', 'Zone Securisee', (SELECT id FROM type_produit WHERE code = 'ALIMENTAIRE'));

INSERT INTO produits (code, nom, description, type_produit_id, volume_unitaire_m3, poids_unitaire_kg, actif) VALUES
    ('PROD001', 'Produit A', 'Description produit A', (SELECT id FROM type_produit WHERE code = 'ALIMENTAIRE'), 0.5000, 10.000, true),
    ('PROD002', 'Produit B', 'Description produit B', (SELECT id FROM type_produit WHERE code = 'INDUSTRIEL'), 1.0000, 20.000, true);

-- ====================================================
-- 3. ZONES, EMPLACEMENTS ET STOCKS (Pour tes Graphes d'Occupation)
-- ====================================================
INSERT INTO zone (libelle, volume_total_m3, allees_id, type_zone_id) VALUES
    ('Zone A1 Alimentaire A', 500.00, (SELECT id FROM allees WHERE code = 'A01'), (SELECT id FROM type_zone WHERE code = 'CHF')),
    ('Zone A2 Alimentaire B', 500.00, (SELECT id FROM allees WHERE code = 'A02'), (SELECT id FROM type_zone WHERE code = 'CHF')),
    ('Zone A1 Industriel A',  500.00, (SELECT id FROM allees WHERE code = 'A01'), (SELECT id FROM type_zone WHERE code = 'ETA')),
    ('Zone A2 Industriel B',  500.00, (SELECT id FROM allees WHERE code = 'A02'), (SELECT id FROM type_zone WHERE code = 'SEC'));

-- Note : Augmentation de la capacite_volume_m3 de tous les emplacements à 100.0 pour éviter l'erreur de capacité du trigger
INSERT INTO emplacement (code, etage_id, capacite_volume_m3, charge_max, colonne, allee_id) VALUES
    ('E1', (SELECT id FROM etage WHERE numero_etage = 1), 100.0, 1500, 1, (SELECT id FROM allee WHERE libelle = 'Allee Principale')),
    ('E2', (SELECT id FROM etage WHERE numero_etage = 1), 100.0, 1300, 2, (SELECT id FROM allee WHERE libelle = 'Allee Principale')),
    ('E3', (SELECT id FROM etage WHERE numero_etage = 1), 100.0, 1300, 3, (SELECT id FROM allee WHERE libelle = 'Allee Principale')),
    ('E4', (SELECT id FROM etage WHERE numero_etage = 2), 100.0, 1300, 1, (SELECT id FROM allee WHERE libelle = 'Allee Principale')),
    ('E5', (SELECT id FROM etage WHERE numero_etage = 2), 100.0, 1300, 2, (SELECT id FROM allee WHERE libelle = 'Allee Principale')),
    ('E6', (SELECT id FROM etage WHERE numero_etage = 2), 100.0, 1300, 3, (SELECT id FROM allee WHERE libelle = 'Allee Secondaire')),
    ('E7', (SELECT id FROM etage WHERE numero_etage = 3), 100.0, 1300, 1, (SELECT id FROM allee WHERE libelle = 'Allee Secondaire')),
    ('E8', (SELECT id FROM etage WHERE numero_etage = 3), 100.0, 1300, 2, (SELECT id FROM allee WHERE libelle = 'Allee Secondaire')),
    ('E9', (SELECT id FROM etage WHERE numero_etage = 3), 100.0, 1300, 3, (SELECT id FROM allee WHERE libelle = 'Allee Secondaire'));

-- Re-ajout de la colonne 'zone_id' maintenant qu'elle est définie dans final.sql
INSERT INTO stocks_emplacement (emplacement_id, produit_id, zone_id, quantite) VALUES
    ((SELECT id FROM emplacement WHERE code = 'E1'), (SELECT id FROM produits WHERE code = 'PROD001'), (SELECT id FROM zone WHERE libelle = 'Zone A1 Alimentaire A'), 20.000),
    ((SELECT id FROM emplacement WHERE code = 'E2'), (SELECT id FROM produits WHERE code = 'PROD002'), (SELECT id FROM zone WHERE libelle = 'Zone A1 Industriel A'), 10.000);

-- ====================================================
-- 4. FLUX ET MOUVEMENTS
-- ====================================================
INSERT INTO types_mouvement (code, libelle, sens) VALUES
    ('ENTREE', 'Entree', 'ENTREE'),
    ('SORTIE', 'Sortie', 'SORTIE'),
    ('TRANSFERT', 'Transfert', 'SORTIE'),
    ('RETOUR', 'Retour', 'ENTREE');

INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
    ('BROUILLON', 'Brouillon', 1),
    ('VALIDE', 'Valide', 2),
    ('ANNULE', 'Annule', 3);

INSERT INTO mouvements (code, date_mouvement, type_mouvement_id, statut_mouvement_id, client_id, utilisateur_id, notes) VALUES
    ('MVT-001', '2026-07-10 09:00:00', (SELECT id FROM types_mouvement WHERE code = 'ENTREE'), (SELECT id FROM statuts_mouvement WHERE code = 'VALIDE'), (SELECT id FROM utilisateurs WHERE email = 'client1@test.com'), (SELECT id FROM utilisateurs WHERE email = 'admin@entrepot.com'), 'Entree initiale'),
    ('MVT-002', '2026-07-11 15:30:00', (SELECT id FROM types_mouvement WHERE code = 'SORTIE'), (SELECT id FROM statuts_mouvement WHERE code = 'VALIDE'), (SELECT id FROM utilisateurs WHERE email = 'client1@test.com'), (SELECT id FROM utilisateurs WHERE email = 'admin@entrepot.com'), 'Sortie partielle');

-- Maintenant que 'E1' fait 15.0 m3 de capacité, l'entrée de 25 * 0.5 m3 = 12.5 m3 va passer sans erreur !
INSERT INTO lignes_mouvement (mouvement_id, produit_id, emplacement_source_id, emplacement_dest_id, quantite) VALUES
    ((SELECT id FROM mouvements WHERE code = 'MVT-001'), (SELECT id FROM produits WHERE code = 'PROD001'), NULL, (SELECT id FROM emplacement WHERE code = 'E1'), 25.000),
    ((SELECT id FROM mouvements WHERE code = 'MVT-002'), (SELECT id FROM produits WHERE code = 'PROD001'), (SELECT id FROM emplacement WHERE code = 'E1'), NULL, 5.000);

-- ====================================================
-- 5. LOGISTIQUE ET FLOTTE DE VÉHICULES
-- ====================================================
INSERT INTO statuts_mission (code, libelle) VALUES
    ('PLANIFIEE', 'Planifiee'),
    ('EN_COURS', 'En cours'),
    ('TERMINEE', 'Terminee');

INSERT INTO statuts_vehicule (code, libelle) VALUES
    ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission'),
    ('MAINTENANCE', 'Maintenance');

INSERT INTO types_vehicule (code, libelle, description) VALUES
    ('CAMION', 'Camion', 'Vehicule de transport principal'),
    ('VAN', 'Van', 'Vehicule leger de livraison');

INSERT INTO chauffeurs (nom, prenom, telephone, numero_permis, date_expiration_permis, actif) VALUES
    ('Rakoto', 'Jean', '0320000001', 'PERMIS-001', '2028-12-31', true);

INSERT INTO vehicules (immatriculation, marque, modele, annee, capacite_volume_m3, capacite_charge_kg, kilometrage_actuel, type_vehicule_id, statut_vehicule_id) VALUES
    ('1012-TAB', 'Isuzu', 'NPR', 2022, 15.000, 3000.00, 12500.00, (SELECT id FROM types_vehicule WHERE code = 'CAMION'), (SELECT id FROM statuts_vehicule WHERE code = 'DISPONIBLE'));

INSERT INTO missions_logistiques (reference_mission, date_depart_prevue, date_arrivee_prevue, date_depart_reelle, date_arrivee_reelle, vehicule_id, chauffeur_id, statut_mission_id, observations) VALUES
    ('MIS-2026-001', '2026-07-10 08:00:00', '2026-07-10 12:00:00', '2026-07-10 08:10:00', '2026-07-10 11:55:00', (SELECT id FROM vehicules WHERE immatriculation = '1012-TAB'), (SELECT id FROM chauffeurs WHERE numero_permis = 'PERMIS-001'), (SELECT id FROM statuts_mission WHERE code = 'TERMINEE'), 'Mission test terminee');

-- ====================================================
-- 6. STATISTIQUES ET RAPPORTS
-- ====================================================
INSERT INTO stats_clients (date_debut, date_fin, volume_stocke_m3, duree_moyenne_jours, nb_entrees, nb_sorties, chiffre_affaires, client_id) VALUES
    ('2026-07-01', '2026-07-31', 12.500, 8.50, 1, 1, 150000.00, (SELECT id FROM utilisateurs WHERE email = 'client1@test.com'));

INSERT INTO top_produits (date_snapshot, rang, quantite_totale, duree_moyenne_stockage_jours, produit_id) VALUES
    ('2026-07-13', 1, 25.000, 8.50, (SELECT id FROM produits WHERE code = 'PROD001')),
    ('2026-07-13', 2, 10.000, 6.00, (SELECT id FROM produits WHERE code = 'PROD002'));