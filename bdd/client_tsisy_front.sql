CREATE OR REPLACE DATABASE entrepot;

CREATE TABLE roles (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,   -- ADMIN | GESTIONNAIRE | RESPONSABLE_LOGISTIQUE | COMPTABLE | CLIENT
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE utilisateurs (
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    date_creation    TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_utilisateurs_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
);

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

CREATE TABLE types_contrat (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- ABONNE | NON_ABONNE
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE contrats (
    id                  BIGSERIAL PRIMARY KEY,
    -- demande_stockage_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    type_contrat_id BIGINT NOT NULL,
    date_creation       TIMESTAMP NOT NULL DEFAULT now(),
    date_debut          DATE NOT NULL,
    date_fin            DATE,
    description         TEXT,
    CHECK (date_fin IS NULL OR date_fin >= date_debut),
    -- CONSTRAINT fk_contrats_demande_stockage_id FOREIGN KEY (demande_stockage_id) REFERENCES demandes_stockage(id),
    CONSTRAINT fk_contrats_utilisateur_id FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id),
    CONSTRAINT fk_contrats_type_contrat_id FOREIGN KEY (type_contrat_id) REFERENCES types_contrat(id)
);

CREATE TABLE renouvellements_contrat (
    id                          BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL,
    -- demande_renouvellement_id BIGINT NOT NULL,
    date_renouvellement                  DATE NOT NULL,
    date_fin                    DATE,
    -- CONSTRAINT fk_renouvellements_contrat_demande_renouvellement_id FOREIGN KEY (demande_renouvellement_id) REFERENCES demandes_renouvellement(id),
    CONSTRAINT fk_renouvellement_contrat_contrat_id FOREIGN KEY (contrat_id) REFERENCES contrats(id)
);