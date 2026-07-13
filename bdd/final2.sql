-- ============================================================================
-- NETTOYAGE COMPLET BASE ENTREPOT
-- ============================================================================

-- ============================================================================
-- NETTOYAGE COMPLET (DROPS)
-- ============================================================================

-- 1. VUES
DROP VIEW IF EXISTS v_clients CASCADE;

-- 2. TABLES (Ordre respectant les contraintes de clés étrangères)
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

-- 3. FONCTIONS ET TRIGGERS
DROP FUNCTION IF EXISTS fn_check_ligne_mouvement() CASCADE;
DROP FUNCTION IF EXISTS fn_appliquer_mouvement_valide() CASCADE;

-- ============================================================================
-- 1. UTILISATEURS
-- ============================================================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);


CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT now(),
    actif BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_utilisateur_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
);


CREATE TABLE utilisateurs_info (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,

    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    numero VARCHAR(30),
    adresse VARCHAR(255),
    secteur VARCHAR(100),

    CONSTRAINT fk_utilisateur_info
        FOREIGN KEY(utilisateur_id)
        REFERENCES utilisateurs(id)
        ON DELETE CASCADE
);


CREATE VIEW v_clients AS
SELECT
    u.id,
    u.email,
    ui.nom,
    ui.prenom,
    ui.numero,
    ui.adresse,
    ui.secteur
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id
LEFT JOIN utilisateurs_info ui
ON ui.utilisateur_id = u.id
WHERE r.code='CLIENT';



-- ============================================================================
-- 2. PRODUITS
-- ============================================================================

CREATE TABLE types_produits (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL
);


CREATE TABLE type_produit (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) UNIQUE NOT NULL,
    libelle VARCHAR(150) NOT NULL
);


CREATE TABLE produits (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(40) UNIQUE NOT NULL,
    nom VARCHAR(150) NOT NULL,

    description TEXT,

    type_produit_id BIGINT,

    volume_unitaire_m3 NUMERIC(10,4)
        NOT NULL CHECK(volume_unitaire_m3 > 0),

    poids_unitaire_kg NUMERIC(10,3),

    actif BOOLEAN DEFAULT TRUE,

    FOREIGN KEY(type_produit_id)
        REFERENCES type_produit(id)
);



-- ============================================================================
-- 3. ZONES
-- ============================================================================

CREATE TABLE types_zone (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(100) NOT NULL,

    type_produit_id BIGINT,

    controle_temperature BOOLEAN DEFAULT FALSE,
    acces_restreint BOOLEAN DEFAULT FALSE,
    journalisation_acces BOOLEAN DEFAULT FALSE,
    charge_lourde_possible BOOLEAN DEFAULT FALSE,

    FOREIGN KEY(type_produit_id)
        REFERENCES types_produits(id)
);



CREATE TABLE type_zone (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(100) NOT NULL,

    type_produit_id BIGINT,

    FOREIGN KEY(type_produit_id)
        REFERENCES type_produit(id)
);



CREATE TABLE allees (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL
);


CREATE TABLE allee (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);


CREATE TABLE etage (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100),
    numero_etage INT
);


CREATE TABLE colonne (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);



CREATE TABLE zone (
    id BIGSERIAL PRIMARY KEY,

    libelle VARCHAR(150),

    volume_total_m3 NUMERIC(12,3)
        CHECK(volume_total_m3 >=0),

    allees_id BIGINT,

    type_zone_id BIGINT,

    FOREIGN KEY(allees_id)
        REFERENCES allees(id),

    FOREIGN KEY(type_zone_id)
        REFERENCES type_zone(id)
);



CREATE TABLE emplacement (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(40) UNIQUE NOT NULL,

    etage_id BIGINT,

    allee_id BIGINT,

    capacite_volume_m3 NUMERIC(10,3)
        NOT NULL CHECK(capacite_volume_m3>0),

    actif BOOLEAN DEFAULT TRUE,

    charge_max DOUBLE PRECISION,

    colonne INT,

    FOREIGN KEY(etage_id)
        REFERENCES etage(id),

    FOREIGN KEY(allee_id)
        REFERENCES allee(id)
);



-- ============================================================================
-- 4. CONTRATS / DEMANDES STOCKAGE
-- ============================================================================

CREATE TABLE types_contrat (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(100) NOT NULL
);



CREATE TABLE statuts_demande_stockage (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(100) NOT NULL
);



CREATE TABLE demandes_stockage (

    id BIGSERIAL PRIMARY KEY,

    utilisateur_id BIGINT NOT NULL,

    type_zone_id BIGINT NOT NULL,

    type_contrat_id BIGINT NOT NULL,


    volume_espace_m3 NUMERIC(10,3)
        NOT NULL CHECK(volume_espace_m3 >0),


    quantite_emplacement INTEGER NOT NULL DEFAULT 1
        CHECK(quantite_emplacement >0),


    date_debut DATE NOT NULL,

    date_fin DATE,


    FOREIGN KEY(utilisateur_id)
        REFERENCES utilisateurs(id),

    FOREIGN KEY(type_zone_id)
        REFERENCES types_zone(id),

    FOREIGN KEY(type_contrat_id)
        REFERENCES types_contrat(id)

);



