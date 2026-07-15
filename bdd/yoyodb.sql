-- ============================================================================
-- yoyodb.sql — Script complet : nettoyage + schéma + données de test
-- Fusion de final2.sql, data_final.sql, yoann_data.sql + données massives
-- ============================================================================

-- ============================================================================
-- NETTOYAGE COMPLET
-- ============================================================================

DROP VIEW IF EXISTS v_clients CASCADE;

DROP TABLE IF EXISTS lignes_mouvement CASCADE;
DROP TABLE IF EXISTS mouvements CASCADE;
DROP TABLE IF EXISTS statuts_mouvement CASCADE;
DROP TABLE IF EXISTS types_mouvement CASCADE;

DROP TABLE IF EXISTS stocks_emplacement CASCADE;

DROP TABLE IF EXISTS missions_logistiques CASCADE;
DROP TABLE IF EXISTS chauffeurs CASCADE;
DROP TABLE IF EXISTS vehicules CASCADE;
DROP TABLE IF EXISTS statuts_mission CASCADE;
DROP TABLE IF EXISTS statuts_vehicule CASCADE;
DROP TABLE IF EXISTS types_vehicule CASCADE;

DROP TABLE IF EXISTS abonnements_stockage CASCADE;
DROP TABLE IF EXISTS factures CASCADE;
DROP TABLE IF EXISTS tarifs_zone CASCADE;
DROP TABLE IF EXISTS unites_duree CASCADE;

DROP TABLE IF EXISTS renouvellements_contrat CASCADE;
DROP TABLE IF EXISTS historique_renouvellement CASCADE;
DROP TABLE IF EXISTS demandes_renouvellement CASCADE;
DROP TABLE IF EXISTS statuts_renouvellement CASCADE;

DROP TABLE IF EXISTS contrats CASCADE;
DROP TABLE IF EXISTS demandes_stockage CASCADE;
DROP TABLE IF EXISTS statuts_demande_stockage CASCADE;
DROP TABLE IF EXISTS types_contrat CASCADE;

DROP TABLE IF EXISTS historiques_etat_demande CASCADE;
DROP TABLE IF EXISTS historique_etat_demande CASCADE;

DROP TABLE IF EXISTS emplacement CASCADE;
DROP TABLE IF EXISTS colonne CASCADE;
DROP TABLE IF EXISTS etage CASCADE;
DROP TABLE IF EXISTS zone CASCADE;
DROP TABLE IF EXISTS allee CASCADE;
DROP TABLE IF EXISTS allees CASCADE;

DROP TABLE IF EXISTS produits CASCADE;
DROP TABLE IF EXISTS type_produit CASCADE;
DROP TABLE IF EXISTS types_produits CASCADE;

DROP TABLE IF EXISTS type_zone CASCADE;
DROP TABLE IF EXISTS types_zone CASCADE;

DROP TABLE IF EXISTS utilisateurs_info CASCADE;
DROP TABLE IF EXISTS utilisateurs CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

DROP FUNCTION IF EXISTS fn_check_ligne_mouvement() CASCADE;
DROP FUNCTION IF EXISTS fn_appliquer_mouvement_valide() CASCADE;

-- ============================================================================
-- 1. RÔLES ET UTILISATEURS
-- ============================================================================

