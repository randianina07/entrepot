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
    role_id          BIGINT NOT NULL REFERENCES roles(id),
    actif            BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation    TIMESTAMP NOT NULL DEFAULT now(),
    derniere_connexion TIMESTAMP
);
CREATE INDEX idx_utilisateurs_role ON utilisateurs(role_id);

-- Informations de profil (remplace Utilisateur_info), separees du compte
-- de connexion pour ne pas melanger donnees d'authentification et donnees
-- personnelles.
CREATE TABLE utilisateurs_info (
    id              BIGSERIAL PRIMARY KEY,
    utilisateur_id  BIGINT NOT NULL UNIQUE REFERENCES utilisateurs(id) ON DELETE CASCADE,
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100),
    numero          VARCHAR(30),
    adresse         VARCHAR(255),
    secteur         VARCHAR(100)
);

-- Vue de confort : presente les utilisateurs de role CLIENT comme une
-- "table Clients" classique, sans dupliquer les donnees.
CREATE VIEW v_clients AS
SELECT u.id, u.email, u.actif, ui.nom, ui.prenom, ui.numero, ui.adresse, ui.secteur
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

CREATE TABLE modes_paiement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- ESPECES | MOBILE_MONEY | VIREMENT | CHEQUE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE statuts_demande_stockage (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- EN_ATTENTE | ACCEPTEE | REFUSEE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_stockage (
    id              BIGSERIAL PRIMARY KEY,
    utilisateur_id  BIGINT NOT NULL REFERENCES utilisateurs(id),   -- le client
    type_zone_id    BIGINT NOT NULL REFERENCES types_zone(id),
    type_contrat_id BIGINT NOT NULL REFERENCES types_contrat(id),
    -- volume fixe si abonnement ; pour un client non-abonne, c'est le
    -- volume estime a la 1ere entree (peut etre ajuste ensuite)
    volume_espace_m3 NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    date_debut      DATE NOT NULL,
    date_fin        DATE
);
CREATE INDEX idx_demandes_stockage_user ON demandes_stockage(utilisateur_id);

CREATE TABLE historique_etat_demande (
    id                  BIGSERIAL PRIMARY KEY,
    demande_stockage_id BIGINT NOT NULL REFERENCES demandes_stockage(id),
    statut_id           BIGINT NOT NULL REFERENCES statuts_demande_stockage(id),
    date_statut         TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_histo_demande ON historique_etat_demande(demande_stockage_id);

CREATE TABLE contrats (
    id                  BIGSERIAL PRIMARY KEY,
    demande_stockage_id BIGINT NOT NULL REFERENCES demandes_stockage(id),
    utilisateur_id      BIGINT NOT NULL REFERENCES utilisateurs(id),
    type_contrat_id     BIGINT NOT NULL REFERENCES types_contrat(id),
    date_creation       TIMESTAMP NOT NULL DEFAULT now(),
    date_debut          DATE NOT NULL,
    date_fin            DATE,
    description         TEXT,
    CHECK (date_fin IS NULL OR date_fin >= date_debut)
);
CREATE INDEX idx_contrats_user ON contrats(utilisateur_id);

CREATE TABLE statuts_renouvellement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE demandes_renouvellement (
    id          BIGSERIAL PRIMARY KEY,
    contrat_id  BIGINT NOT NULL REFERENCES contrats(id),
    date_debut  DATE NOT NULL,
    date_fin    DATE
);

CREATE TABLE historique_renouvellement (
    id                          BIGSERIAL PRIMARY KEY,
    demande_renouvellement_id   BIGINT NOT NULL REFERENCES demandes_renouvellement(id),
    statut_renouvellement_id    BIGINT NOT NULL REFERENCES statuts_renouvellement(id),
    date_statut                 TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE renouvellements_contrat (
    id                          BIGSERIAL PRIMARY KEY,
    demande_renouvellement_id   BIGINT NOT NULL REFERENCES demandes_renouvellement(id),
    date_debut                  DATE NOT NULL,
    date_fin                    DATE
);

-- ----------------------------------------------------------------------------
-- Facturation des contrats de stockage
-- ----------------------------------------------------------------------------

CREATE TABLE unites_duree (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- JOUR | SEMAINE | MOIS
    libelle VARCHAR(50) NOT NULL
);

-- Grille tarifaire historisee par type de zone et unite de duree
CREATE TABLE tarifs_zone (
    id                  BIGSERIAL PRIMARY KEY,
    type_zone_id        BIGINT NOT NULL REFERENCES types_zone(id),
    unite_duree_id      BIGINT NOT NULL REFERENCES unites_duree(id),
    prix_m3             NUMERIC(12,2) NOT NULL CHECK (prix_m3 >= 0),
    date_debut_validite DATE NOT NULL,
    date_fin_validite   DATE,
    CHECK (date_fin_validite IS NULL OR date_fin_validite >= date_debut_validite)
);
CREATE INDEX idx_tarifs_zone_type ON tarifs_zone(type_zone_id);

-- Facture client : prix_facture = f(volume_espace_m3, tarifs_zone.prix_m3,
-- duree en unites_duree). Pour un client abonne, le volume facture est le
-- volume reserve (volume_espace_m3 du contrat) quelle que soit l'occupation
-- reelle. Pour un client non-abonne, le volume facture est l'occupation
-- reelle cumulee jour par jour (calculee a partir de stocks_emplacement /
-- de l'historique des mouvements).
CREATE TABLE factures (
    id              BIGSERIAL PRIMARY KEY,
    contrat_id      BIGINT NOT NULL REFERENCES contrats(id),
    mode_paiement_id BIGINT REFERENCES modes_paiement(id),
    volume_espace_m3 NUMERIC(10,3) NOT NULL CHECK (volume_espace_m3 > 0),
    prix_facture    NUMERIC(14,2) NOT NULL CHECK (prix_facture >= 0),
    date_emission   DATE NOT NULL DEFAULT CURRENT_DATE,
    date_paiement   DATE
);
CREATE INDEX idx_factures_contrat ON factures(contrat_id);