CREATE TABLE contrats (

    id BIGSERIAL PRIMARY KEY,

    demande_stockage_id BIGINT NOT NULL,

    utilisateur_id BIGINT NOT NULL,

    type_zone_id BIGINT NOT NULL,

    type_contrat_id BIGINT NOT NULL,


    volume_espace_m3 NUMERIC(10,3)
        NOT NULL CHECK(volume_espace_m3>0),


    quantite_emplacement INTEGER NOT NULL DEFAULT 1
        CHECK(quantite_emplacement>0),


    date_creation TIMESTAMP DEFAULT now(),

    date_debut DATE NOT NULL,

    date_fin DATE,


    description TEXT,


    CHECK(date_fin IS NULL OR date_fin>=date_debut),


    FOREIGN KEY(demande_stockage_id)
        REFERENCES demandes_stockage(id),


    FOREIGN KEY(utilisateur_id)
        REFERENCES utilisateurs(id),


    FOREIGN KEY(type_zone_id)
        REFERENCES types_zone(id),


    FOREIGN KEY(type_contrat_id)
        REFERENCES types_contrat(id)

);

-- ============================================================================
-- 5. RENOUVELLEMENTS
-- ============================================================================

CREATE TABLE statuts_renouvellement (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    libelle VARCHAR(100) NOT NULL
);


CREATE TABLE demandes_renouvellement (
    id BIGSERIAL PRIMARY KEY,

    contrat_id BIGINT NOT NULL,

    date_demande DATE NOT NULL,

    date_fin DATE,

    FOREIGN KEY(contrat_id)
        REFERENCES contrats(id)
);


CREATE TABLE historique_renouvellement (
    id BIGSERIAL PRIMARY KEY,

    demande_renouvellement_id BIGINT NOT NULL,

    statut_renouvellement_id BIGINT NOT NULL,

    date_statut TIMESTAMP DEFAULT now(),

    FOREIGN KEY(demande_renouvellement_id)
        REFERENCES demandes_renouvellement(id),

    FOREIGN KEY(statut_renouvellement_id)
        REFERENCES statuts_renouvellement(id)
);



CREATE TABLE renouvellements_contrat (
    id BIGSERIAL PRIMARY KEY,

    contrat_id BIGINT NOT NULL,

    demande_renouvellement_id BIGINT NOT NULL,

    date_renouvellement DATE NOT NULL,

    date_fin DATE,

    FOREIGN KEY(contrat_id)
        REFERENCES contrats(id),

    FOREIGN KEY(demande_renouvellement_id)
        REFERENCES demandes_renouvellement(id)
);



-- ============================================================================
-- 6. FACTURATION
-- ============================================================================

CREATE TABLE unites_duree (
    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(50) NOT NULL
);



CREATE TABLE tarifs_zone (
    id BIGSERIAL PRIMARY KEY,

    type_zone_id BIGINT NOT NULL,

    unite_duree_id BIGINT NOT NULL,

    prix_m3 NUMERIC(12,2)
        NOT NULL CHECK(prix_m3>=0),

    date_debut_validite DATE NOT NULL,

    date_fin_validite DATE,


    FOREIGN KEY(type_zone_id)
        REFERENCES types_zone(id),


    FOREIGN KEY(unite_duree_id)
        REFERENCES unites_duree(id)

);



CREATE TABLE factures (
    id BIGSERIAL PRIMARY KEY,

    contrat_id BIGINT NOT NULL,


    volume_espace_m3 NUMERIC(10,3)
        NOT NULL CHECK(volume_espace_m3>0),


    prix_facture NUMERIC(14,2)
        NOT NULL CHECK(prix_facture>=0),


    date_emission DATE DEFAULT CURRENT_DATE,

    date_paiement DATE,


    FOREIGN KEY(contrat_id)
        REFERENCES contrats(id)
);



CREATE TABLE abonnements_stockage (

    id BIGSERIAL PRIMARY KEY,


    utilisateur_id BIGINT NOT NULL,


    contrat_id BIGINT UNIQUE NOT NULL,


    type_zone_id BIGINT NOT NULL,


    duree_mois INTEGER NOT NULL
        CHECK(duree_mois>0),


    FOREIGN KEY(utilisateur_id)
        REFERENCES utilisateurs(id),


    FOREIGN KEY(contrat_id)
        REFERENCES contrats(id),


    FOREIGN KEY(type_zone_id)
        REFERENCES types_zone(id)

);



-- ============================================================================
-- 7. STOCKS
-- ============================================================================