CREATE TABLE roles (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE utilisateurs (
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role_id          BIGINT NOT NULL,
    date_creation    TIMESTAMP NOT NULL DEFAULT now(),
    actif            BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_utilisateur_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE utilisateurs_info (
    id             BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    nom            VARCHAR(100) NOT NULL,
    prenom         VARCHAR(100),
    numero         VARCHAR(30),
    adresse        VARCHAR(255),
    secteur        VARCHAR(100),
    CONSTRAINT fk_utilisateur_info FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

CREATE VIEW v_clients AS
SELECT u.id, u.email, ui.nom, ui.prenom, ui.numero, ui.adresse, ui.secteur
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id
LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
WHERE r.code = 'CLIENT';

-- ============================================================================
-- 2. PRODUITS
-- ============================================================================

CREATE TABLE types_produits (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(40) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE type_produit (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(40) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE produits (
    id                BIGSERIAL PRIMARY KEY,
    code              VARCHAR(40) UNIQUE NOT NULL,
    nom               VARCHAR(150) NOT NULL,
    description       TEXT,
    type_produit_id   BIGINT,
    volume_unitaire_m3 NUMERIC(10,4) NOT NULL CHECK (volume_unitaire_m3 > 0),
    poids_unitaire_kg NUMERIC(10,3),
    actif             BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (type_produit_id) REFERENCES type_produit(id)
);

-- ============================================================================
-- 3. ZONES DE STOCKAGE
-- ============================================================================

CREATE TABLE types_zone (
    id                     BIGSERIAL PRIMARY KEY,
    code                   VARCHAR(20) UNIQUE NOT NULL,
    libelle                VARCHAR(100) NOT NULL,
    type_produit_id        BIGINT,
    controle_temperature   BOOLEAN DEFAULT FALSE,
    acces_restreint        BOOLEAN DEFAULT FALSE,
    journalisation_acces   BOOLEAN DEFAULT FALSE,
    charge_lourde_possible BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (type_produit_id) REFERENCES types_produits(id)
);

CREATE TABLE type_zone (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20) UNIQUE NOT NULL,
    libelle         VARCHAR(100) NOT NULL,
    type_produit_id BIGINT,
    FOREIGN KEY (type_produit_id) REFERENCES type_produit(id)
);

CREATE TABLE allees (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE allee (
    id      BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);

CREATE TABLE etage (
    id           BIGSERIAL PRIMARY KEY,
    libelle      VARCHAR(100),
    numero_etage INT
);

CREATE TABLE colonne (
    id      BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);

CREATE TABLE zone (
    id             BIGSERIAL PRIMARY KEY,
    libelle        VARCHAR(150),
    volume_total_m3 NUMERIC(12,3) CHECK (volume_total_m3 >= 0),
    allees_id      BIGINT,
    type_zone_id   BIGINT,
    FOREIGN KEY (allees_id) REFERENCES allees(id),
    FOREIGN KEY (type_zone_id) REFERENCES type_zone(id)
);

CREATE TABLE emplacement (
    id                 BIGSERIAL PRIMARY KEY,
    code               VARCHAR(40) UNIQUE NOT NULL,
    etage_id           BIGINT,
    allee_id           BIGINT,
    capacite_volume_m3 NUMERIC(10,3) NOT NULL CHECK (capacite_volume_m3 > 0),
    actif              BOOLEAN DEFAULT TRUE,
    charge_max         DOUBLE PRECISION,
    colonne            INT,
    FOREIGN KEY (etage_id) REFERENCES etage(id),
    FOREIGN KEY (allee_id) REFERENCES allee(id)
);

-- ============================================================================
-- 4. CONTRATS / DEMANDES STOCKAGE
-- ============================================================================

CREATE TABLE types_contrat (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE statuts_demande_stockage (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_stockage (
    id                  BIGSERIAL PRIMARY KEY,
    utilisateur_id      BIGINT NOT NULL,
    type_zone_id        BIGINT NOT NULL,
    type_contrat_id     BIGINT NOT NULL,
    volume_espace_m3    NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    quantite_emplacement INTEGER NOT NULL DEFAULT 1 CHECK (quantite_emplacement > 0),
    date_debut          DATE NOT NULL,
    date_fin            DATE,
    duree_mois          INTEGER DEFAULT NULL,
    FOREIGN KEY (utilisateur_id)  REFERENCES utilisateurs(id),
    FOREIGN KEY (type_zone_id)    REFERENCES types_zone(id),
    FOREIGN KEY (type_contrat_id) REFERENCES types_contrat(id)
);

CREATE TABLE contrats (
    id                    BIGSERIAL PRIMARY KEY,
    demande_stockage_id   BIGINT NOT NULL,
    utilisateur_id        BIGINT NOT NULL,
    type_zone_id          BIGINT NOT NULL,
    type_contrat_id       BIGINT NOT NULL,
    volume_espace_m3      NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    quantite_emplacement  INTEGER NOT NULL DEFAULT 1 CHECK (quantite_emplacement > 0),
    date_creation         TIMESTAMP DEFAULT now(),
    date_debut            DATE NOT NULL,
    date_fin              DATE,
    description           TEXT,
    duree_mois            INTEGER DEFAULT NULL,
    CHECK (date_fin IS NULL OR date_fin >= date_debut),
    FOREIGN KEY (demande_stockage_id) REFERENCES demandes_stockage(id),
    FOREIGN KEY (utilisateur_id)      REFERENCES utilisateurs(id),
    FOREIGN KEY (type_zone_id)        REFERENCES types_zone(id),
    FOREIGN KEY (type_contrat_id)     REFERENCES types_contrat(id)
);

-- ============================================================================
-- 5. RENOUVELLEMENTS
-- ============================================================================

CREATE TABLE statuts_renouvellement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_renouvellement (
    id          BIGSERIAL PRIMARY KEY,
    contrat_id  BIGINT NOT NULL,
    date_demande DATE NOT NULL,
    date_fin    DATE,
    FOREIGN KEY (contrat_id) REFERENCES contrats(id)
);

CREATE TABLE historique_renouvellement (
    id                        BIGSERIAL PRIMARY KEY,
    demande_renouvellement_id BIGINT NOT NULL,
    statut_renouvellement_id  BIGINT NOT NULL,
    date_statut               TIMESTAMP DEFAULT now(),
    FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement(id),
    FOREIGN KEY (statut_renouvellement_id)  REFERENCES statuts_renouvellement(id)
);

CREATE TABLE renouvellements_contrat (
    id                        BIGSERIAL PRIMARY KEY,
    contrat_id                BIGINT NOT NULL,
    demande_renouvellement_id BIGINT NOT NULL,
    date_renouvellement       DATE NOT NULL,
    date_fin                  DATE,
    FOREIGN KEY (contrat_id)                REFERENCES contrats(id),
    FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement(id)
);

-- ============================================================================
-- 6. FACTURATION
-- ============================================================================

CREATE TABLE unites_duree (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE tarifs_zone (
    id                BIGSERIAL PRIMARY KEY,
    type_zone_id      BIGINT NOT NULL,
    unite_duree_id    BIGINT NOT NULL,
    prix_m3           NUMERIC(12,2) NOT NULL CHECK (prix_m3 >= 0),
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    FOREIGN KEY (type_zone_id)   REFERENCES types_zone(id),
    FOREIGN KEY (unite_duree_id) REFERENCES unites_duree(id)
);

CREATE TABLE factures (
    id               BIGSERIAL PRIMARY KEY,
    contrat_id       BIGINT NOT NULL,
    volume_espace_m3 NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    prix_facture     NUMERIC(14,2) NOT NULL CHECK (prix_facture >= 0),
    date_emission    DATE DEFAULT CURRENT_DATE,
    date_paiement    DATE,
    FOREIGN KEY (contrat_id) REFERENCES contrats(id)
);

CREATE TABLE abonnements_stockage (
    id             BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    contrat_id     BIGINT UNIQUE NOT NULL,
    type_zone_id   BIGINT NOT NULL,
    duree_mois     INTEGER NOT NULL CHECK (duree_mois > 0),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
    FOREIGN KEY (contrat_id)     REFERENCES contrats(id),
    FOREIGN KEY (type_zone_id)   REFERENCES types_zone(id)
);

-- ============================================================================
-- 7. STOCKS
-- ============================================================================

CREATE TABLE stocks_emplacement (
    id             BIGSERIAL PRIMARY KEY,
    emplacement_id BIGINT NOT NULL,
    produit_id     BIGINT NOT NULL,
    quantite       NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantite >= 0),
    UNIQUE (emplacement_id, produit_id),
    FOREIGN KEY (emplacement_id) REFERENCES emplacement(id),
    FOREIGN KEY (produit_id)     REFERENCES produits(id)
);

-- ============================================================================
-- 8. LOGISTIQUE
-- ============================================================================

CREATE TABLE types_vehicule (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(30) UNIQUE NOT NULL,
    libelle     VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE statuts_vehicule (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE vehicules (
    id                  BIGSERIAL PRIMARY KEY,
    immatriculation     VARCHAR(20) UNIQUE NOT NULL,
    marque              VARCHAR(60),
    modele              VARCHAR(60),
    annee               INT,
    capacite_volume_m3  NUMERIC(10,3) NOT NULL CHECK (capacite_volume_m3 > 0),
    capacite_charge_kg  NUMERIC(10,2) NOT NULL CHECK (capacite_charge_kg > 0),
    kilometrage_actuel  NUMERIC(10,2) DEFAULT 0,
    type_vehicule_id    BIGINT NOT NULL,
    statut_vehicule_id  BIGINT NOT NULL,
    FOREIGN KEY (type_vehicule_id)   REFERENCES types_vehicule(id),
    FOREIGN KEY (statut_vehicule_id) REFERENCES statuts_vehicule(id)
);

CREATE TABLE chauffeurs (
    id                      BIGSERIAL PRIMARY KEY,
    nom                     VARCHAR(100) NOT NULL,
    prenom                  VARCHAR(100),
    telephone               VARCHAR(30),
    numero_permis           VARCHAR(40) UNIQUE NOT NULL,
    date_expiration_permis  DATE,
    actif                   BOOLEAN DEFAULT TRUE
);

CREATE TABLE statuts_mission (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE missions_logistiques (
    id                  BIGSERIAL PRIMARY KEY,
    reference_mission   VARCHAR(40) UNIQUE NOT NULL,
    date_depart_prevue  TIMESTAMP,
    date_arrivee_prevue TIMESTAMP,
    date_depart_reelle  TIMESTAMP,
    date_arrivee_reelle TIMESTAMP,
    vehicule_id         BIGINT NOT NULL,
    chauffeur_id        BIGINT NOT NULL,
    statut_mission_id   BIGINT NOT NULL,
    observations        TEXT,
    FOREIGN KEY (vehicule_id)  REFERENCES vehicules(id),
    FOREIGN KEY (chauffeur_id) REFERENCES chauffeurs(id),
    FOREIGN KEY (statut_mission_id) REFERENCES statuts_mission(id)
);

-- ============================================================================
-- 9. MOUVEMENTS
-- ============================================================================

CREATE TABLE types_mouvement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    sens    VARCHAR(10) NOT NULL CHECK (sens IN ('ENTREE', 'SORTIE'))
);

CREATE TABLE statuts_mouvement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(50) NOT NULL,
    ordre   INT NOT NULL
);

CREATE TABLE mouvements (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(30) UNIQUE NOT NULL,
    date_mouvement      TIMESTAMP DEFAULT now(),
    type_mouvement_id   BIGINT NOT NULL,
    statut_mouvement_id BIGINT NOT NULL,
    client_id           BIGINT,
    utilisateur_id      BIGINT NOT NULL,
    notes               TEXT,
    FOREIGN KEY (type_mouvement_id)   REFERENCES types_mouvement(id),
    FOREIGN KEY (statut_mouvement_id) REFERENCES statuts_mouvement(id),
    FOREIGN KEY (client_id)           REFERENCES utilisateurs(id),
    FOREIGN KEY (utilisateur_id)      REFERENCES utilisateurs(id)
);

CREATE TABLE lignes_mouvement (
    id                    BIGSERIAL PRIMARY KEY,
    mouvement_id          BIGINT NOT NULL,
    produit_id            BIGINT NOT NULL,
    emplacement_source_id BIGINT,
    emplacement_dest_id   BIGINT,
    quantite              NUMERIC(12,3) NOT NULL CHECK (quantite > 0),
    FOREIGN KEY (mouvement_id)            REFERENCES mouvements(id),
    FOREIGN KEY (produit_id)              REFERENCES produits(id),
    FOREIGN KEY (emplacement_source_id)   REFERENCES emplacement(id),
    FOREIGN KEY (emplacement_dest_id)     REFERENCES emplacement(id)
);

-- ============================================================================
-- FONCTIONS & TRIGGERS
-- ============================================================================

CREATE OR REPLACE FUNCTION fn_check_ligne_mouvement()
RETURNS TRIGGER AS $$
DECLARE
    v_sens            VARCHAR(10);
    v_code_type       VARCHAR(30);
    v_volume_unitaire NUMERIC(10,4);
    v_volume_occupe   NUMERIC(14,3);
    v_capacite        NUMERIC(10,3);
BEGIN
    SELECT tm.sens, tm.code INTO v_sens, v_code_type
    FROM mouvements m
    JOIN types_mouvement tm ON tm.id = m.type_mouvement_id
    WHERE m.id = NEW.mouvement_id;

    IF v_code_type = 'TRANSFERT_INTERNE' THEN
        IF NEW.emplacement_source_id IS NULL OR NEW.emplacement_dest_id IS NULL THEN
            RAISE EXCEPTION 'Transfert interne : emplacement source et destination obligatoires';
        END IF;
        IF NEW.emplacement_source_id = NEW.emplacement_dest_id THEN
            RAISE EXCEPTION 'Transfert interne : la source et la destination doivent etre differentes';
        END IF;
    ELSIF v_sens = 'ENTREE' THEN
        IF NEW.emplacement_source_id IS NOT NULL THEN
            RAISE EXCEPTION 'Mouvement ENTREE : emplacement_source_id doit etre NULL';
        END IF;
        IF NEW.emplacement_dest_id IS NULL THEN
            RAISE EXCEPTION 'Mouvement ENTREE : emplacement_dest_id est obligatoire';
        END IF;
    ELSIF v_sens = 'SORTIE' THEN
        IF NEW.emplacement_dest_id IS NOT NULL THEN
            RAISE EXCEPTION 'Mouvement SORTIE : emplacement_dest_id doit etre NULL';
        END IF;
        IF NEW.emplacement_source_id IS NULL THEN
            RAISE EXCEPTION 'Mouvement SORTIE : emplacement_source_id est obligatoire';
        END IF;
    END IF;

    IF NEW.emplacement_dest_id IS NOT NULL THEN
        SELECT volume_unitaire_m3 INTO v_volume_unitaire FROM produits WHERE id = NEW.produit_id;
        SELECT capacite_volume_m3 INTO v_capacite FROM emplacement WHERE id = NEW.emplacement_dest_id;
        SELECT COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) INTO v_volume_occupe
        FROM stocks_emplacement se
        JOIN produits p ON p.id = se.produit_id
        WHERE se.emplacement_id = NEW.emplacement_dest_id;

        IF v_volume_occupe + (NEW.quantite * v_volume_unitaire) > v_capacite THEN
            RAISE EXCEPTION 'Capacite insuffisante sur l''emplacement de destination';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_ligne_mouvement
    BEFORE INSERT OR UPDATE ON lignes_mouvement
    FOR EACH ROW EXECUTE FUNCTION fn_check_ligne_mouvement();

CREATE OR REPLACE FUNCTION fn_appliquer_mouvement_valide()
RETURNS TRIGGER AS $$
DECLARE
    v_code_statut_avant VARCHAR(20);
    v_code_statut_apres VARCHAR(20);
    ligne RECORD;
BEGIN
    SELECT code INTO v_code_statut_apres FROM statuts_mouvement WHERE id = NEW.statut_mouvement_id;
    IF TG_OP = 'UPDATE' THEN
        SELECT code INTO v_code_statut_avant FROM statuts_mouvement WHERE id = OLD.statut_mouvement_id;
    ELSE
        v_code_statut_avant := NULL;
    END IF;

    IF v_code_statut_apres = 'VALIDE' AND v_code_statut_avant IS DISTINCT FROM 'VALIDE' THEN
        FOR ligne IN SELECT * FROM lignes_mouvement WHERE mouvement_id = NEW.id LOOP
            IF ligne.emplacement_source_id IS NOT NULL THEN
                UPDATE stocks_emplacement
                SET quantite = quantite - ligne.quantite
                WHERE emplacement_id = ligne.emplacement_source_id AND produit_id = ligne.produit_id;
                IF NOT FOUND THEN
                    RAISE EXCEPTION 'Stock introuvable pour le produit % a l''emplacement %', ligne.produit_id, ligne.emplacement_source_id;
                END IF;
            END IF;
            IF ligne.emplacement_dest_id IS NOT NULL THEN
                INSERT INTO stocks_emplacement (emplacement_id, produit_id, quantite)
                VALUES (ligne.emplacement_dest_id, ligne.produit_id, ligne.quantite)
                ON CONFLICT (emplacement_id, produit_id)
                DO UPDATE SET quantite = stocks_emplacement.quantite + ligne.quantite;
            END IF;
        END LOOP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_appliquer_mouvement_valide
    AFTER INSERT OR UPDATE ON mouvements
    FOR EACH ROW EXECUTE FUNCTION fn_appliquer_mouvement_valide();

-- ============================================================================
-- DONNÉES PARAMÈTRES (existantes)
-- ============================================================================

INSERT INTO roles (code, libelle) VALUES
('ADMIN', 'Administrateur'),
('GESTIONNAIRE', 'Gestionnaire entrepot'),
('RESPONSABLE_LOGISTIQUE', 'Responsable logistique'),
('COMPTABLE', 'Comptable'),
('CLIENT', 'Client');

INSERT INTO types_contrat (code, libelle) VALUES
('ABONNE', 'Contrat abonnement'),
('NON_ABONNE', 'Stockage ponctuel sans abonnement');

INSERT INTO statuts_demande_stockage (code, libelle) VALUES
('EN_ATTENTE', 'Demande en attente de traitement'),
('ACCEPTEE', 'Demande acceptée'),
('REFUSEE', 'Demande refusée');

INSERT INTO statuts_renouvellement (code, libelle) VALUES
('EN_ATTENTE', 'Renouvellement en attente'),
('ACCEPTEE', 'Renouvellement accepté'),
('REFUSEE', 'Renouvellement refusé');

INSERT INTO types_produits (code, libelle) VALUES
('ALIMENTAIRE', 'Produit alimentaire'),
('INDUSTRIEL', 'Produit industriel'),
('SENSIBLE', 'Produit sensible'),
('VALEUR', 'Produit de valeur');

INSERT INTO type_produit (code, libelle) VALUES
('ALIMENTAIRE', 'Produit alimentaire'),
('INDUSTRIEL', 'Produit industriel'),
('SENSIBLE', 'Produit sensible'),
('VALEUR', 'Produit de valeur');

INSERT INTO types_zone (code, libelle, controle_temperature, acces_restreint, journalisation_acces, charge_lourde_possible) VALUES
('ETA', 'Etagère classique',       FALSE, FALSE, FALSE, FALSE),
('CHF', 'Frigo / Chambre froide',  TRUE,  FALSE, FALSE, FALSE),
('SEC', 'Zone sécurisée',          FALSE, TRUE,  TRUE,  FALSE),
('SOL', 'Zone au sol',             FALSE, FALSE, FALSE, TRUE);

INSERT INTO type_zone (code, libelle, type_produit_id) VALUES
('ETA', 'Etagère classique',      2),
('CHF', 'Frigo / Chambre froide', 1),
('SEC', 'Zone sécurisée',         4),
('SOL', 'Zone au sol',            2);

INSERT INTO types_mouvement (code, libelle, sens) VALUES
('RETOUR_CLIENT',     'Retour client',            'ENTREE'),
('RECEPTION',         'Réception',                'ENTREE'),
('LIVRAISON_CLIENT',  'Livraison client',         'SORTIE'),
('TRANSFERT_INTERNE', 'Transfert interne',        'SORTIE'),
('PERTE_DESTRUCTION', 'Perte / Destruction',      'SORTIE'),
('EXPEDITION',        'Expédition',               'SORTIE');

INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
('EN_ATTENTE',  'En attente',   1),
('EN_CONTROLE', 'En contrôle',  2),
('VALIDE',      'Valide',       3),
('EXPEDIE',     'Expédié',      4),
('ANNULE',      'Annulé',       5);

INSERT INTO types_vehicule (code, libelle, description) VALUES
('CAMION',    'Camion',       'Véhicule lourd pour transport de marchandises'),
('UTILITAIRE','Utilitaire',   'Véhicule utilitaire léger'),
('FOURGON',   'Fourgonnette', 'Fourgonnette moyenne capacité'),
('REMORQUE',  'Remorque',     'Remorque tractable');

INSERT INTO statuts_vehicule (code, libelle) VALUES
('DISPONIBLE',     'Disponible'),
('EN_MISSION',     'En mission'),
('EN_MAINTENANCE', 'En maintenance'),
('HORS_SERVICE',   'Hors service');

INSERT INTO statuts_mission (code, libelle) VALUES
('PLANIFIEE', 'Planifiée'),
('EN_COURS',  'En cours'),
('TERMINEE',  'Terminée'),
('ANNULEE',   'Annulée');

INSERT INTO unites_duree (code, libelle) VALUES
('JOUR', 'Jour'),
('MOIS', 'Mois');

INSERT INTO tarifs_zone (type_zone_id, unite_duree_id, prix_m3, date_debut_validite) VALUES
(1, 1, 1500,  '2026-01-01'),
(1, 2, 35000, '2026-01-01'),
(2, 1, 1500,  '2026-01-01'),
(2, 2, 35000, '2026-01-01'),
(3, 1, 1000,  '2026-01-01'),
(3, 2, 20000, '2026-01-01'),
(4, 1, 1000,  '2026-01-01'),
(4, 2, 20000, '2026-01-01');