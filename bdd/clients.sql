-- ============================================================================
-- 1. REFERENTIELS GENERIQUES : ROLES, UTILISATEURS (internes + clients)
-- ============================================================================

CREATE TABLE types_produits (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(40) NOT NULL UNIQUE,   -- ALIMENTAIRE | INDUSTRIEL | SENSIBLE | VALEUR ...
    libelle VARCHAR(150) NOT NULL
);


CREATE TABLE types_zone (
    id                     BIGSERIAL PRIMARY KEY,
    code                   VARCHAR(20) NOT NULL UNIQUE,  -- ETA | CHF | SEC | SOL
    libelle                VARCHAR(100) NOT NULL,        -- Etagere classique, Frigo/Chambre froide, Zone securisee, Zone au sol
    type_produit_id BIGINT,
    controle_temperature   BOOLEAN NOT NULL DEFAULT FALSE,
    acces_restreint        BOOLEAN NOT NULL DEFAULT FALSE,
    journalisation_acces   BOOLEAN NOT NULL DEFAULT FALSE,
    charge_lourde_possible BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_types_zone_type_produit_id FOREIGN KEY (type_produit_id) REFERENCES types_produits(id)
);

-- Niveau 2 : la zone / allee (ex: "A1")
CREATE TABLE zones (
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(20) NOT NULL UNIQUE,  -- ex: A1
    libelle        VARCHAR(150),
    type_zone_id BIGINT NOT NULL,
    volume_total_m3 NUMERIC(12,3) NOT NULL CHECK (volume_total_m3 >= 0),
    CONSTRAINT fk_zones_type_zone_id FOREIGN KEY (type_zone_id) REFERENCES types_zone(id)
);


CREATE TABLE roles (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,   -- ADMIN | GESTIONNAIRE | RESPONSABLE_LOGISTIQUE | COMPTABLE | CLIENT
    libelle VARCHAR(100) NOT NULL
);

