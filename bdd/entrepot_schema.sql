-- ============================================================================
-- SYSTEME DE GESTION D'ENTREPOT - SCHEMA DE BASE DE DONNEES
-- ============================================================================
-- Cible : PostgreSQL 14+
--
-- Ce script reprend la conception fournie (Stats & BI, Clients & Contrats,
-- Entrees/Sorties, Espaces de stockage, Vehicules & Logistique) en
-- harmonisant les noms, en corrigeant les incoherences entre modules et en
-- comblant les manques necessaires a l'integrite referentielle.
--
-- PRINCIPAUX CHOIX DE CONCEPTION (voir message d'accompagnement pour le detail) :
--   1. Convention de nommage unique : snake_case partout (tables au pluriel).
--   2. Cles primaires techniques (BIGSERIAL) partout, y compris pour les
--      entites qui avaient un code "metier" comme identifiant (ex: zones,
--      mouvements). Le code metier est conserve dans une colonne `code`
--      UNIQUE, separee de la cle technique -> renommer un code ne casse
--      plus aucune relation.
--   3. Unification des comptes : clients et utilisateurs internes
--      partagent une seule table `utilisateurs` distinguee par `role_id`
--      (la conception fournie melangeait Utilisateur_log/Clients/Users).
--   4. Ajout du 3eme niveau d'emplacement (Zone / Allee / Numero) reclame
--      par le cahier des charges section 2.3 mais absent de la conception
--      (Zones_Produits ne portait qu'un niveau Zone).
--   5. Separation de deux notions confondues sous le nom "methode_paiement" :
--        - types_contrat (Abonne / Non abonne) -> regle de facturation
--        - modes_paiement (Especes / Mobile Money / Virement / Cheque)
--          -> instrument de paiement reel (section 2.6 du cahier des charges)
--   6. Fusion de ZoneLivraison et ZoneLivraisonTarif (doublon) en une seule
--      table `zones_livraison`.
--   7. Ajout du module Depenses (section 2.6), absent de la conception
--      fournie mais explicitement requis (priorite Haute).
-- ============================================================================

-- ============================================================================
-- 1. REFERENTIELS GENERIQUES : ROLES, UTILISATEURS (internes + clients)
-- ============================================================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- ADMIN | GESTIONNAIRE | RESPONSABLE_LOGISTIQUE | COMPTABLE | CLIENT
    libelle VARCHAR(100) NOT NULL
);

-- Compte de connexion (remplace Utilisateur_log / "Users" / "Clients" : un
-- client est un utilisateur dont le role est CLIENT)
CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT now (),
    CONSTRAINT fk_utilisateurs_role_id FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Informations de profil (remplace Utilisateur_info), separees du compte
-- de connexion pour ne pas melanger donnees d'authentification et donnees
-- personnelles.
CREATE TABLE utilisateurs_info (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    numero VARCHAR(30),
    adresse VARCHAR(255),
    secteur VARCHAR(100),
    CONSTRAINT fk_utilisateurs_info_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs (id) ON DELETE CASCADE
);

-- Vue de confort : presente les utilisateurs de role CLIENT comme une
-- "table Clients" classique, sans dupliquer les donnees.
CREATE VIEW v_clients AS
SELECT u.id, u.email, ui.nom, ui.prenom, ui.numero, ui.adresse, ui.secteur
FROM
    utilisateurs u
    JOIN roles r ON r.id = u.role_id
    LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
WHERE
    r.code = 'CLIENT';

-- ============================================================================
-- 2. PRODUITS
-- ============================================================================

CREATE TABLE types_produits (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) NOT NULL UNIQUE, -- ALIMENTAIRE | INDUSTRIEL | SENSIBLE | VALEUR ...
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE produits (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) NOT NULL UNIQUE,
    nom VARCHAR(150) NOT NULL,
    description TEXT,
    type_produit_id BIGINT,
    volume_unitaire_m3 NUMERIC(10, 4) NOT NULL CHECK (volume_unitaire_m3 > 0),
    poids_unitaire_kg NUMERIC(10, 3),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_produits_type_produit_id FOREIGN KEY (type_produit_id) REFERENCES types_produits (id)
);

-- ============================================================================
-- 3. ESPACES DE STOCKAGE (structure a 3 niveaux : Zone / Allee / Numero)
-- ============================================================================
-- La conception fournie definissait "ZonesType" et "type_zone" comme deux
-- tables distinctes pour le meme concept (frigo / etagere / zone securisee).
-- Elles sont fusionnees ici en une seule table `types_zone`.
-- Le cahier des charges (2.3) demande explicitement un decoupage en 3
-- niveaux (ex: ETA-A1-N1) ; la conception fournie n'avait que 2 niveaux
-- (ZonesType + Zones). Le niveau "Numero" (emplacement physique precis)
-- est ajoute ici sous forme de table `emplacements`.

CREATE TABLE types_zone (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- ETA | CHF | SEC | SOL
    libelle VARCHAR(100) NOT NULL, -- Etagere classique, Frigo/Chambre froide, Zone securisee, Zone au sol
    type_produit_id BIGINT,
    controle_temperature BOOLEAN NOT NULL DEFAULT FALSE,
    acces_restreint BOOLEAN NOT NULL DEFAULT FALSE,
    journalisation_acces BOOLEAN NOT NULL DEFAULT FALSE,
    charge_lourde_possible BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_types_zone_type_produit_id FOREIGN KEY (type_produit_id) REFERENCES types_produits (id)
);

-- Niveau 2 : la zone / allee (ex: "A1")
CREATE TABLE zones (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- ex: A1
    libelle VARCHAR(150),
    type_zone_id BIGINT NOT NULL,
    volume_total_m3 NUMERIC(12, 3) NOT NULL CHECK (volume_total_m3 >= 0),
    CONSTRAINT fk_zones_type_zone_id FOREIGN KEY (type_zone_id) REFERENCES types_zone (id)
);

-- Niveau 3 (AJOUT) : l'emplacement precis / numero (ex: "N1") -> code complet
-- du type "ETA-A1-N1" reconstitue via types_zone.code || zones.code || code
CREATE TABLE emplacements (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(40) NOT NULL UNIQUE, -- ex: ETA1-A1-N1
    zone_id BIGINT NOT NULL,
    capacite_volume_m3 NUMERIC(10, 3) NOT NULL CHECK (capacite_volume_m3 > 0),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_emplacements_zone_id FOREIGN KEY (zone_id) REFERENCES zones (id)
);

-- Stock courant par emplacement (remplace "Zones_Produits"). La quantite
-- est mise a jour par les triggers de validation des mouvements (section 6).
CREATE TABLE stocks_emplacement (
    id BIGSERIAL PRIMARY KEY,
    emplacement_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    quantite NUMERIC(12, 3) NOT NULL DEFAULT 0 CHECK (quantite >= 0),
    UNIQUE (emplacement_id, produit_id),
    CONSTRAINT fk_stocks_emplacement_emplacement_id FOREIGN KEY (emplacement_id) REFERENCES emplacements (id),
    CONSTRAINT fk_stocks_emplacement_produit_id FOREIGN KEY (produit_id) REFERENCES produits (id)
);

-- Vue d'aide : volume occupe par emplacement (sert aux controles de
-- capacite et a la visualisation dynamique des places libres/occupees)
CREATE VIEW v_occupation_emplacement AS
SELECT
    e.id AS emplacement_id,
    e.code,
    e.zone_id,
    e.capacite_volume_m3,
    COALESCE(
        SUM(
            se.quantite * p.volume_unitaire_m3
        ),
        0
    ) AS volume_occupe_m3,
    e.capacite_volume_m3 - COALESCE(
        SUM(
            se.quantite * p.volume_unitaire_m3
        ),
        0
    ) AS volume_libre_m3
FROM
    emplacements e
    LEFT JOIN stocks_emplacement se ON se.emplacement_id = e.id
    LEFT JOIN produits p ON p.id = se.produit_id
GROUP BY
    e.id,
    e.code,
    e.zone_id,
    e.capacite_volume_m3;

