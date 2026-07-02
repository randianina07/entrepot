-- Active: 1782389823187@@127.0.0.1@5432@entrepot
-- 1. Création de la table 'colonne'
CREATE TABLE colonne (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255)
);

-- 2. Création de la table 'etage' (nécessaire pour la clé étrangère d'emplacement)
CREATE TABLE etage (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255)
);

-- 3. Création de la table 'emplacement'
CREATE TABLE emplacement (
    id BIGSERIAL PRIMARY KEY,
    codes VARCHAR(255),
    etage_id BIGINT,
    colonne_id BIGINT,
    capacite_volume_m3 DOUBLE PRECISION NOT NULL,
    actif BOOLEAN NOT NULL,
    charge_max DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_emplacement_etage FOREIGN KEY (etage_id) REFERENCES etage(id),
    CONSTRAINT fk_emplacement_colonne FOREIGN KEY (colonne_id) REFERENCES colonne(id)
);

-- =========================================================================
-- INSCRIPTION DE DONNÉES DE TEST (SEEDING)
-- =========================================================================

-- Insertion des colonnes de test
INSERT INTO colonne (libelle) VALUES ('Colonne A'), ('Colonne B'), ('Colonne C');

-- Insertion des étages de test
INSERT INTO etage (libelle) VALUES ('Étage 1'), ('Étage 2'), ('Étage 3');

-- Insertion des emplacements (Scénarios de test identiques à tes faux emplacements)
INSERT INTO emplacement (code, etage_id, colonne_id, capacite_volume_m3, actif, charge_max) VALUES 
-- emp1 : Trop petit pour une requête de 40 m3
('ETA-A1-ET1-E1', 1, 1, 10.0, true, 500.0), 

-- emp2 : Volume de 30 m3, mais inactif (simule un emplacement occupé ou indisponible)
('ETA-A1-ET1-E2', 1, 2, 30.0, false, 1000.0), 

-- emp3 : Volume de 35 m3, actif (Parfait si on cherche un volume <= 35)
('ETA-A1-ET1-E3', 1, 3, 35.0, true, 1200.0),

-- emp4 : Un très grand emplacement de 50 m3, actif (Idéal pour ton test à 40 m3 !)
('ETA-A2-ET2-E4', 2, 1, 50.0, true, 2000.0);