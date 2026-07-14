
-- ============================================================================
-- 6. TRIGGERS ET FONCTIONS
-- ============================================================================

-- Fonction pour vérifier les lignes de mouvement
CREATE OR REPLACE FUNCTION fn_check_ligne_mouvement()
RETURNS TRIGGER AS $$
DECLARE
    v_sens          VARCHAR(10);
    v_code_type      VARCHAR(30);
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

    -- Controle de capacite sur l'emplacement de destination
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

-- Fonction pour appliquer les mouvements valides
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
-- 7. DONNEES DE TEST
-- ============================================================================
INSERT INTO types_contrat (code, libelle) VALUES
    ('ABONNE', 'Abonne'),
    ('NON_ABONNE', 'Non abonne');

-- Insertion des rôles
INSERT INTO roles (code, libelle) VALUES
    ('ADMIN', 'Administrateur'),
    ('GESTIONNAIRE', 'Gestionnaire d''entrepot'),
    ('RESPONSABLE_LOGISTIQUE', 'Responsable logistique'),
    ('COMPTABLE', 'Comptable'),
    ('CLIENT', 'Client');

-- Insertion des utilisateurs
INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, actif) VALUES
    ('admin@entrepot.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiKqG5q6QeF1J7P2QJm2d7l5z0QvM6C', 1, TRUE),
    ('gestionnaire@entrepot.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiKqG5q6QeF1J7P2QJm2d7l5z0QvM6C', 2, TRUE),
    ('client@test.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiKqG5q6QeF1J7P2QJm2d7l5z0QvM6C', 5, TRUE);

-- Informations des utilisateurs
INSERT INTO utilisateurs_info (utilisateur_id, nom, prenom, numero, adresse, secteur) VALUES
    (1, 'Admin', 'Super', '0340000000', 'Antananarivo', 'Administration'),
    (2, 'Gestionnaire', 'Principal', '0340000001', 'Antananarivo', 'Gestion'),
    (3, 'Client', 'Test', '0340000002', 'Antananarivo', 'Client');

-- Insertion des types de produits
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

-- Insertion des produits
INSERT INTO produits (code, nom, description, type_produit_id, volume_unitaire_m3, poids_unitaire_kg, actif) VALUES
    ('PRD001', 'Riz', 'Riz de haute qualité', 1, 0.001, 1.0, TRUE),
    ('PRD002', 'Huile', 'Huile végétale', 1, 0.0015, 0.9, TRUE),
    ('PRD003', 'Ciment', 'Sac de ciment 50kg', 2, 0.02, 50.0, TRUE),
    ('PRD004', 'Médicaments', 'Médicaments sensibles', 3, 0.0005, 0.1, TRUE);

-- Insertion des types de zones
INSERT INTO types_zone (code, libelle, controle_temperature, acces_restreint, journalisation_acces, charge_lourde_possible) VALUES
    ('ETA', 'Etagère classique', FALSE, FALSE, FALSE, FALSE),
    ('CHF', 'Frigo / Chambre froide', TRUE, FALSE, FALSE, FALSE),
    ('SEC', 'Zone sécurisée', FALSE, TRUE, TRUE, FALSE),
    ('SOL', 'Zone au sol', FALSE, FALSE, FALSE, TRUE);

INSERT INTO type_zone (code, libelle, type_produit_id) VALUES
    ('ETA', 'Etagère classique', 2),
    ('CHF', 'Frigo / Chambre froide', 1),
    ('SEC', 'Zone sécurisée', 4),
    ('SOL', 'Zone au sol', 2);

-- Insertion des allées
INSERT INTO allees (code) VALUES
    ('A1'),
    ('A2'),
    ('B1'),
    ('B2');

INSERT INTO allee (libelle) VALUES
    ('Allée A1'),
    ('Allée A2'),
    ('Allée B1'),
    ('Allée B2');

-- Insertion des étages
INSERT INTO etage (libelle, numero_etage) VALUES
    ('Rez-de-chaussée', 0),
    ('Premier étage', 1),
    ('Deuxième étage', 2);

-- Insertion des colonnes
INSERT INTO colonne (libelle) VALUES
    ('Colonne 1'),
    ('Colonne 2'),
    ('Colonne 3');

-- Insertion des zones
INSERT INTO zone (libelle, volume_total_m3, allees_id, type_zone_id) VALUES
    ('Zone A1', 100.0, 1, 1),
    ('Zone A2', 150.0, 2, 1),
    ('Zone B1', 200.0, 3, 4);

-- Insertion des emplacements
INSERT INTO emplacement (code, etage_id, allee_id, capacite_volume_m3, actif, charge_max, colonne) VALUES
    ('ETA-A1-N1', 1, 1, 10.0, TRUE, 500.0, 1),
    ('ETA-A1-N2', 1, 1, 10.0, TRUE, 500.0, 2),
    ('ETA-A2-N1', 1, 2, 15.0, TRUE, 750.0, 1),
    ('SOL-B1-N1', 1, 3, 50.0, TRUE, 2000.0, 1);