-- ============================================================================
-- 4. CLIENTS & CONTRATS
-- ============================================================================
-- "methode_paiement" dans la conception fournie melangeait deux notions :
-- le regime contractuel (Abonne / Non abonne) et l'instrument de paiement
-- (especes, mobile money...). Elles sont separees ci-dessous.

CREATE TABLE types_contrat (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- ABONNE | NON_ABONNE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE modes_paiement (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- ESPECES | MOBILE_MONEY | VIREMENT | CHEQUE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE statuts_demande_stockage (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- EN_ATTENTE | ACCEPTEE | REFUSEE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_stockage (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL, -- le client
    type_zone_id BIGINT NOT NULL,
    type_contrat_id BIGINT NOT NULL,
    -- volume fixe si abonnement ; pour un client non-abonne, c'est le
    -- volume estime a la 1ere entree (peut etre ajuste ensuite)
    volume_espace_m3 NUMERIC(10, 3) NOT NULL CHECK (volume_espace_m3 > 0),
    date_debut DATE NOT NULL,
    date_fin DATE,
    CONSTRAINT fk_demandes_stockage_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs (id),
    CONSTRAINT fk_demandes_stockage_type_zone_id FOREIGN KEY (type_zone_id) REFERENCES types_zone (id),
    CONSTRAINT fk_demandes_stockage_type_contrat_id FOREIGN KEY (type_contrat_id) REFERENCES types_contrat (id)
);

CREATE TABLE historique_etat_demande (
    id BIGSERIAL PRIMARY KEY,
    demande_stockage_id BIGINT NOT NULL,
    statut_id BIGINT NOT NULL,
    date_statut TIMESTAMP NOT NULL DEFAULT now (),
    CONSTRAINT fk_historique_etat_demande_demande_stockage_id FOREIGN KEY (demande_stockage_id) REFERENCES demandes_stockage (id),
    CONSTRAINT fk_historique_etat_demande_statut_id FOREIGN KEY (statut_id) REFERENCES statuts_demande_stockage (id)
);

CREATE TABLE contrats (
    id BIGSERIAL PRIMARY KEY,
    demande_stockage_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    type_contrat_id BIGINT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT now (),
    date_debut DATE NOT NULL,
    date_fin DATE,
    description TEXT,
    CHECK (
        date_fin IS NULL
        OR date_fin >= date_debut
    ),
    CONSTRAINT fk_contrats_demande_stockage_id FOREIGN KEY (demande_stockage_id) REFERENCES demandes_stockage (id),
    CONSTRAINT fk_contrats_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs (id),
    CONSTRAINT fk_contrats_type_contrat_id FOREIGN KEY (type_contrat_id) REFERENCES types_contrat (id)
);

CREATE TABLE statuts_renouvellement (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_renouvellement (
    id BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    CONSTRAINT fk_demandes_renouvellement_contrat_id FOREIGN KEY (contrat_id) REFERENCES contrats (id)
);

CREATE TABLE historique_renouvellement (
    id BIGSERIAL PRIMARY KEY,
    demande_renouvellement_id BIGINT NOT NULL,
    statut_renouvellement_id BIGINT NOT NULL,
    date_statut TIMESTAMP NOT NULL DEFAULT now (),
    CONSTRAINT fk_historique_renouvellement_demande_renouvellement_id FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement (id),
    CONSTRAINT fk_historique_renouvellement_statut_renouvellement_id FOREIGN KEY (statut_renouvellement_id) REFERENCES statuts_renouvellement (id)
);

CREATE TABLE renouvellements_contrat (
    id BIGSERIAL PRIMARY KEY,
    demande_renouvellement_id BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    CONSTRAINT fk_renouvellements_contrat_demande_renouvellement_id FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement (id)
);

-- ----------------------------------------------------------------------------
-- Facturation des contrats de stockage
-- ----------------------------------------------------------------------------

CREATE TABLE unites_duree (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- JOUR | SEMAINE | MOIS
    libelle VARCHAR(50) NOT NULL
);

-- Grille tarifaire historisee par type de zone et unite de duree
CREATE TABLE tarifs_zone (
    id BIGSERIAL PRIMARY KEY,
    type_zone_id BIGINT NOT NULL,
    unite_duree_id BIGINT NOT NULL,
    prix_m3 NUMERIC(12, 2) NOT NULL CHECK (prix_m3 >= 0),
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    CHECK (
        date_fin_validite IS NULL
        OR date_fin_validite >= date_debut_validite
    ),
    CONSTRAINT fk_tarifs_zone_type_zone_id FOREIGN KEY (type_zone_id) REFERENCES types_zone (id),
    CONSTRAINT fk_tarifs_zone_unite_duree_id FOREIGN KEY (unite_duree_id) REFERENCES unites_duree (id)
);

-- Facture client : prix_facture = f(volume_espace_m3, tarifs_zone.prix_m3,
-- duree en unites_duree). Pour un client abonne, le volume facture est le
-- volume reserve (volume_espace_m3 du contrat) quelle que soit l'occupation
-- reelle. Pour un client non-abonne, le volume facture est l'occupation
-- reelle cumulee jour par jour (calculee a partir de stocks_emplacement /
-- de l'historique des mouvements).
CREATE TABLE factures (
    id BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL,
    mode_paiement_id BIGINT,
    volume_espace_m3 NUMERIC(10, 3) NOT NULL CHECK (volume_espace_m3 > 0),
    prix_facture NUMERIC(14, 2) NOT NULL CHECK (prix_facture >= 0),
    date_emission DATE NOT NULL DEFAULT CURRENT_DATE,
    date_paiement DATE,
    CONSTRAINT fk_factures_contrat_id FOREIGN KEY (contrat_id) REFERENCES contrats (id),
    CONSTRAINT fk_factures_mode_paiement_id FOREIGN KEY (mode_paiement_id) REFERENCES modes_paiement (id)
);

-- ============================================================================
-- 5. ENTREES / SORTIES DE STOCK (MOUVEMENTS)
-- ============================================================================

CREATE TABLE types_mouvement (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- RETOUR_CLIENT | RECEPTION | LIVRAISON_CLIENT | TRANSFERT_INTERNE | PERTE_DESTRUCTION | EXPEDITION
    libelle VARCHAR(100) NOT NULL,
    sens VARCHAR(10) NOT NULL CHECK (sens IN ('ENTREE', 'SORTIE'))
);

CREATE TABLE statuts_mouvement (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- EN_ATTENTE | EN_CONTROLE | VALIDE | EXPEDIE | ANNULE
    libelle VARCHAR(50) NOT NULL,
    ordre INT NOT NULL -- ordre du cycle de vie, utile pour l'IHM / les regles de transition
);

CREATE TABLE mouvements (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- ex: MOV-2026-00123
    date_mouvement TIMESTAMP NOT NULL DEFAULT now (),
    type_mouvement_id BIGINT NOT NULL,
    statut_mouvement_id BIGINT NOT NULL,
    client_id BIGINT, -- optionnel selon le type de mouvement
    utilisateur_id BIGINT NOT NULL, -- operateur qui cree le mouvement
    notes TEXT,
    CONSTRAINT fk_mouvements_type_mouvement_id FOREIGN KEY (type_mouvement_id) REFERENCES types_mouvement (id),
    CONSTRAINT fk_mouvements_statut_mouvement_id FOREIGN KEY (statut_mouvement_id) REFERENCES statuts_mouvement (id),
    CONSTRAINT fk_mouvements_client_id FOREIGN KEY (client_id) REFERENCES utilisateurs (id),
    CONSTRAINT fk_mouvements_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs (id)
);

CREATE TABLE lignes_mouvement (
    id BIGSERIAL PRIMARY KEY,
    mouvement_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    emplacement_source_id BIGINT, -- null si entree pure
    emplacement_dest_id BIGINT, -- null si sortie pure
    quantite NUMERIC(12, 3) NOT NULL CHECK (quantite > 0),
    CONSTRAINT fk_lignes_mouvement_mouvement_id FOREIGN KEY (mouvement_id) REFERENCES mouvements (id),
    CONSTRAINT fk_lignes_mouvement_produit_id FOREIGN KEY (produit_id) REFERENCES produits (id),
    CONSTRAINT fk_lignes_mouvement_emplacement_source_id FOREIGN KEY (emplacement_source_id) REFERENCES emplacements (id),
    CONSTRAINT fk_lignes_mouvement_emplacement_dest_id FOREIGN KEY (emplacement_dest_id) REFERENCES emplacements (id)
);

-- ============================================================================
-- 6. VEHICULES & LOGISTIQUE
-- ============================================================================

CREATE TABLE types_vehicule (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE statuts_vehicule (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- DISPONIBLE | EN_MISSION | EN_MAINTENANCE | HORS_SERVICE
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE vehicules (
    id BIGSERIAL PRIMARY KEY,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    marque VARCHAR(60),
    modele VARCHAR(60),
    annee INT,
    capacite_volume_m3 NUMERIC(10, 3) NOT NULL CHECK (capacite_volume_m3 > 0),
    capacite_charge_kg NUMERIC(10, 2) NOT NULL CHECK (capacite_charge_kg > 0),
    kilometrage_actuel NUMERIC(10, 2) NOT NULL DEFAULT 0,
    type_vehicule_id BIGINT NOT NULL,
    statut_vehicule_id BIGINT NOT NULL,
    CONSTRAINT fk_vehicules_type_vehicule_id FOREIGN KEY (type_vehicule_id) REFERENCES types_vehicule (id),
    CONSTRAINT fk_vehicules_statut_vehicule_id FOREIGN KEY (statut_vehicule_id) REFERENCES statuts_vehicule (id)
);

CREATE TABLE chauffeurs (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    telephone VARCHAR(30),
    numero_permis VARCHAR(40) NOT NULL UNIQUE,
    date_expiration_permis DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE statuts_mission (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- PLANIFIEE | EN_COURS | TERMINEE | ANNULEE
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE missions_logistiques (
    id BIGSERIAL PRIMARY KEY,
    reference_mission VARCHAR(40) NOT NULL UNIQUE,
    date_depart_prevue TIMESTAMP,
    date_arrivee_prevue TIMESTAMP,
    date_depart_reelle TIMESTAMP,
    date_arrivee_reelle TIMESTAMP,
    vehicule_id BIGINT NOT NULL,
    chauffeur_id BIGINT NOT NULL,
    statut_mission_id BIGINT NOT NULL,
    observations TEXT,
    CONSTRAINT fk_missions_logistiques_vehicule_id FOREIGN KEY (vehicule_id) REFERENCES vehicules (id),
    CONSTRAINT fk_missions_logistiques_chauffeur_id FOREIGN KEY (chauffeur_id) REFERENCES chauffeurs (id),
    CONSTRAINT fk_missions_logistiques_statut_mission_id FOREIGN KEY (statut_mission_id) REFERENCES statuts_mission (id)
);

-- ----------------------------------------------------------------------------
-- Livraison en ville
-- ----------------------------------------------------------------------------
-- La conception fournie definissait "ZoneLivraison" (id, libelle, tarif_base)
-- et "ZoneLivraisonTarif" (id, libelle, commune, distance_km, actif), qui
-- representent en realite le meme objet metier. Elles sont fusionnees ici.

-- =====================================================
-- COMMUNES
-- =====================================================

CREATE TABLE communes (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- =====================================================
-- ZONES DE LIVRAISON
-- =====================================================

CREATE TABLE zones_livraison (
    id BIGSERIAL PRIMARY KEY,
    commune_id BIGINT NOT NULL,
    nom VARCHAR(100) NOT NULL,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_zone_commune FOREIGN KEY (commune_id) REFERENCES communes (id),
    CONSTRAINT uk_zone_commune UNIQUE (commune_id, nom)
);

-- =====================================================
-- ENTREPOTS
-- =====================================================

CREATE TABLE entrepots (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    adresse VARCHAR(255),
    commune_id BIGINT NOT NULL,
    zone_livraison_id BIGINT,
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    telephone VARCHAR(30),
    email VARCHAR(150),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_entrepot_commune FOREIGN KEY (commune_id) REFERENCES communes (id),
    CONSTRAINT fk_entrepot_zone FOREIGN KEY (zone_livraison_id) REFERENCES zones_livraison (id)
);

-- =====================================================
-- MODES DE CALCUL
-- =====================================================

CREATE TABLE modes_calcul_livraison (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

-- =====================================================
-- TARIFS
-- =====================================================

CREATE TABLE tarifs_livraison (
    id BIGSERIAL PRIMARY KEY,
    zone_livraison_id BIGINT NOT NULL,
    mode_calcul_id BIGINT NOT NULL,
    prix_fixe NUMERIC(12, 2) NOT NULL DEFAULT 0,
    prix_par_km NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (prix_par_km >= 0),
    prix_par_kg NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (prix_par_kg >= 0),
    prix_par_m3 NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (prix_par_m3 >= 0),
    date_debut DATE NOT NULL,
    date_fin DATE,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_date_validite CHECK (
        date_fin IS NULL
        OR date_fin >= date_debut
    ),
    CONSTRAINT fk_tarif_zone FOREIGN KEY (zone_livraison_id) REFERENCES zones_livraison (id),
    CONSTRAINT fk_tarif_mode FOREIGN KEY (mode_calcul_id) REFERENCES modes_calcul_livraison (id)
);

-- =====================================================
-- STATUT LIVRAISON
-- =====================================================

CREATE TABLE statuts_livraison (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

-- =====================================================
-- LIVRAISONS
-- =====================================================

CREATE TABLE livraisons (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    mission_id BIGINT NOT NULL,
    entrepot_id BIGINT NOT NULL,
    commune_id BIGINT NOT NULL,
    zone_livraison_id BIGINT NOT NULL,
    tarif_livraison_id BIGINT NOT NULL,
    description_adresse_livraison VARCHAR(255) NOT NULL,
    latitude_livraison NUMERIC(10, 7),
    longitude_livraison NUMERIC(10, 7),
    distance_km NUMERIC(10, 2) CHECK (
        distance_km IS NULL
        OR distance_km >= 0
    ),
    duree_minute INTEGER CHECK (
        duree_minute IS NULL
        OR duree_minute >= 0
    ),
    poids_total NUMERIC(10, 2) NOT NULL DEFAULT 0 CHECK (poids_total >= 0),
    volume_total NUMERIC(10, 3) NOT NULL DEFAULT 0 CHECK (volume_total >= 0),
    montant_livraison NUMERIC(12, 2) CHECK (
        montant_livraison IS NULL
        OR montant_livraison >= 0
    ),
    date_prevue TIMESTAMP,
    date_livraison TIMESTAMP,
    statut_id BIGINT NOT NULL,
    CONSTRAINT fk_livraison_client FOREIGN KEY (client_id) REFERENCES utilisateurs (id),
    CONSTRAINT fk_livraison_mission FOREIGN KEY (mission_id) REFERENCES missions_logistiques (id),
    CONSTRAINT fk_livraison_entrepot FOREIGN KEY (entrepot_id) REFERENCES entrepots (id),
    CONSTRAINT fk_livraison_commune FOREIGN KEY (commune_id) REFERENCES communes (id),
    CONSTRAINT fk_livraison_zone FOREIGN KEY (zone_livraison_id) REFERENCES zones_livraison (id),
    CONSTRAINT fk_livraison_tarif FOREIGN KEY (tarif_livraison_id) REFERENCES tarifs_livraison (id),
    CONSTRAINT fk_livraison_statut FOREIGN KEY (statut_id) REFERENCES statuts_livraison (id)
);
-- Preuve de livraison (module futur, prevu dans le cahier des charges 2.9)
CREATE TABLE preuves_livraison (
    id BIGSERIAL PRIMARY KEY,
    livraison_id BIGINT NOT NULL UNIQUE,
    date_validation TIMESTAMP NOT NULL DEFAULT now (),
    signature_client TEXT, -- signature electronique (image/svg encode ou reference fichier)
    photo_colis VARCHAR(255), -- chemin / URL de la photo
    commentaire TEXT,
    CONSTRAINT fk_preuves_livraison_livraison_id FOREIGN KEY (livraison_id) REFERENCES livraisons (id)
);

CREATE TABLE facturation_livraison (
    id BIGSERIAL PRIMARY KEY,
    livraison_id BIGINT NOT NULL,
    tarif_livraison_id BIGINT NOT NULL,
    poids_facture NUMERIC(10, 2),
    volume_facture NUMERIC(10, 3),
    montant_calcule NUMERIC(12, 2) NOT NULL,
    montant_final NUMERIC(12, 2) NOT NULL, -- peut inclure frais d'attente, manutention, urgence...
    date_facturation TIMESTAMP NOT NULL DEFAULT now (),
    CONSTRAINT fk_facturation_livraison_livraison_id FOREIGN KEY (livraison_id) REFERENCES livraisons (id),
    CONSTRAINT fk_facturation_livraison_tarif_livraison_id FOREIGN KEY (tarif_livraison_id) REFERENCES tarifs_livraison (id)
);

CREATE INDEX idx_zone_commune ON zones_livraison (commune_id);

CREATE INDEX idx_entrepot_commune ON entrepots (commune_id);

CREATE INDEX idx_tarif_zone ON tarifs_livraison (zone_livraison_id);

CREATE INDEX idx_tarif_mode ON tarifs_livraison (mode_calcul_id);

CREATE INDEX idx_livraison_client ON livraisons (client_id);

CREATE INDEX idx_livraison_mission ON livraisons (mission_id);

CREATE INDEX idx_livraison_zone ON livraisons (zone_livraison_id);

CREATE INDEX idx_livraison_statut ON livraisons (statut_id);

-- ----------------------------------------------------------------------------
-- Maintenance et historique des deplacements
-- ----------------------------------------------------------------------------

CREATE TABLE types_maintenance (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE maintenances_vehicule (
    id BIGSERIAL PRIMARY KEY,
    vehicule_id BIGINT NOT NULL,
    type_maintenance_id BIGINT NOT NULL,
    date_maintenance DATE NOT NULL,
    kilometrage NUMERIC(10, 2),
    cout NUMERIC(12, 2),
    description TEXT,
    prochaine_maintenance DATE,
    CONSTRAINT fk_maintenances_vehicule_vehicule_id FOREIGN KEY (vehicule_id) REFERENCES vehicules (id),
    CONSTRAINT fk_maintenances_vehicule_type_maintenance_id FOREIGN KEY (type_maintenance_id) REFERENCES types_maintenance (id)
);

CREATE TABLE historique_vehicule (
    id BIGSERIAL PRIMARY KEY,
    vehicule_id BIGINT NOT NULL,
    mission_id BIGINT,
    date_depart TIMESTAMP,
    date_arrivee TIMESTAMP,
    kilometrage_depart NUMERIC(10, 2),
    kilometrage_arrivee NUMERIC(10, 2),
    distance_parcourue NUMERIC(10, 2) GENERATED ALWAYS AS (
        kilometrage_arrivee - kilometrage_depart
    ) STORED,
    CONSTRAINT fk_historique_vehicule_vehicule_id FOREIGN KEY (vehicule_id) REFERENCES vehicules (id),
    CONSTRAINT fk_historique_vehicule_mission_id FOREIGN KEY (mission_id) REFERENCES missions_logistiques (id)
);

-- ============================================================================
-- 7. GESTION FINANCIERE - DEPENSES (AJOUT PROPOSE)
-- ============================================================================
-- Le module "Gestion financiere" (cahier des charges 2.6, priorite Haute)
-- n'etait couvert dans la conception fournie que par la facturation client
-- (factures, facturation_livraison). Les depenses operationnelles
-- (maintenance, carburant, salaires, electricite/froid) n'avaient pas de
-- table. Elles sont ajoutees ici. Les "recettes" ne sont pas dupliquees
-- dans une table separee : elles sont deja portees par `factures` et
-- `facturation_livraison" et peuvent etre consolidees via la vue
-- `v_recettes` ci-dessous, ce qui evite une double saisie de la meme
-- information financiere.

CREATE TABLE categories_depense (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- MAINTENANCE | CARBURANT | SALAIRES | ELECTRICITE_FROID
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE depenses (
    id BIGSERIAL PRIMARY KEY,
    date_depense DATE NOT NULL DEFAULT CURRENT_DATE,
    categorie_id BIGINT NOT NULL,
    montant NUMERIC(14, 2) NOT NULL CHECK (montant >= 0),
    description TEXT,
    mode_paiement_id BIGINT,
    vehicule_id BIGINT, -- pour rattacher carburant/maintenance a un vehicule
    utilisateur_id BIGINT, -- saisi par
    CONSTRAINT fk_depenses_categorie_id FOREIGN KEY (categorie_id) REFERENCES categories_depense (id),
    CONSTRAINT fk_depenses_mode_paiement_id FOREIGN KEY (mode_paiement_id) REFERENCES modes_paiement (id),
    CONSTRAINT fk_depenses_vehicule_id FOREIGN KEY (vehicule_id) REFERENCES vehicules (id),
    CONSTRAINT fk_depenses_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs (id)
);

CREATE VIEW v_recettes AS
SELECT 'CONTRAT'::VARCHAR(20) AS source, f.id, f.date_emission AS date_recette, f.prix_facture AS montant
FROM factures f
UNION ALL
SELECT 'LIVRAISON'::VARCHAR(20) AS source, fl.id, fl.date_facturation::DATE AS date_recette, fl.montant_final AS montant
FROM facturation_livraison fl;

-- ============================================================================
-- 8. STATISTIQUES & BUSINESS INTELLIGENCE
-- ============================================================================

CREATE TABLE occupation_espaces (
    id BIGSERIAL PRIMARY KEY,
    date_snapshot DATE NOT NULL,
    zone_id BIGINT NOT NULL,
    capacite_totale_m3 NUMERIC(12, 3) NOT NULL CHECK (capacite_totale_m3 >= 0),
    capacite_occupee_m3 NUMERIC(12, 3) NOT NULL CHECK (capacite_occupee_m3 >= 0),
    taux_occupation NUMERIC(6, 2) GENERATED ALWAYS AS (
        CASE
            WHEN capacite_totale_m3 = 0 THEN 0
            ELSE round(
                (
                    capacite_occupee_m3 / capacite_totale_m3
                ) * 100,
                2
            )
        END
    ) STORED,
    UNIQUE (date_snapshot, zone_id),
    CONSTRAINT fk_occupation_espaces_zone_id FOREIGN KEY (zone_id) REFERENCES zones (id)
);

CREATE TABLE stats_clients (
    id BIGSERIAL PRIMARY KEY,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    volume_stocke_m3 NUMERIC(12, 3),
    duree_moyenne_jours NUMERIC(8, 2),
    nb_entrees INT NOT NULL DEFAULT 0,
    nb_sorties INT NOT NULL DEFAULT 0,
    chiffre_affaires NUMERIC(14, 2) NOT NULL DEFAULT 0,
    client_id BIGINT NOT NULL,
    CHECK (date_fin >= date_debut),
    CONSTRAINT fk_stats_clients_client_id FOREIGN KEY (client_id) REFERENCES utilisateurs (id)
);

CREATE TABLE flux_entrees_sorties (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    type_flux VARCHAR(10) NOT NULL CHECK (
        type_flux IN ('ENTREE', 'SORTIE')
    ),
    type_detail VARCHAR(50), -- livraison / retour / perte...
    quantite NUMERIC(12, 3) NOT NULL,
    volume_m3 NUMERIC(12, 3) NOT NULL,
    mouvement_id BIGINT NOT NULL,
    CONSTRAINT fk_flux_entrees_sorties_mouvement_id FOREIGN KEY (mouvement_id) REFERENCES mouvements (id)
);

CREATE TABLE performance_logistique (
    id                  BIGSERIAL PRIMARY KEY,
    date_snapshot       DATE NOT NULL UNIQUE,
    nb_livraisons_total INT NOT NULL DEFAULT 0,
    nb_livraisons_ok    INT NOT NULL DEFAULT 0,
    taux_livraison      NUMERIC(6,2) GENERATED ALWAYS AS (
        CASE WHEN nb_livraisons_total = 0 THEN 0
             ELSE round((nb_livraisons_ok::NUMERIC / nb_livraisons_total) * 100, 2)
        END
    ) STORED,
    delai_moyen_heures  NUMERIC(8,2),
    nb_retards          INT NOT NULL DEFAULT 0
);

CREATE TABLE top_produits (
    id BIGSERIAL PRIMARY KEY,
    date_snapshot DATE NOT NULL,
    rang INT NOT NULL CHECK (rang > 0),
    quantite_totale NUMERIC(12, 3) NOT NULL,
    duree_moyenne_stockage_jours NUMERIC(8, 2),
    produit_id BIGINT NOT NULL,
    UNIQUE (date_snapshot, rang),
    CONSTRAINT fk_top_produits_produit_id FOREIGN KEY (produit_id) REFERENCES produits (id)
);

-- Cette table te permettra de ne pas appeler Google Maps à chaque création de livraison. Lorsqu'une zone est utilisée pour la première fois, tu récupères ses coordonnées via l'API, tu les enregistres, puis les prochaines livraisons utilisent directement ces coordonnées, ce qui réduit les coûts et accélère les calculs. C'est une pratique courante dans les applications de logistique.
CREATE TABLE geocoding_cache (
    id BIGSERIAL PRIMARY KEY,
    commune_id BIGINT NOT NULL,
    zone_livraison_id BIGINT NOT NULL,
    adresse_google VARCHAR(255),
    latitude NUMERIC(10, 7),
    longitude NUMERIC(10, 7),
    date_mise_a_jour TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cache_commune FOREIGN KEY (commune_id) REFERENCES communes (id),
    CONSTRAINT fk_cache_zone FOREIGN KEY (zone_livraison_id) REFERENCES zones_livraison (id)
);

-- ============================================================================
-- 9. REGLES METIER - TRIGGERS
-- ============================================================================
-- Les regles ci-dessous touchent plusieurs lignes/tables a la fois ; elles
-- ne peuvent pas s'exprimer avec un simple CHECK (qui ne porte que sur la
-- ligne courante). Elles sont donc implementees en triggers PL/pgSQL.

-- ----------------------------------------------------------------------------
-- 9.1 Lignes de mouvement : coherence source/destination selon le sens du
--     mouvement, et controle de la capacite de l'emplacement de destination
-- ----------------------------------------------------------------------------
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

    -- Controle de capacite sur l'emplacement de destination (entree ou transfert)
    IF NEW.emplacement_dest_id IS NOT NULL THEN
        SELECT volume_unitaire_m3 INTO v_volume_unitaire FROM produits WHERE id = NEW.produit_id;
        SELECT capacite_volume_m3 INTO v_capacite FROM emplacements WHERE id = NEW.emplacement_dest_id;
        SELECT COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) INTO v_volume_occupe
        FROM stocks_emplacement se
        JOIN produits p ON p.id = se.produit_id
        WHERE se.emplacement_id = NEW.emplacement_dest_id;

        IF v_volume_occupe + (NEW.quantite * v_volume_unitaire) > v_capacite THEN
            RAISE EXCEPTION 'Capacite insuffisante sur l''emplacement de destination (occupe: %, demande: %, capacite: %)',
                v_volume_occupe, NEW.quantite * v_volume_unitaire, v_capacite;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_ligne_mouvement
    BEFORE INSERT OR UPDATE ON lignes_mouvement
    FOR EACH ROW EXECUTE FUNCTION fn_check_ligne_mouvement();

-- ----------------------------------------------------------------------------
-- 9.2 Mise a jour du stock (stocks_emplacement) lorsqu'un mouvement passe au
--     statut VALIDE (cf. cahier des charges 2.5 : "la quantite dans
--     Zones_Produits doit etre mise a jour")
-- ----------------------------------------------------------------------------
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

    -- On applique le mouvement uniquement lors de la transition VERS 'VALIDE'
    IF v_code_statut_apres = 'VALIDE' AND v_code_statut_avant IS DISTINCT FROM 'VALIDE' THEN
        FOR ligne IN SELECT * FROM lignes_mouvement WHERE mouvement_id = NEW.id LOOP
            -- Sortie : decrementer l'emplacement source
            IF ligne.emplacement_source_id IS NOT NULL THEN
                UPDATE stocks_emplacement
                SET quantite = quantite - ligne.quantite
                WHERE emplacement_id = ligne.emplacement_source_id AND produit_id = ligne.produit_id;

                IF NOT FOUND THEN
                    RAISE EXCEPTION 'Stock introuvable pour le produit % a l''emplacement %', ligne.produit_id, ligne.emplacement_source_id;
                END IF;
            END IF;

            -- Entree : incrementer l'emplacement de destination (creer la ligne si absente)
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

-- ----------------------------------------------------------------------------
-- 9.3 Vehicules & missions : un vehicule en maintenance ou deja en mission
--     active ne peut pas etre affecte a une nouvelle mission active ; un
--     chauffeur ne peut avoir qu'une seule mission active.
--     "Mission active" = statut PLANIFIEE ou EN_COURS.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_affectation_mission()
RETURNS TRIGGER AS $$
DECLARE
    v_code_statut_mission VARCHAR(20);
    v_code_statut_vehicule VARCHAR(20);
    v_nb_missions_actives_vehicule INT;
    v_nb_missions_actives_chauffeur INT;
BEGIN
    SELECT code INTO v_code_statut_mission FROM statuts_mission WHERE id = NEW.statut_mission_id;

    -- On ne controle que lorsque la mission elle-meme est active
    IF v_code_statut_mission IN ('PLANIFIEE', 'EN_COURS') THEN

        SELECT sv.code INTO v_code_statut_vehicule
        FROM vehicules v JOIN statuts_vehicule sv ON sv.id = v.statut_vehicule_id
        WHERE v.id = NEW.vehicule_id;

        IF v_code_statut_vehicule = 'EN_MAINTENANCE' THEN
            RAISE EXCEPTION 'Le vehicule % est en maintenance et ne peut pas etre affecte a une mission', NEW.vehicule_id;
        END IF;

        SELECT count(*) INTO v_nb_missions_actives_vehicule
        FROM missions_logistiques ml
        JOIN statuts_mission sm ON sm.id = ml.statut_mission_id
        WHERE ml.vehicule_id = NEW.vehicule_id
          AND sm.code IN ('PLANIFIEE', 'EN_COURS')
          AND ml.id <> COALESCE(NEW.id, -1);

        IF v_nb_missions_actives_vehicule > 0 THEN
            RAISE EXCEPTION 'Le vehicule % a deja une mission active', NEW.vehicule_id;
        END IF;

        SELECT count(*) INTO v_nb_missions_actives_chauffeur
        FROM missions_logistiques ml
        JOIN statuts_mission sm ON sm.id = ml.statut_mission_id
        WHERE ml.chauffeur_id = NEW.chauffeur_id
          AND sm.code IN ('PLANIFIEE', 'EN_COURS')
          AND ml.id <> COALESCE(NEW.id, -1);

        IF v_nb_missions_actives_chauffeur > 0 THEN
            RAISE EXCEPTION 'Le chauffeur % a deja une mission active', NEW.chauffeur_id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_affectation_mission
    BEFORE INSERT OR UPDATE ON missions_logistiques
    FOR EACH ROW EXECUTE FUNCTION fn_check_affectation_mission();

-- ----------------------------------------------------------------------------
-- 9.4 Livraisons : le poids et le volume cumules des livraisons d'une
--     mission ne doivent pas depasser la capacite du vehicule affecte.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_capacite_livraison()
RETURNS TRIGGER AS $$
DECLARE
    v_capacite_charge_kg NUMERIC(10,2);
    v_capacite_volume_m3 NUMERIC(10,3);
    v_poids_cumule       NUMERIC(12,2);
    v_volume_cumule      NUMERIC(12,3);
BEGIN
    SELECT v.capacite_charge_kg, v.capacite_volume_m3
    INTO v_capacite_charge_kg, v_capacite_volume_m3
    FROM missions_logistiques ml
    JOIN vehicules v ON v.id = ml.vehicule_id
    WHERE ml.id = NEW.mission_id;

    SELECT COALESCE(SUM(poids_total), 0), COALESCE(SUM(volume_total), 0)
    INTO v_poids_cumule, v_volume_cumule
    FROM livraisons
    WHERE mission_id = NEW.mission_id AND id <> COALESCE(NEW.id, -1);

    IF v_poids_cumule + NEW.poids_total > v_capacite_charge_kg THEN
        RAISE EXCEPTION 'Capacite de charge du vehicule depassee pour la mission % (cumule: % kg, capacite: % kg)',
            NEW.mission_id, v_poids_cumule + NEW.poids_total, v_capacite_charge_kg;
    END IF;

    IF v_volume_cumule + NEW.volume_total > v_capacite_volume_m3 THEN
        RAISE EXCEPTION 'Capacite de volume du vehicule depassee pour la mission % (cumule: % m3, capacite: % m3)',
            NEW.mission_id, v_volume_cumule + NEW.volume_total, v_capacite_volume_m3;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_capacite_livraison
    BEFORE INSERT OR UPDATE ON livraisons
    FOR EACH ROW EXECUTE FUNCTION fn_check_capacite_livraison();

INSERT INTO
    roles (code, libelle)
VALUES ('ADMIN', 'Administrateur'),
    (
        'GESTIONNAIRE',
        'Gestionnaire d''entrepot'
    ),
    (
        'RESPONSABLE_LOGISTIQUE',
        'Responsable logistique'
    ),
    ('COMPTABLE', 'Comptable'),
    ('CLIENT', 'Client');

INSERT INTO
    types_zone (
        code,
        libelle,
        controle_temperature,
        acces_restreint,
        journalisation_acces,
        charge_lourde_possible
    )
VALUES (
        'ETA',
        'Etagere classique',
        FALSE,
        FALSE,
        FALSE,
        FALSE
    ),
    (
        'CHF',
        'Frigo / Chambre froide',
        TRUE,
        FALSE,
        FALSE,
        FALSE
    ),
    (
        'SEC',
        'Zone securisee',
        FALSE,
        TRUE,
        TRUE,
        FALSE
    ),
    (
        'SOL',
        'Zone au sol',
        FALSE,
        FALSE,
        FALSE,
        TRUE
    );

INSERT INTO
    types_mouvement (code, libelle, sens)
VALUES (
        'RETOUR_CLIENT',
        'Retour client',
        'ENTREE'
    ),
    (
        'RECEPTION',
        'Reception',
        'ENTREE'
    ),
    (
        'LIVRAISON_CLIENT',
        'Livraison client',
        'SORTIE'
    ),
    (
        'TRANSFERT_INTERNE',
        'Transfert interne',
        'SORTIE'
    ),
    (
        'PERTE_DESTRUCTION',
        'Perte / Destruction',
        'SORTIE'
    ),
    (
        'EXPEDITION',
        'Expedition',
        'SORTIE'
    );

INSERT INTO
    statuts_mouvement (code, libelle, ordre)
VALUES ('EN_ATTENTE', 'En attente', 1),
    (
        'EN_CONTROLE',
        'En controle',
        2
    ),
    ('VALIDE', 'Valide', 3),
    ('EXPEDIE', 'Expedie', 4),
    ('ANNULE', 'Annule', 5);

INSERT INTO
    modes_paiement (code, libelle)
VALUES ('ESPECES', 'Especes'),
    (
        'MOBILE_MONEY',
        'Mobile Money'
    ),
    (
        'VIREMENT',
        'Virement bancaire'
    ),
    ('CHEQUE', 'Cheque');

INSERT INTO
    types_contrat (code, libelle)
VALUES ('ABONNE', 'Abonne'),
    ('NON_ABONNE', 'Non abonne');

INSERT INTO
    statuts_demande_stockage (code, libelle)
VALUES (
        'EN_ATTENTE',
        'Demande en attente'
    ),
    (
        'ACCEPTEE',
        'Demande acceptee'
    ),
    ('REFUSEE', 'Demande refusee');

INSERT INTO
    statuts_renouvellement (code, libelle)
VALUES ('EN_ATTENTE', 'En attente'),
    ('ACCEPTEE', 'Acceptee'),
    ('REFUSEE', 'Refusee');

INSERT INTO
    unites_duree (code, libelle)
VALUES ('JOUR', 'Jour'),
    ('SEMAINE', 'Semaine'),
    ('MOIS', 'Mois');

INSERT INTO
    types_vehicule (code, libelle)
VALUES (
        'CAMION_LEGER',
        'Camion leger'
    ),
    (
        'CAMION_FRIGO',
        'Camion frigorifique'
    ),
    (
        'FOURGONNETTE',
        'Fourgonnette'
    ),
    (
        'MOTO_LIVRAISON',
        'Moto de livraison'
    );

INSERT INTO
    statuts_vehicule (code, libelle)
VALUES ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission'),
    (
        'EN_MAINTENANCE',
        'En maintenance'
    ),
    (
        'HORS_SERVICE',
        'Hors service'
    );

INSERT INTO
    statuts_mission (code, libelle)
VALUES ('PLANIFIEE', 'Planifiee'),
    ('EN_COURS', 'En cours'),
    ('TERMINEE', 'Terminee'),
    ('ANNULEE', 'Annulee');

-- Exemples a adapter selon le parc reel
INSERT INTO
    types_maintenance (code, libelle)
VALUES (
        'REVISION',
        'Revision periodique'
    ),
    ('REPARATION', 'Reparation'),
    ('VIDANGE', 'Vidange'),
    (
        'PNEUS',
        'Changement pneumatiques'
    );

INSERT INTO
    modes_calcul_livraison (code, libelle)
VALUES ('POIDS', 'Par poids'),
    ('VOLUME', 'Par volume'),
    ('ZONE', 'Par zone'),
    ('POIDS_ZONE', 'Poids + Zone'),
    (
        'VOLUME_ZONE',
        'Volume + Zone'
    );

INSERT INTO
    categories_depense (code, libelle)
VALUES (
        'MAINTENANCE',
        'Maintenance et reparations'
    ),
    ('CARBURANT', 'Carburant'),
    (
        'SALAIRES',
        'Salaires et charges sociales'
    ),
    (
        'ELECTRICITE_FROID',
        'Electricite et froid'
    );

-- ============================================================================
-- INSERTION DONNEES TEST - 2 LIGNES PAR TABLE
-- ============================================================================

-- 1. roles
INSERT INTO
    roles (code, libelle)
VALUES ('ADMIN', 'Administrateur'),
    ('CLIENT', 'Client test');

-- 2. utilisateurs
INSERT INTO
    utilisateurs (
        email,
        mot_de_passe_hash,
        role_id
    )
VALUES ('admin@test.com', 'hash1', 1),
    ('client@test.com', 'hash2', 2);

-- 3. utilisateurs_info
INSERT INTO
    utilisateurs_info (
        utilisateur_id,
        nom,
        prenom,
        numero,
        adresse,
        secteur
    )
VALUES (
        1,
        'Admin',
        'Systeme',
        '000',
        'Antananarivo',
        'IT'
    ),
    (
        2,
        'Rakoto',
        'Jean',
        '111',
        'Tana',
        'Commerce'
    );

-- 4. types_produits
INSERT INTO
    types_produits (code, libelle)
VALUES ('ALIM', 'Alimentaire'),
    ('IND', 'Industriel');

-- 5. produits
INSERT INTO
    produits (
        code,
        nom,
        description,
        type_produit_id,
        volume_unitaire_m3,
        poids_unitaire_kg
    )
VALUES (
        'P001',
        'Riz',
        'Sac de riz',
        1,
        0.05,
        25
    ),
    (
        'P002',
        'Sucre',
        'Sac sucre',
        1,
        0.04,
        20
    );

-- 6. types_zone
INSERT INTO
    types_zone (
        code,
        libelle,
        controle_temperature,
        acces_restreint,
        journalisation_acces,
        charge_lourde_possible
    )
VALUES (
        'ETA',
        'Etagere',
        false,
        false,
        false,
        false
    ),
    (
        'CHF',
        'Chambre froide',
        true,
        false,
        false,
        false
    );

-- 7. zones
INSERT INTO
    zones (
        code,
        libelle,
        type_zone_id,
        volume_total_m3
    )
VALUES ('A1', 'Zone A1', 1, 100),
    ('B1', 'Zone B1', 2, 200);

-- 8. emplacements
INSERT INTO
    emplacements (
        code,
        zone_id,
        capacite_volume_m3
    )
VALUES ('ETA-A1-N1', 1, 10),
    ('CHF-B1-N1', 2, 20);

-- 9. stocks_emplacement
INSERT INTO
    stocks_emplacement (
        emplacement_id,
        produit_id,
        quantite
    )
VALUES (1, 1, 10),
    (2, 2, 5);

-- 10. types_contrat
INSERT INTO
    types_contrat (code, libelle)
VALUES ('ABONNE', 'Abonné'),
    ('NON_ABONNE', 'Non abonné');

-- 11. modes_paiement
INSERT INTO
    modes_paiement (code, libelle)
VALUES ('ESPECES', 'Espèces'),
    ('VIREMENT', 'Virement');

-- 12. statuts_demande_stockage
INSERT INTO
    statuts_demande_stockage (code, libelle)
VALUES ('EN_ATTENTE', 'En attente'),
    ('ACCEPTEE', 'Acceptée');

-- 13. demandes_stockage
INSERT INTO
    demandes_stockage (
        utilisateur_id,
        type_zone_id,
        type_contrat_id,
        volume_espace_m3,
        date_debut
    )
VALUES (2, 1, 1, 10, '2026-01-01'),
    (2, 2, 2, 20, '2026-01-02');

-- 14. contrats
INSERT INTO
    contrats (
        demande_stockage_id,
        utilisateur_id,
        type_contrat_id,
        date_debut
    )
VALUES (1, 2, 1, '2026-01-01'),
    (2, 2, 2, '2026-01-02');

-- 15. factures
INSERT INTO
    factures (
        contrat_id,
        mode_paiement_id,
        volume_espace_m3,
        prix_facture
    )
VALUES (1, 1, 10, 1000),
    (2, 2, 20, 2000);

-- 16. types_mouvement
INSERT INTO
    types_mouvement (code, libelle, sens)
VALUES (
        'RECEPTION',
        'Reception',
        'ENTREE'
    ),
    (
        'LIVRAISON_CLIENT',
        'Livraison',
        'SORTIE'
    );

-- 17. statuts_mouvement
INSERT INTO
    statuts_mouvement (code, libelle, ordre)
VALUES ('VALIDE', 'Valide', 1),
    ('EN_ATTENTE', 'En attente', 2);

-- 18. mouvements
INSERT INTO
    mouvements (
        code,
        type_mouvement_id,
        statut_mouvement_id,
        utilisateur_id
    )
VALUES ('M001', 1, 1, 1),
    ('M002', 2, 2, 1);

-- 19. lignes_mouvement
INSERT INTO
    lignes_mouvement (
        mouvement_id,
        produit_id,
        emplacement_dest_id,
        quantite
    )
VALUES (1, 1, 1, 5),
    (2, 2, 2, 2);

-- 20. types_vehicule
INSERT INTO
    types_vehicule (code, libelle)
VALUES ('CAMION', 'Camion'),
    ('MOTO', 'Moto');

-- 21. statuts_vehicule
INSERT INTO
    statuts_vehicule (code, libelle)
VALUES ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission');

-- 22. vehicules
INSERT INTO
    vehicules (
        immatriculation,
        marque,
        modele,
        annee,
        capacite_volume_m3,
        capacite_charge_kg,
        type_vehicule_id,
        statut_vehicule_id
    )
VALUES (
        '1234TBA',
        'Toyota',
        'Hilux',
        2020,
        10,
        1000,
        1,
        1
    ),
    (
        '5678TBB',
        'Yamaha',
        'TruckBike',
        2022,
        5,
        200,
        2,
        2
    );

-- 23. chauffeurs
INSERT INTO
    chauffeurs (
        nom,
        prenom,
        telephone,
        numero_permis
    )
VALUES (
        'Rakoto',
        'Jean',
        '033000000',
        'P001'
    ),
    (
        'Rabe',
        'Paul',
        '034000000',
        'P002'
    );

-- 24. statuts_mission
INSERT INTO
    statuts_mission (code, libelle)
VALUES ('PLANIFIEE', 'Planifiée'),
    ('EN_COURS', 'En cours');

-- 25. missions_logistiques
INSERT INTO
    missions_logistiques (
        reference_mission,
        vehicule_id,
        chauffeur_id,
        statut_mission_id
    )
VALUES ('MS001', 1, 1, 1),
    ('MS002', 2, 2, 2);

-- 26. zones_livraison
-- INSERT INTO zones_livraison (libelle,commune,distance_km,tarif_base)
-- VALUES
-- ('Centre','Tana',5,1000),
-- ('Nord','Tana',10,2000);

-- 27. modes_calcul_livraison
-- INSERT INTO modes_calcul_livraison (code,libelle)
-- VALUES
-- ('POIDS','Par poids'),
-- ('VOLUME','Par volume');

-- 28. tarifs_livraison
-- INSERT INTO tarifs_livraison (zone_livraison_id,mode_calcul_id,prix_base,prix_par_kg,prix_par_m3,date_debut_validite)
-- VALUES
-- (1,1,100,10,5,'2026-01-01'),
-- (2,2,200,20,10,'2026-01-01');

-- 29. livraisons
-- INSERT INTO livraisons (mission_id,client_id,adresse_livraison,zone_livraison_id,poids_total,volume_total)
-- VALUES
-- (1,2,'Tana Centre',1,10,1),
-- (2,2,'Tana Nord',2,20,2);

INSERT INTO
    communes (nom)
VALUES ('Antananarivo I'),
    ('Antananarivo II'),
    ('Antananarivo III'),
    ('Antananarivo IV'),
    ('Antananarivo V'),
    ('Antananarivo VI'),
    ('Andoharanofotsy'),
    ('Tanjombato'),
    ('Ankaraobato'),
    ('Ambohijanaka');

INSERT INTO
    zones_livraison (
        commune_id,
        nom,
        latitude,
        longitude
    )
VALUES

-- Antananarivo I
(
    1,
    'Analakely',
    -18.9088,
    47.5253
),
(
    1,
    'Isoraka',
    -18.9158,
    47.5284
),
(
    1,
    'Antaninarenina',
    -18.9109,
    47.5228
),

-- Antananarivo II
(
    2,
    'Ambohimanarina',
    -18.8790,
    47.5098
),
(
    2,
    'Anjanahary',
    -18.9001,
    47.5378
),

-- Antananarivo III
(3, '67Ha', -18.9238, 47.5367),
(
    3,
    'Ankorondrano',
    -18.8795,
    47.5248
),
(
    3,
    'Andraharo',
    -18.8736,
    47.5217
),

-- Antananarivo IV
(
    4,
    'Andavamamba',
    -18.9225,
    47.5174
),
(
    4,
    'Mahamasina',
    -18.9175,
    47.5184
),

-- Antananarivo V
(
    5,
    'Ambatoroka',
    -18.8965,
    47.5312
),
(
    5,
    'Ankatso',
    -18.9122,
    47.5326
),

-- Antananarivo VI
(
    6,
    'Ivandry',
    -18.8752,
    47.5298
),
(
    6,
    'Talatamaty',
    -18.8060,
    47.4782
),

-- Andoharanofotsy
(
    7,
    'Andoharanofotsy Centre',
    -18.9790,
    47.5333
),
(
    7,
    'Belambanana',
    -18.9778,
    47.5351
),

-- Tanjombato
( 8, 'Tanjombato Centre', -18.9705, 47.5276 ),

-- Ankaraobato
( 9, 'Ankaraobato', -18.9885, 47.5435 ),

-- Ambohijanaka
( 10, 'Ambohijanaka', -19.0015, 47.5605 );

INSERT INTO
    entrepots (
        nom,
        adresse,
        commune_id,
        zone_livraison_id,
        latitude,
        longitude,
        telephone,
        email
    )
VALUES (
        'Entrepôt Principal ITU',
        'ITU Andoharanofotsy',
        7,
        15,
        -18.9790,
        47.5333,
        '0340011223',
        'contact@itu-logistique.mg'
    );

INSERT INTO
    modes_calcul_livraison (code, libelle)
VALUES (
        'DISTANCE',
        'Calcul selon la distance'
    ),
    (
        'POIDS',
        'Calcul selon le poids'
    ),
    (
        'VOLUME',
        'Calcul selon le volume'
    ),
    (
        'DISTANCE_POIDS',
        'Distance + Poids'
    ),
    (
        'DISTANCE_VOLUME',
        'Distance + Volume'
    );

INSERT INTO
    statuts_livraison (code, libelle)
VALUES ('EN_ATTENTE', 'En attente'),
    ('A_PREPARER', 'À préparer'),
    (
        'EN_COURS',
        'En cours de livraison'
    ),
    ('LIVREE', 'Livrée'),
    ('ANNULEE', 'Annulée');

INSERT INTO
    tarifs_livraison (
        zone_livraison_id,
        mode_calcul_id,
        prix_fixe,
        prix_par_km,
        prix_par_kg,
        prix_par_m3,
        date_debut,
        actif
    )
VALUES

-- Andoharanofotsy
( 15, 4, 3000, 700, 300, 15000, '2026-01-01', true ),

-- Belambanana
( 16, 4, 3000, 700, 300, 15000, '2026-01-01', true ),

-- Tanjombato
( 17, 4, 3000, 800, 300, 15000, '2026-01-01', true ),

-- Ankaraobato
( 18, 4, 3000, 850, 350, 16000, '2026-01-01', true ),

-- Ambohijanaka
( 19, 4, 3000, 900, 350, 17000, '2026-01-01', true ),

-- Analakely
( 1, 4, 3000, 1000, 350, 18000, '2026-01-01', true ),

-- Isoraka
( 2, 4, 3000, 1000, 350, 18000, '2026-01-01', true ),

-- 67Ha
( 6, 4, 3000, 900, 300, 17000, '2026-01-01', true );

INSERT INTO
    livraisons (
        client_id,
        mission_id,
        entrepot_id,
        commune_id,
        zone_livraison_id,
        tarif_livraison_id,
        description_adresse_livraison,
        latitude_livraison,
        longitude_livraison,
        distance_km,
        duree_minute,
        poids_total,
        volume_total,
        montant_livraison,
        date_prevue,
        date_livraison,
        statut_id
    )
VALUES (
        1,
        1,
        1,
        1,
        1,
        6,
        'Lot II M 18, près de la BNI Analakely',
        -18.9088000,
        47.5253000,
        9.40,
        24,
        8.50,
        0.30,
        12200,
        '2026-07-05 09:00:00',
        NULL,
        1
    ),
    (
        2,
        2,
        1,
        1,
        2,
        7,
        'Immeuble A, Isoraka',
        -18.9158000,
        47.5284000,
        8.70,
        21,
        15.00,
        0.60,
        13530,
        '2026-07-05 10:30:00',
        NULL,
        2
    ),
    (
        3,
        3,
        1,
        3,
        6,
        8,
        '67Ha Sud, près de la pharmacie',
        -18.9238000,
        47.5367000,
        7.60,
        19,
        12.00,
        0.40,
        10440,
        '2026-07-05 13:00:00',
        NULL,
        3
    ),
    (
        4,
        4,
        1,
        7,
        15,
        1,
        'ITU Andoharanofotsy',
        -18.9790000,
        47.5333000,
        1.20,
        5,
        3.00,
        0.05,
        1740,
        '2026-07-03 09:00:00',
        '2026-07-03 09:07:00',
        4
    ),
    (
        5,
        5,
        1,
        8,
        17,
        3,
        'Marché de Tanjombato',
        -18.9705000,
        47.5276000,
        2.80,
        9,
        18.00,
        0.90,
        7340,
        '2026-07-06 08:00:00',
        NULL,
        1
    );

-- 30. preuves_livraison
INSERT INTO
    preuves_livraison (
        livraison_id,
        signature_client,
        photo_colis
    )
VALUES (1, 'sig1', 'img1.jpg'),
    (2, 'sig2', 'img2.jpg');

-- 31. categories_depense
INSERT INTO
    categories_depense (code, libelle)
VALUES ('CARBURANT', 'Carburant'),
    ('MAINTENANCE', 'Maintenance');

-- 32. depenses
INSERT INTO
    depenses (
        categorie_id,
        montant,
        description,
        vehicule_id,
        utilisateur_id
    )
VALUES (1, 100, 'Essence', 1, 1),
    (2, 200, 'Reparation', 2, 1);

-- 33. unites_duree
INSERT INTO
    unites_duree (code, libelle)
VALUES ('JOUR', 'Jour'),
    ('MOIS', 'Mois');

-- 34. occupation_espaces
INSERT INTO
    occupation_espaces (
        date_snapshot,
        zone_id,
        capacite_totale_m3,
        capacite_occupee_m3
    )
VALUES ('2026-06-01', 1, 100, 50),
    ('2026-06-02', 2, 200, 80);

-- 35. stats_clients
INSERT INTO
    stats_clients (
        date_debut,
        date_fin,
        volume_stocke_m3,
        duree_moyenne_jours,
        nb_entrees,
        nb_sorties,
        chiffre_affaires,
        client_id
    )
VALUES (
        '2026-01-01',
        '2026-01-31',
        100,
        10,
        5,
        2,
        10000,
        2
    ),
    (
        '2026-02-01',
        '2026-02-28',
        200,
        20,
        10,
        5,
        20000,
        2
    );

-- 36. flux_entrees_sorties
INSERT INTO
    flux_entrees_sorties (
        date,
        type_flux,
        type_detail,
        quantite,
        volume_m3,
        mouvement_id
    )
VALUES (
        '2026-06-01',
        'ENTREE',
        'Reception',
        5,
        1,
        1
    ),
    (
        '2026-06-02',
        'SORTIE',
        'Livraison',
        2,
        0.5,
        2
    );

-- 37. performance_logistique
INSERT INTO
    performance_logistique (
        date_snapshot,
        nb_livraisons_total,
        nb_livraisons_ok,
        delai_moyen_heures,
        nb_retards
    )
VALUES ('2026-06-01', 10, 8, 5, 2),
    ('2026-06-02', 20, 18, 4, 1);

-- 38. top_produits
INSERT INTO
    top_produits (
        date_snapshot,
        rang,
        quantite_totale,
        duree_moyenne_stockage_jours,
        produit_id
    )
VALUES ('2026-06-01', 1, 100, 5, 1),
    ('2026-06-01', 2, 80, 3, 2);

-- 39. types_maintenance
INSERT INTO
    types_maintenance (code, libelle)
VALUES ('REVISION', 'Révision'),
    ('VIDANGE', 'Vidange');

-- 40. maintenances_vehicule
INSERT INTO
    maintenances_vehicule (
        vehicule_id,
        type_maintenance_id,
        date_maintenance,
        kilometrage,
        cout
    )
VALUES (
        1,
        1,
        '2026-06-01',
        10000,
        500
    ),
    (
        2,
        2,
        '2026-06-02',
        20000,
        800
    );

-- 41. historique_vehicule
INSERT INTO
    historique_vehicule (
        vehicule_id,
        mission_id,
        date_depart,
        date_arrivee,
        kilometrage_depart,
        kilometrage_arrivee
    )
VALUES (
        1,
        1,
        '2026-06-01',
        '2026-06-02',
        10000,
        10100
    ),
    (
        2,
        2,
        '2026-06-03',
        '2026-06-04',
        20000,
        20200
    );