-- Compte de connexion (remplace Utilisateur_log / "Users" / "Clients" : un
-- client est un utilisateur dont le role est CLIENT)
CREATE TABLE utilisateurs (
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    date_creation    TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_utilisateurs_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Informations de profil (remplace Utilisateur_info), separees du compte
-- de connexion pour ne pas melanger donnees d'authentification et donnees
-- personnelles.
CREATE TABLE utilisateurs_info (
    id              BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL UNIQUE,
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100),
    numero          VARCHAR(30),
    adresse         VARCHAR(255),
    secteur         VARCHAR(100),
    CONSTRAINT fk_utilisateurs_info_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Vue de confort : presente les utilisateurs de role CLIENT comme une
-- "table Clients" classique, sans dupliquer les donnees.
CREATE VIEW v_clients AS
SELECT u.id, u.email, ui.nom, ui.prenom, ui.numero, ui.adresse, ui.secteur
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id
LEFT JOIN utilisateurs_info ui ON ui.utilisateur_id = u.id
WHERE r.code = 'CLIENT';

-- ============================================================================
-- 4. CLIENTS & CONTRATS
-- ============================================================================
-- "methode_paiement" dans la conception fournie melangeait deux notions :
-- le regime contractuel (Abonne / Non abonne) et l'instrument de paiement
-- (especes, mobile money...). Elles sont separees ci-dessous.

CREATE TABLE types_contrat (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- ABONNE | NON_ABONNE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE statuts_demande_stockage (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- EN_ATTENTE | ACCEPTEE | REFUSEE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_stockage (
    id              BIGSERIAL PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL, -- le client
    type_zone_id BIGINT NOT NULL,
    type_contrat_id BIGINT NOT NULL,
    -- volume fixe si abonnement ; pour un client non-abonne, c'est le
    -- volume estime a la 1ere entree (peut etre ajuste ensuite)
    volume_espace_m3 NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    date_debut      DATE NOT NULL,
    date_fin        DATE,
    CONSTRAINT fk_demandes_stockage_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
    CONSTRAINT fk_demandes_stockage_type_zone_id FOREIGN KEY (type_zone_id) REFERENCES types_zone(id),
    CONSTRAINT fk_demandes_stockage_type_contrat_id FOREIGN KEY (type_contrat_id) REFERENCES types_contrat(id)
);

CREATE TABLE historique_etat_demande (
    id                  BIGSERIAL PRIMARY KEY,
    demande_stockage_id BIGINT NOT NULL,
    statut_id BIGINT NOT NULL,
    date_statut         TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_historique_etat_demande_demande_stockage_id FOREIGN KEY (demande_stockage_id) REFERENCES demandes_stockage(id),
    CONSTRAINT fk_historique_etat_demande_statut_id FOREIGN KEY (statut_id) REFERENCES statuts_demande_stockage(id)
);

CREATE TABLE contrats (
    id                  BIGSERIAL PRIMARY KEY,
    demande_stockage_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    type_zone_id BIGINT NOT NULL,
    type_contrat_id BIGINT NOT NULL,
    volume_espace_m3 NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    date_creation       TIMESTAMP NOT NULL DEFAULT now(),
    date_debut          DATE NOT NULL,
    date_fin            DATE,
    description         TEXT,
    CHECK (date_fin IS NULL OR date_fin >= date_debut),
    CONSTRAINT fk_contrats_demande_stockage_id FOREIGN KEY (demande_stockage_id) REFERENCES demandes_stockage(id),
    CONSTRAINT fk_contrats_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
    CONSTRAINT fk_contrats_type_contrat_id FOREIGN KEY (type_contrat_id) REFERENCES types_contrat(id),
    CONSTRAINT fk_demandes_stockage_type_contrat_id FOREIGN KEY (type_contrat_id) REFERENCES types_contrat(id)
);

ALTER TABLE demandes_stockage
ADD COLUMN quantite_emplacement INTEGER NOT NULL DEFAULT 1
CHECK (quantite_emplacement > 0);

ALTER TABLE contrats
ADD COLUMN quantite_emplacement INTEGER NOT NULL DEFAULT 1
CHECK (quantite_emplacement > 0);

CREATE TABLE statuts_renouvellement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_renouvellement (
    id          BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL,
    date_demande  DATE NOT NULL,
    date_fin    DATE,
    CONSTRAINT fk_demandes_renouvellement_contrat_id FOREIGN KEY (contrat_id) REFERENCES contrats(id)
);

CREATE TABLE historique_renouvellement (
    id                          BIGSERIAL PRIMARY KEY,
    demande_renouvellement_id BIGINT NOT NULL,
    statut_renouvellement_id BIGINT NOT NULL,
    date_statut                 TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_historique_renouvellement_demande_renouvellement_id FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement(id),
    CONSTRAINT fk_historique_renouvellement_statut_renouvellement_id FOREIGN KEY (statut_renouvellement_id) REFERENCES statuts_renouvellement(id)
);

CREATE TABLE renouvellements_contrat (
    id                          BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL,
    demande_renouvellement_id BIGINT NOT NULL,
    date_renouvellement                  DATE NOT NULL,
    date_fin                    DATE,
    CONSTRAINT fk_renouvellements_contrat_demande_renouvellement_id FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement(id),
    CONSTRAINT fk_renouvellement_contrat_contrat_id FOREIGN KEY (contrat_id) REFERENCES contrats(id)
);

-- ----------------------------------------------------------------------------
-- Facturation des contrats de stockage
-- ----------------------------------------------------------------------------

CREATE TABLE unites_duree (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- JOUR | MOIS
    libelle VARCHAR(50) NOT NULL
);

-- Grille tarifaire historisee par type de zone et unite de duree
CREATE TABLE tarifs_zone (
    id                  BIGSERIAL PRIMARY KEY,
    type_zone_id BIGINT NOT NULL,
    unite_duree_id BIGINT NOT NULL,
    prix_m3             NUMERIC(12,2) NOT NULL CHECK (prix_m3 >= 0),
    date_debut_validite DATE NOT NULL,
    date_fin_validite   DATE,
    CHECK (date_fin_validite IS NULL OR date_fin_validite >= date_debut_validite),
    CONSTRAINT fk_tarifs_zone_type_zone_id FOREIGN KEY (type_zone_id) REFERENCES types_zone(id),
    CONSTRAINT fk_tarifs_zone_unite_duree_id FOREIGN KEY (unite_duree_id) REFERENCES unites_duree(id)
);

-- Facture client : prix_facture = f(volume_espace_m3, tarifs_zone.prix_m3,
-- duree en unites_duree). Pour un client abonne, le volume facture est le
-- volume reserve (volume_espace_m3 du contrat) quelle que soit l'occupation
-- reelle. Pour un client non-abonne, le volume facture est l'occupation
-- reelle cumulee jour par jour (calculee a partir de stocks_emplacement /
-- de l'historique des mouvements).
CREATE TABLE factures (
    id              BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL,
    volume_espace_m3 NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    prix_facture    NUMERIC(14,2) NOT NULL CHECK (prix_facture >= 0),
    date_emission   DATE NOT NULL DEFAULT CURRENT_DATE,
    date_paiement   DATE,
    CONSTRAINT fk_factures_contrat_id FOREIGN KEY (contrat_id) REFERENCES contrats(id)
);

CREATE TABLE abonnements_stockage (
    id              BIGSERIAL PRIMARY KEY,
    utilisateur_id  BIGINT NOT NULL,
    contrat_id      BIGINT NOT NULL UNIQUE,
    type_zone_id    BIGINT NOT NULL,
    duree_mois      INTEGER NOT NULL CHECK (duree_mois > 0),

    CONSTRAINT fk_abonnements_stockage_utilisateur_id
        FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateurs(id),

    CONSTRAINT fk_abonnements_stockage_contrat_id
        FOREIGN KEY (contrat_id)
        REFERENCES contrats(id),

    CONSTRAINT fk_abonnements_stockage_type_zone_id
        FOREIGN KEY (type_zone_id)
        REFERENCES types_zone(id)
);