CREATE TABLE stocks_emplacement (

    id BIGSERIAL PRIMARY KEY,


    emplacement_id BIGINT NOT NULL,


    produit_id BIGINT NOT NULL,


    quantite NUMERIC(12,3)
        NOT NULL DEFAULT 0
        CHECK(quantite>=0),


    UNIQUE(emplacement_id, produit_id),


    FOREIGN KEY(emplacement_id)
        REFERENCES emplacement(id),


    FOREIGN KEY(produit_id)
        REFERENCES produits(id)

);



-- ============================================================================
-- 8. LOGISTIQUE
-- ============================================================================

CREATE TABLE types_vehicule (

    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(30) UNIQUE NOT NULL,

    libelle VARCHAR(100) NOT NULL,

    description TEXT

);



CREATE TABLE statuts_vehicule (

    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(50) NOT NULL

);



CREATE TABLE vehicules (

    id BIGSERIAL PRIMARY KEY,

    immatriculation VARCHAR(20) UNIQUE NOT NULL,

    marque VARCHAR(60),

    modele VARCHAR(60),

    annee INT,


    capacite_volume_m3 NUMERIC(10,3)
        NOT NULL CHECK(capacite_volume_m3>0),


    capacite_charge_kg NUMERIC(10,2)
        NOT NULL CHECK(capacite_charge_kg>0),


    kilometrage_actuel NUMERIC(10,2) DEFAULT 0,


    type_vehicule_id BIGINT NOT NULL,

    statut_vehicule_id BIGINT NOT NULL,


    FOREIGN KEY(type_vehicule_id)
        REFERENCES types_vehicule(id),


    FOREIGN KEY(statut_vehicule_id)
        REFERENCES statuts_vehicule(id)

);



CREATE TABLE chauffeurs (

    id BIGSERIAL PRIMARY KEY,

    nom VARCHAR(100) NOT NULL,

    prenom VARCHAR(100),

    telephone VARCHAR(30),

    numero_permis VARCHAR(40) UNIQUE NOT NULL,

    date_expiration_permis DATE,

    actif BOOLEAN DEFAULT TRUE

);



CREATE TABLE statuts_mission (

    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(50) NOT NULL

);



CREATE TABLE missions_logistiques (

    id BIGSERIAL PRIMARY KEY,

    reference_mission VARCHAR(40) UNIQUE NOT NULL,


    date_depart_prevue TIMESTAMP,

    date_arrivee_prevue TIMESTAMP,

    date_depart_reelle TIMESTAMP,

    date_arrivee_reelle TIMESTAMP,


    vehicule_id BIGINT NOT NULL,

    chauffeur_id BIGINT NOT NULL,

    statut_mission_id BIGINT NOT NULL,


    observations TEXT,


    FOREIGN KEY(vehicule_id)
        REFERENCES vehicules(id),


    FOREIGN KEY(chauffeur_id)
        REFERENCES chauffeurs(id),


    FOREIGN KEY(statut_mission_id)
        REFERENCES statuts_mission(id)

);



-- ============================================================================
-- 9. MOUVEMENTS
-- ============================================================================

CREATE TABLE types_mouvement (

    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(30) UNIQUE NOT NULL,

    libelle VARCHAR(100) NOT NULL,

    sens VARCHAR(10) NOT NULL
        CHECK(sens IN ('ENTREE','SORTIE'))

);



CREATE TABLE statuts_mouvement (

    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(20) UNIQUE NOT NULL,

    libelle VARCHAR(50) NOT NULL,

    ordre INT NOT NULL

);



CREATE TABLE mouvements (

    id BIGSERIAL PRIMARY KEY,

    code VARCHAR(30) UNIQUE NOT NULL,

    date_mouvement TIMESTAMP DEFAULT now(),


    type_mouvement_id BIGINT NOT NULL,

    statut_mouvement_id BIGINT NOT NULL,


    client_id BIGINT,

    utilisateur_id BIGINT NOT NULL,


    notes TEXT,


    FOREIGN KEY(type_mouvement_id)
        REFERENCES types_mouvement(id),


    FOREIGN KEY(statut_mouvement_id)
        REFERENCES statuts_mouvement(id),


    FOREIGN KEY(client_id)
        REFERENCES utilisateurs(id),


    FOREIGN KEY(utilisateur_id)
        REFERENCES utilisateurs(id)

);



CREATE TABLE lignes_mouvement (

    id BIGSERIAL PRIMARY KEY,


    mouvement_id BIGINT NOT NULL,


    produit_id BIGINT NOT NULL,


    emplacement_source_id BIGINT,


    emplacement_dest_id BIGINT,


    quantite NUMERIC(12,3)
        NOT NULL CHECK(quantite>0),


    FOREIGN KEY(mouvement_id)
        REFERENCES mouvements(id),


    FOREIGN KEY(produit_id)
        REFERENCES produits(id),


    FOREIGN KEY(emplacement_source_id)
        REFERENCES emplacement(id),


    FOREIGN KEY(emplacement_dest_id)
        REFERENCES emplacement(id)

);


-- ============================================================================
-- FIN SCHEMA
-- ============================================================================