-- Insertion des types de mouvements
INSERT INTO types_mouvement (code, libelle, sens) VALUES
    ('RETOUR_CLIENT', 'Retour client', 'ENTREE'),
    ('RECEPTION', 'Réception', 'ENTREE'),
    ('LIVRAISON_CLIENT', 'Livraison client', 'SORTIE'),
    ('TRANSFERT_INTERNE', 'Transfert interne', 'SORTIE'),
    ('PERTE_DESTRUCTION', 'Perte / Destruction', 'SORTIE'),
    ('EXPEDITION', 'Expédition', 'SORTIE');

-- Insertion des statuts de mouvements
INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
    ('EN_ATTENTE', 'En attente', 1),
    ('EN_CONTROLE', 'En contrôle', 2),
    ('VALIDE', 'Valide', 3),
    ('EXPEDIE', 'Expédié', 4),
    ('ANNULE', 'Annulé', 5);

-- Insertion d'un mouvement de test
INSERT INTO mouvements (code, type_mouvement_id, statut_mouvement_id, utilisateur_id, notes) VALUES
    ('MOV-2026-0001', 2, 1, 2, 'Réception initiale de test');

-- Insertion d'une ligne de mouvement de test
INSERT INTO lignes_mouvement (mouvement_id, produit_id, emplacement_dest_id, quantite) VALUES
    (1, 1, 1, 100.0);

-- Insertion des types de véhicules
INSERT INTO types_vehicule (code, libelle, description) VALUES
    ('CAMION', 'Camion', 'Véhicule lourd pour transport de marchandises'),
    ('UTILITAIRE', 'Utilitaire', 'Véhicule utilitaire léger'),
    ('FOURGON', 'Fourgonnette', 'Fourgonnette moyenne capacité'),
    ('REMORQUE', 'Remorque', 'Remorque tractable');

-- Insertion des statuts de véhicules
INSERT INTO statuts_vehicule (code, libelle) VALUES
    ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission'),
    ('EN_MAINTENANCE', 'En maintenance'),
    ('HORS_SERVICE', 'Hors service');

-- Insertion des chauffeurs de test
INSERT INTO chauffeurs (nom, prenom, telephone, numero_permis, date_expiration_permis, actif) VALUES
    ('Rakoto', 'Jean', '0341234567', 'PERM-001', '2027-12-31', TRUE),
    ('Rasoa', 'Marie', '0342345678', 'PERM-002', '2026-06-30', TRUE),
    ('Randria', 'Paul', '0343456789', 'PERM-003', '2028-01-15', FALSE);

-- Insertion des véhicules de test
INSERT INTO vehicules (immatriculation, marque, modele, annee, capacite_volume_m3, capacite_charge_kg, kilometrage_actuel, type_vehicule_id, statut_vehicule_id) VALUES
    ('ABC-123', 'Renault', 'Kangoo', 2020, 3.5, 1000.0, 45000.0, 2, 1),
    ('DEF-456', 'Peugeot', 'Partner', 2021, 4.0, 1200.0, 32000.0, 2, 1),
    ('GHI-789', 'Citroën', 'Berlingo', 2019, 3.8, 900.0, 67000.0, 2, 3);

-- Insertion des statuts de missions
INSERT INTO statuts_mission (code, libelle) VALUES
    ('PLANIFIEE', 'Planifiée'),
    ('EN_COURS', 'En cours'),
    ('TERMINEE', 'Terminée'),
    ('ANNULEE', 'Annulée');

-- Insertion des missions logistiques de test
INSERT INTO missions_logistiques (reference_mission, date_depart_prevue, date_arrivee_prevue, vehicule_id, chauffeur_id, statut_mission_id, observations) VALUES
    ('MISS-2026-001', '2026-07-10 08:00:00', '2026-07-10 12:00:00', 1, 1, 1, 'Livraison client A'),
    ('MISS-2026-002', '2026-07-11 09:00:00', '2026-07-11 14:00:00', 2, 2, 2, 'Réception fournisseur B');

INSERT INTO unites_duree (code, libelle) VALUES
('JOUR', 'Jour'),
('MOIS', 'Mois');

INSERT INTO tarifs_zone (
    type_zone_id,
    unite_duree_id,
    prix_m3,
    date_debut_validite,
    date_fin_validite
) VALUES
-- Frigo
(1, 1, 1500,  '2026-01-01', NULL),
(1, 2, 35000, '2026-01-01', NULL),

-- Entana sarobidy
(2, 1, 1500,  '2026-01-01', NULL),
(2, 2, 35000, '2026-01-01', NULL),

-- Étagère
(3, 1, 1000,  '2026-01-01', NULL),
(3, 2, 20000, '2026-01-01', NULL),

-- Zone 4
(4, 1, 1000,  '2026-01-01', NULL),
(4, 2, 20000, '2026-01-01', NULL);
