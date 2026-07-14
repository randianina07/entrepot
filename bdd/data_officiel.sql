-- ====================================================
-- 0. NETTOYAGE ET REMISE À ZÉRO
-- ====================================================
/*
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
    ('TRANSFERT_INTERNE', 'Transfert interne', 'SORTIE'),
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
*/
CREATE TABLE IF NOT EXISTS zones_livraison (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL,
    commune VARCHAR(150),
    distance_km NUMERIC(10,2),
    tarif_base NUMERIC(14,2),
    actif BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS modes_calcul_livraison (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS tarifs_livraison (
    id BIGSERIAL PRIMARY KEY,
    zone_livraison_id BIGINT NOT NULL,
    mode_calcul_id BIGINT NOT NULL,
    prix_base NUMERIC(14,2),
    prix_par_kg NUMERIC(14,2),
    prix_par_m3 NUMERIC(14,2),
    date_debut_validite TIMESTAMP,
    date_fin_validite TIMESTAMP
);

CREATE TABLE IF NOT EXISTS livraisons (
    id BIGSERIAL PRIMARY KEY,
    mission_id BIGINT,
    client_id BIGINT,
    adresse_livraison VARCHAR(255),
    zone_livraison_id BIGINT,
    poids_total NUMERIC(14,3),
    volume_total NUMERIC(14,3),
    date_prevue TIMESTAMP,
    date_livraison TIMESTAMP,
    montant_livraison NUMERIC(14,2)
);

CREATE TABLE IF NOT EXISTS facturation_livraison (
    id BIGSERIAL PRIMARY KEY,
    livraison_id BIGINT NOT NULL,
    tarif_livraison_id BIGINT NOT NULL,
    poids_facture NUMERIC(14,3),
    volume_facture NUMERIC(14,3),
    montant_calcule NUMERIC(14,2),
    montant_final NUMERIC(14,2),
    date_facturation TIMESTAMP
);

CREATE TABLE IF NOT EXISTS preuves_livraison (
    id BIGSERIAL PRIMARY KEY,
    livraison_id BIGINT NOT NULL,
    date_validation TIMESTAMP,
    signature_client VARCHAR(255),
    photo_colis VARCHAR(255),
    commentaire TEXT
);

CREATE TABLE IF NOT EXISTS types_maintenance (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS maintenances_vehicule (
    id BIGSERIAL PRIMARY KEY,
    vehicule_id BIGINT NOT NULL,
    type_maintenance_id BIGINT NOT NULL,
    date_maintenance DATE,
    kilometrage NUMERIC(14,2),
    cout NUMERIC(14,2),
    description TEXT,
    prochaine_maintenance DATE
);

CREATE TABLE IF NOT EXISTS historique_vehicule (
    id BIGSERIAL PRIMARY KEY,
    vehicule_id BIGINT NOT NULL,
    mission_id BIGINT,
    date_depart TIMESTAMP,
    date_arrivee TIMESTAMP,
    kilometrage_Depart NUMERIC(14,2),
    kilometrage_arrivee NUMERIC(14,2),
    distance_parcourue DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS flux_entrees_sorties (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    type_flux VARCHAR(10) NOT NULL,
    type_detail VARCHAR(50),
    quantite NUMERIC(12,3) NOT NULL,
    volume_m3 NUMERIC(12,3) NOT NULL,
    mouvement_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS stats_clients (
    id BIGSERIAL PRIMARY KEY,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    volume_stocke_m3 NUMERIC(12,3),
    duree_moyenne_jours NUMERIC(8,2),
    nb_entrees INTEGER,
    nb_sorties INTEGER,
    chiffre_affaires NUMERIC(14,2),
    client_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS top_produits (
    id BIGSERIAL PRIMARY KEY,
    date_snapshot DATE NOT NULL,
    rang INTEGER NOT NULL,
    quantite_totale NUMERIC(12,3) NOT NULL,
    duree_moyenne_stockage_jours NUMERIC(8,2),
    produit_id BIGINT NOT NULL
);

ALTER TABLE IF EXISTS stocks_emplacement
    ADD COLUMN IF NOT EXISTS zone_id BIGINT;

-- ====================================================================
-- Jeu de donnees de test - Gestion Entrepot
-- Genere a partir des entites du dossier model/ (Hibernate ddl-auto=create)
-- Execute automatiquement par Spring Boot au demarrage de l'application
-- ====================================================================

TRUNCATE TABLE
    flux_entrees_sorties,
    lignes_mouvement,
    preuves_livraison,
    facturation_livraison,
    livraisons,
    stocks_emplacement,
    top_produits,
    stats_clients,
    historique_vehicule,
    maintenances_vehicule,
    missions_logistiques,
    mouvements,
    tarifs_livraison,
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
    zones_livraison,
    modes_calcul_livraison,
    types_maintenance,
    roles,
    statuts_mission,
    statuts_vehicule,
    types_vehicule,
    statuts_mouvement,
    types_mouvement
RESTART IDENTITY CASCADE;

-- ====================================================================
-- 1. TABLES DE REFERENCE (sans dependance)
-- ====================================================================

INSERT INTO roles (code, libelle) VALUES
    ('ADMIN', 'Administrateur'),
    ('GESTIONNAIRE', 'Gestionnaire'),
    ('RESPONSABLE_LOGISTIQUE', 'Responsable Logistique'),
    ('CLIENT', 'Client');

INSERT INTO types_mouvement (code, libelle, sens) VALUES
    ('ENTREE', 'Entree', 'ENTREE'),
    ('SORTIE', 'Sortie', 'SORTIE'),
    ('TRANSFERT_INTERNE', 'Transfert interne', 'SORTIE'),
    ('RETOUR', 'Retour', 'ENTREE');

INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
    ('BROUILLON', 'Brouillon', 1),
    ('VALIDE', 'Valide', 2),
    ('ANNULE', 'Annule', 3);

INSERT INTO statuts_mission (code, libelle) VALUES
    ('PLANIFIEE', 'Planifiee'),
    ('EN_COURS', 'En cours'),
    ('TERMINEE', 'Terminee'),
    ('ANNULEE', 'Annulee');

INSERT INTO statuts_vehicule (code, libelle) VALUES
    ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission'),
    ('MAINTENANCE', 'Maintenance');

INSERT INTO types_vehicule (code, libelle, description) VALUES
    ('CAMION', 'Camion', 'Vehicule de transport principal'),
    ('VAN', 'Van', 'Vehicule leger de livraison'),
    ('CAMIONNETTE', 'Camionnette', 'Vehicule de petite livraison urbaine');

INSERT INTO types_maintenance (code, libelle) VALUES
    ('VIDANGE', 'Vidange'),
    ('REVISION_GENERALE', 'Revision generale'),
    ('PNEUS', 'Remplacement pneus'),
    ('FREINS', 'Systeme de freinage');

INSERT INTO etage (id, libelle, numero_etage) VALUES
    (1, 'Etage 1', 1),
    (2, 'Etage 2', 2),
    (3, 'Etage 3', 3);

INSERT INTO allee (libelle) VALUES
    ('Allee Principale'),
    ('Allee Secondaire'),
    ('Allee Reserve');

INSERT INTO allees (code) VALUES
    ('A01'),
    ('A02'),
    ('A03');

INSERT INTO type_produit (code, libelle) VALUES
    ('ALIMENTAIRE', 'Alimentaire'),
    ('INDUSTRIEL', 'Industriel'),
    ('ELECTRONIQUE', 'Electronique'),
    ('TEXTILE', 'Textile');

INSERT INTO modes_calcul_livraison (code, libelle) VALUES
    ('POIDS', 'Calcul au poids'),
    ('VOLUME', 'Calcul au volume'),
    ('FORFAIT', 'Tarif forfaitaire'),
    ('MIXTE', 'Calcul mixte poids/volume');

INSERT INTO zones_livraison (libelle, commune, distance_km, tarif_base, actif) VALUES
    ('Antananarivo Centre', 'Antananarivo', 5.0, 10000, true),
    ('Antananarivo Peripherie', 'Antananarivo', 18.0, 20000, true),
    ('Antsirabe', 'Antsirabe', 170.0, 95000, true),
    ('Toamasina', 'Toamasina', 360.0, 180000, true),
    ('Mahajanga', 'Mahajanga', 560.0, 250000, false);

-- ====================================================================
-- 2. TABLES DEPENDANT DES REFERENCES DE NIVEAU 1
-- ====================================================================

INSERT INTO type_zone (code, libelle, type_produit_id) VALUES
    ('ETA', 'Etagere', 1),
    ('CHF', 'Chambre Froide', 1),
    ('SEC', 'Zone Securisee', 2),
    ('ELE', 'Zone Electronique', 3);

INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, date_creation, actif) VALUES
    ('marie.rasoanaivo@entrepot.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 2, '2026-01-10 08:30:00', true),
    ('paul.andriamahefa@entrepot.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 2, '2026-01-12 09:00:00', true),
    ('hery.rabemananjara@entrepot.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 3, '2026-01-15 09:15:00', true),
    ('sary.rakotondrabe@gmail.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-02-01 10:00:00', true),
    ('nathalie.razafindrakoto@gmail.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-02-03 11:00:00', true),
    ('jean.andriantsoa@gmail.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-02-10 14:00:00', true),
    ('fanja.rasolofo@gmail.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-02-14 16:30:00', true),
    ('tojo.ravelojaona@gmail.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-02-20 08:45:00', true),
    ('zoe.rakotomalala@gmail.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-02-25 09:00:00', true);

INSERT INTO vehicules (immatriculation, marque, modele, annee, capacite_volume_m3, capacite_charge_kg, kilometrage_actuel, type_vehicule_id, statut_vehicule_id) VALUES
    ('1012-TAB', 'Isuzu', 'NPR', 2022, 15.000, 3000.00, 12500.00, 1, 1),
    ('2045-TBC', 'Isuzu', 'NQR', 2021, 20.000, 3500.00, 34210.50, 1, 2),
    ('3078-TAA', 'Toyota', 'Hiace', 2023, 8.000, 1200.00, 8600.00, 2, 1),
    ('4102-TBD', 'Mercedes', 'Sprinter', 2020, 10.000, 1500.00, 45320.00, 2, 3),
    ('5230-TAE', 'Suzuki', 'Carry', 2022, 4.500, 800.00, 15200.75, 3, 1);

INSERT INTO chauffeurs (nom, prenom, telephone, numero_permis, date_expiration_permis, actif) VALUES
    ('Rakoto', 'Jean', '0320000001', 'PERMIS-001', '2028-12-31', true),
    ('Andriamanjato', 'Solo', '0330000002', 'PERMIS-002', '2027-06-15', true),
    ('Rasoamanana', 'Vola', '0340000003', 'PERMIS-003', '2026-08-01', true),
    ('Randria', 'Herizo', '0350000004', 'PERMIS-004', '2025-12-31', false),
    ('Rabe', 'Tiana', '0360000005', 'PERMIS-005', '2029-03-10', true);

INSERT INTO produits (code, nom, description, type_produit_id, volume_unitaire_m3, poids_unitaire_kg, actif) VALUES
    ('PROD-RIZ-001', 'Riz blanc 25kg', 'Sac de riz blanc conditionne 25kg', 1, 0.0500, 25.000, true),
    ('PROD-HUI-002', 'Huile vegetale 5L', 'Bidon huile vegetale 5 litres', 1, 0.0060, 5.000, true),
    ('PROD-CON-003', 'Conserve de poisson', 'Boite de conserve de poisson 400g', 1, 0.0020, 0.400, true),
    ('PROD-CIM-004', 'Ciment 50kg', 'Sac de ciment Portland 50kg', 2, 0.0350, 50.000, true),
    ('PROD-FER-005', 'Fer a beton 12mm (botte)', 'Botte de fer a beton diametre 12mm', 2, 0.0800, 60.000, true),
    ('PROD-PEI-006', 'Peinture batiment 20L', 'Seau de peinture batiment 20 litres', 2, 0.0220, 22.000, true),
    ('PROD-TEL-007', 'Telephone smartphone', 'Smartphone emballe avec accessoires', 3, 0.0008, 0.200, true),
    ('PROD-ORD-008', 'Ordinateur portable', 'Ordinateur portable 15 pouces', 3, 0.0060, 2.500, true),
    ('PROD-TIS-009', 'Tissu coton (rouleau)', 'Rouleau de tissu coton 50m', 4, 0.0450, 15.000, true),
    ('PROD-VET-010', 'Vetements pret-a-porter (carton)', 'Carton de vetements assortis', 4, 0.0600, 12.000, false);

-- ====================================================================
-- 3. TABLES DEPENDANT DES REFERENCES DE NIVEAU 2
-- ====================================================================

INSERT INTO utilisateurs_info (utilisateur_id, nom, prenom, numero, adresse, secteur) VALUES
    (1, 'Admin', 'System', '0320000000', 'Lot II M 45, Antananarivo', 'Direction'),
    (2, 'Rasoanaivo', 'Marie', '0321111111', 'Lot III B 12, Antananarivo', 'Gestion Stock'),
    (3, 'Andriamahefa', 'Paul', '0321111112', 'Lot IV A 20, Antananarivo', 'Gestion Stock'),
    (4, 'Rabemananjara', 'Hery', '0321111113', 'Lot V B 08, Antananarivo', 'Logistique'),
    (5, 'Rakotondrabe', 'Sary', '0331234561', 'Analakely, Antananarivo', 'Commerce'),
    (6, 'Razafindrakoto', 'Nathalie', '0331234562', 'Ivandry, Antananarivo', 'Restauration'),
    (7, 'Andriantsoa', 'Jean', '0331234563', 'Tanambao, Antsirabe', 'BTP'),
    (8, 'Rasolofo', 'Fanja', '0331234564', 'Mahavoky, Antsirabe', 'Textile'),
    (9, 'Ravelojaona', 'Tojo', '0331234565', 'Tanambao V, Toamasina', 'Import-Export');

INSERT INTO zone (id, libelle, volume_total_m3, allees_id, type_zone_id) VALUES
    (1, 'Zone A1 - Alimentaire Sec', 500, 1, 1),
    (2, 'Zone A2 - Chambre Froide', 300, 1, 2),
    (3, 'Zone B1 - Materiaux Industriels', 800, 2, 3),
    (4, 'Zone B2 - Fer et Ciment', 600, 2, 3),
    (5, 'Zone C1 - Electronique Securisee', 200, 3, 4),
    (6, 'Zone C2 - Textile', 350, 3, 1);

INSERT INTO missions_logistiques (reference_mission, date_depart_prevue, date_arrivee_prevue, date_depart_reelle, date_arrivee_reelle, vehicule_id, chauffeur_id, statut_mission_id, observations) VALUES
    ('MIS-2026-001', '2026-07-01 08:00:00', '2026-07-01 12:00:00', '2026-07-01 08:00:00', '2026-07-01 11:55:00', 1, 1, 3, 'Mission terminee sans incident'),
    ('MIS-2026-002', '2026-07-12 07:00:00', '2026-07-12 13:00:00', '2026-07-12 07:30:00', NULL, 2, 2, 2, 'Mission en cours vers Antananarivo Peripherie'),
    ('MIS-2026-003', '2026-07-16 08:00:00', '2026-07-16 15:00:00', NULL, NULL, 3, 3, 1, 'Mission planifiee vers Antsirabe'),
    ('MIS-2026-004', '2026-06-20 09:00:00', '2026-06-20 15:00:00', '2026-06-20 09:00:00', '2026-06-20 14:20:00', 1, 1, 3, 'Livraison materiaux BTP terminee'),
    ('MIS-2026-005', '2026-06-15 06:00:00', '2026-06-15 10:00:00', '2026-06-15 06:00:00', '2026-06-15 09:45:00', 5, 5, 3, 'Livraison express Toamasina terminee'),
    ('MIS-2026-006', '2026-07-13 08:00:00', '2026-07-13 12:00:00', NULL, NULL, 4, 4, 4, 'Mission annulee - vehicule en maintenance');

INSERT INTO maintenances_vehicule (vehicule_id, type_maintenance_id, date_maintenance, kilometrage, cout, description, prochaine_maintenance) VALUES
    (1, 1, '2026-05-10', 11500.00, 150000.00, 'Vidange + filtre a huile', '2026-11-10'),
    (2, 2, '2026-04-15', 30000.00, 850000.00, 'Revision generale 30000 km', '2027-04-15'),
    (3, 3, '2026-06-01', 8000.00, 640000.00, 'Remplacement des 4 pneus', '2027-06-01'),
    (4, 4, '2026-06-20', 44800.00, 320000.00, 'Remplacement plaquettes et disques', '2026-12-20'),
    (4, 2, '2026-07-05', 45300.00, 900000.00, 'Revision generale suite panne moteur', '2027-01-05'),
    (5, 1, '2026-06-25', 15000.00, 120000.00, 'Vidange standard', '2026-12-25');

INSERT INTO tarifs_livraison (zone_livraison_id, mode_calcul_id, prix_base, prix_par_kg, prix_par_m3, date_debut_validite, date_fin_validite) VALUES
    (1, 1, 5000, 500, 20000, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
    (2, 1, 8000, 600, 22000, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
    (3, 2, 40000, 300, 35000, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
    (4, 3, 150000, 0, 0, '2026-01-01 00:00:00', '2026-12-31 23:59:59'),
    (1, 4, 6000, 450, 18000, '2025-01-01 00:00:00', '2025-12-31 23:59:59');

-- ====================================================================
-- 4. TABLES DEPENDANT DES REFERENCES DE NIVEAU 3
-- ====================================================================

INSERT INTO emplacement (id, code, etage_id, capacite_volume_m3, charge_max, colonne,allee_id) VALUES
(1, 'E1', 1, 100.0, 1300, 1,1),
(2, 'E2', 1, 100.0, 1300, 2,1),
(3, 'E3', 1, 100.0, 1300, 3,1),
(4, 'E4', 2, 100.0, 1300, 1,1),
(5, 'E5', 2, 100.0, 1300, 2,1),
(6, 'E6', 2, 100.0, 1300, 3,2),
(7, 'E7', 3, 100.0, 1300, 1,2),
(8, 'E8', 3, 100.0, 1300, 2,2),
(9, 'E9', 3, 100.0, 1300, 3,2),
(10, 'E10', 3, 100.0, 1300, 4,3),
(11, 'E11', 3, 100.0, 1300, 5,3),
(12, 'E12', 2, 100.0, 1300, 4,2);

INSERT INTO historique_vehicule (vehicule_id, mission_id, date_depart, date_arrivee, kilometrage_Depart, kilometrage_arrivee) VALUES
    (1, 1, '2026-07-01 08:00:00', '2026-07-01 11:55:00', 12300.00, 12500.00),
    (2, 2, '2026-07-12 07:30:00', NULL, 34000.00, NULL),
    (1, 4, '2026-06-20 09:00:00', '2026-06-20 14:20:00', 12100.00, 12300.00),
    (5, 5, '2026-06-15 06:00:00', '2026-06-15 09:45:00', 14800.00, 15000.00);

INSERT INTO livraisons (mission_id, client_id, adresse_livraison, zone_livraison_id, poids_total, volume_total, date_prevue, date_livraison, montant_livraison) VALUES
    (1, 5, 'Analakely, Antananarivo', 1, 450.5, 3.2, '2026-07-01 10:00:00', '2026-07-01 11:50:00', 45000),
    (2, 6, 'Ivandry, Antananarivo', 2, 620.0, 4.5, '2026-07-12 09:00:00', NULL, NULL),
    (4, 7, 'Tanambao, Antsirabe', 3, 1200.0, 8.0, '2026-06-20 13:00:00', '2026-06-20 14:15:00', 460000),
    (5, 9, 'Tanambao V, Toamasina', 4, 2500.0, 15.0, '2026-06-15 09:30:00', '2026-06-15 09:40:00', 620000),
    (1, 8, 'Mahavoky, Antsirabe', 3, 300.0, 2.0, '2026-07-01 10:30:00', '2026-07-01 12:00:00', 150000),
    (6, 5, 'Analakely, Antananarivo', 1, 180.0, 1.1, '2026-07-13 08:00:00', NULL, NULL);

INSERT INTO mouvements (code, date_mouvement, type_mouvement_id, statut_mouvement_id, client_id, utilisateur_id, notes) VALUES
    ('MVT-2026-001', '2026-07-01 09:00:00', 1, 2, 5, 7, 'Reception riz et huile'),
    ('MVT-2026-002', '2026-07-02 14:30:00', 2, 2, 5, 7, 'Livraison partielle riz'),
    ('MVT-2026-003', '2026-07-03 10:15:00', 1, 2, 7, 7, 'Reception ciment et fer'),
    ('MVT-2026-004', '2026-07-05 16:00:00', 3, 2, NULL, 7, 'Transfert electronique vers zone securisee'),
    ('MVT-2026-005', '2026-07-08 08:45:00', 2, 1, 8, 7, 'Sortie textile en preparation'),
    ('MVT-2026-006', '2026-07-10 11:20:00', 1, 2, 9, 7, 'Reception electronique'),
    ('MVT-2026-007', '2026-07-11 09:00:00', 4, 2, 6, 7, 'Retour marchandise endommagee'),
    ('MVT-2026-008', '2026-07-13 13:40:00', 2, 3, 5, 7, 'Sortie annulee - erreur client');

-- ====================================================================
-- 5. TABLES DEPENDANT DES REFERENCES DE NIVEAU 4
-- ====================================================================

INSERT INTO stocks_emplacement (emplacement_id, produit_id, zone_id, quantite) VALUES
    (1, 1, 1, 300.000),
    (2, 2, 1, 150.000),
    (3, 3, 2, 200.000),
    (4, 4, 3, 400.000),
    (5, 5, 3, 250.000),
    (6, 6, 4, 180.000),
    (7, 7, 5, 60.000),
    (8, 8, 5, 45.000),
    (9, 9, 6, 220.000),
    (10, 1, 1, 120.000),
    (11, 4, 3, 300.000),
    (12, 7, 5, 30.000);

INSERT INTO lignes_mouvement (mouvement_id, produit_id, emplacement_source_id, emplacement_dest_id, quantite) VALUES
    (1, 1, NULL, 1, 200.000),
    (1, 2, NULL, 2, 100.000),
    (2, 1, 1, NULL, 50.000),
    (3, 4, NULL, 4, 300.000),
    (3, 5, NULL, 5, 150.000),
    (4, 7, 8, 7, 20.000),
    (5, 9, 9, NULL, 40.000),
    (6, 8, NULL, 8, 25.000),
    (6, 7, NULL, 12, 30.000),
    (7, 3, NULL, 3, 10.000),
    (8, 1, 10, NULL, 20.000);

INSERT INTO flux_entrees_sorties (date, type_flux, type_detail, quantite, volume_m3, mouvement_id) VALUES
    ('2026-07-01', 'ENTREE', 'RECEPTION', 200.000, 10.000, 1),
    ('2026-07-01', 'ENTREE', 'RECEPTION', 100.000, 0.600, 1),
    ('2026-07-02', 'SORTIE', 'LIVRAISON', 50.000, 2.500, 2),
    ('2026-07-03', 'ENTREE', 'RECEPTION', 300.000, 10.500, 3),
    ('2026-07-03', 'ENTREE', 'RECEPTION', 150.000, 12.000, 3),
    ('2026-07-05', 'SORTIE', 'TRANSFERT_INTERNE', 20.000, 0.016, 4),
    ('2026-07-10', 'ENTREE', 'RECEPTION', 25.000, 0.150, 6),
    ('2026-07-11', 'ENTREE', 'RETOUR_CLIENT', 10.000, 0.020, 7);

INSERT INTO facturation_livraison (livraison_id, tarif_livraison_id, poids_facture, volume_facture, montant_calcule, montant_final, date_facturation) VALUES
    (1, 1, 450.5, 3.2, 45250.00, 45000.00, '2026-07-01 12:00:00'),
    (2, 2, 620.0, 4.5, 106000.00, NULL, NULL),
    (3, 3, 1200.0, 8.0, 460000.00, 460000.00, '2026-06-20 14:20:00'),
    (4, 4, 2500.0, 15.0, 620000.00, 620000.00, '2026-06-15 09:45:00'),
    (5, 3, 300.0, 2.0, 150500.00, 150000.00, '2026-07-01 12:05:00'),
    (6, 1, 180.0, 1.1, 35000.00, NULL, NULL);

INSERT INTO preuves_livraison (livraison_id, date_validation, signature_client, photo_colis, commentaire) VALUES
    (1, '2026-07-01 11:52:00', 'signature_client_001.png', 'photo_colis_001.jpg', 'Colis recu en bon etat'),
    (3, '2026-06-20 14:16:00', 'signature_client_003.png', 'photo_colis_003.jpg', 'Livraison conforme'),
    (4, '2026-06-15 09:42:00', 'signature_client_004.png', 'photo_colis_004.jpg', 'RAS'),
    (5, '2026-07-01 12:01:00', 'signature_client_005.png', 'photo_colis_005.jpg', 'Client absent, colis remis au gardien');

-- ====================================================================
-- 6. STATISTIQUES ET AGREGATS
-- ====================================================================

INSERT INTO stats_clients (date_debut, date_fin, volume_stocke_m3, duree_moyenne_jours, nb_entrees, nb_sorties, chiffre_affaires, client_id) VALUES
    ('2026-07-01', '2026-07-31', 12.500, 8.50, 3, 2, 230000.00, 5),
    ('2026-07-01', '2026-07-31', 8.200, 6.00, 2, 1, 106000.00, 6),
    ('2026-07-01', '2026-07-31', 20.000, 10.00, 4, 1, 460000.00, 7),
    ('2026-07-01', '2026-07-31', 5.500, 4.00, 1, 1, 150000.00, 8),
    ('2026-07-01', '2026-07-31', 15.000, 5.00, 2, 1, 620000.00, 9);

INSERT INTO top_produits (date_snapshot, rang, quantite_totale, duree_moyenne_stockage_jours, produit_id) VALUES
    ('2026-07-13', 1, 300.000, 7.50, 1),
    ('2026-07-13', 2, 250.000, 5.00, 5),
    ('2026-07-13', 3, 180.000, 6.20, 9),
    ('2026-07-13', 4, 150.000, 4.00, 2),
    ('2026-07-13', 5, 90.000, 3.00, 7);
