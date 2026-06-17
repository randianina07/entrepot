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
-- 2. PRODUITS
-- ============================================================================

CREATE TABLE types_produits (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(40) NOT NULL UNIQUE,   -- ALIMENTAIRE | INDUSTRIEL | SENSIBLE | VALEUR ...
    libelle VARCHAR(150) NOT NULL
);

CREATE TABLE produits (
    id                 BIGSERIAL PRIMARY KEY,
    code               VARCHAR(40) NOT NULL UNIQUE,
    nom                VARCHAR(150) NOT NULL,
    description        TEXT,
    type_produit_id    BIGINT REFERENCES types_produits(id),
    volume_unitaire_m3 NUMERIC(10,4) NOT NULL CHECK (volume_unitaire_m3 > 0),
    poids_unitaire_kg  NUMERIC(10,3),
    actif              BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_produits_type ON produits(type_produit_id);

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
    id                     BIGSERIAL PRIMARY KEY,
    code                   VARCHAR(20) NOT NULL UNIQUE,  -- ETA | CHF | SEC | SOL
    libelle                VARCHAR(100) NOT NULL,        -- Etagere classique, Frigo/Chambre froide, Zone securisee, Zone au sol
    type_produit_id        BIGINT REFERENCES types_produits(id),
    controle_temperature   BOOLEAN NOT NULL DEFAULT FALSE,
    acces_restreint        BOOLEAN NOT NULL DEFAULT FALSE,
    journalisation_acces   BOOLEAN NOT NULL DEFAULT FALSE,
    charge_lourde_possible BOOLEAN NOT NULL DEFAULT FALSE
);

-- Niveau 2 : la zone / allee (ex: "A1")
CREATE TABLE zones (
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(20) NOT NULL UNIQUE,  -- ex: A1
    libelle        VARCHAR(150),
    type_zone_id   BIGINT NOT NULL REFERENCES types_zone(id),
    volume_total_m3 NUMERIC(12,3) NOT NULL CHECK (volume_total_m3 >= 0)
);
CREATE INDEX idx_zones_type ON zones(type_zone_id);

-- Niveau 3 (AJOUT) : l'emplacement precis / numero (ex: "N1") -> code complet
-- du type "ETA-A1-N1" reconstitue via types_zone.code || zones.code || code
CREATE TABLE emplacements (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(40) NOT NULL UNIQUE,  -- ex: ETA1-A1-N1
    zone_id             BIGINT NOT NULL REFERENCES zones(id),
    capacite_volume_m3  NUMERIC(10,3) NOT NULL CHECK (capacite_volume_m3 > 0),
    actif               BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_emplacements_zone ON emplacements(zone_id);

-- Stock courant par emplacement (remplace "Zones_Produits"). La quantite
-- est mise a jour par les triggers de validation des mouvements (section 6).
CREATE TABLE stocks_emplacement (
    id              BIGSERIAL PRIMARY KEY,
    emplacement_id  BIGINT NOT NULL REFERENCES emplacements(id),
    produit_id      BIGINT NOT NULL REFERENCES produits(id),
    quantite        NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (quantite >= 0),
    UNIQUE (emplacement_id, produit_id)
);
CREATE INDEX idx_stocks_emplacement_produit ON stocks_emplacement(produit_id);

-- Vue d'aide : volume occupe par emplacement (sert aux controles de
-- capacite et a la visualisation dynamique des places libres/occupees)
CREATE VIEW v_occupation_emplacement AS
SELECT
    e.id AS emplacement_id,
    e.code,
    e.zone_id,
    e.capacite_volume_m3,
    COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_occupe_m3,
    e.capacite_volume_m3 - COALESCE(SUM(se.quantite * p.volume_unitaire_m3), 0) AS volume_libre_m3
FROM emplacements e
LEFT JOIN stocks_emplacement se ON se.emplacement_id = e.id
LEFT JOIN produits p ON p.id = se.produit_id
GROUP BY e.id, e.code, e.zone_id, e.capacite_volume_m3;

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

-- ============================================================================
-- 5. ENTREES / SORTIES DE STOCK (MOUVEMENTS)
-- ============================================================================

CREATE TABLE types_mouvement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,  -- RETOUR_CLIENT | RECEPTION | LIVRAISON_CLIENT | TRANSFERT_INTERNE | PERTE_DESTRUCTION | EXPEDITION
    libelle VARCHAR(100) NOT NULL,
    sens    VARCHAR(10) NOT NULL CHECK (sens IN ('ENTREE', 'SORTIE'))
);

CREATE TABLE statuts_mouvement (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- EN_ATTENTE | EN_CONTROLE | VALIDE | EXPEDIE | ANNULE
    libelle VARCHAR(50) NOT NULL,
    ordre   INT NOT NULL   -- ordre du cycle de vie, utile pour l'IHM / les regles de transition
);

CREATE TABLE mouvements (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(30) NOT NULL UNIQUE,  -- ex: MOV-2026-00123
    date_mouvement      TIMESTAMP NOT NULL DEFAULT now(),
    type_mouvement_id   BIGINT NOT NULL REFERENCES types_mouvement(id),
    statut_mouvement_id BIGINT NOT NULL REFERENCES statuts_mouvement(id),
    client_id           BIGINT REFERENCES utilisateurs(id),   -- optionnel selon le type de mouvement
    utilisateur_id      BIGINT NOT NULL REFERENCES utilisateurs(id),   -- operateur qui cree le mouvement
    notes               TEXT
);
CREATE INDEX idx_mouvements_type ON mouvements(type_mouvement_id);
CREATE INDEX idx_mouvements_statut ON mouvements(statut_mouvement_id);
CREATE INDEX idx_mouvements_client ON mouvements(client_id);

CREATE TABLE lignes_mouvement (
    id                  BIGSERIAL PRIMARY KEY,
    mouvement_id        BIGINT NOT NULL REFERENCES mouvements(id),
    produit_id          BIGINT NOT NULL REFERENCES produits(id),
    emplacement_source_id BIGINT REFERENCES emplacements(id),  -- null si entree pure
    emplacement_dest_id   BIGINT REFERENCES emplacements(id),  -- null si sortie pure
    quantite            NUMERIC(12,3) NOT NULL CHECK (quantite > 0)
);
CREATE INDEX idx_lignes_mouvement_mvt ON lignes_mouvement(mouvement_id);
CREATE INDEX idx_lignes_mouvement_produit ON lignes_mouvement(produit_id);

-- ============================================================================
-- 6. VEHICULES & LOGISTIQUE
-- ============================================================================

CREATE TABLE types_vehicule (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(30) NOT NULL UNIQUE,
    libelle     VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE statuts_vehicule (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- DISPONIBLE | EN_MISSION | EN_MAINTENANCE | HORS_SERVICE
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE vehicules (
    id                  BIGSERIAL PRIMARY KEY,
    immatriculation     VARCHAR(20) NOT NULL UNIQUE,
    marque              VARCHAR(60),
    modele              VARCHAR(60),
    annee               INT,
    capacite_volume_m3  NUMERIC(10,3) NOT NULL CHECK (capacite_volume_m3 > 0),
    capacite_charge_kg  NUMERIC(10,2) NOT NULL CHECK (capacite_charge_kg > 0),
    kilometrage_actuel  NUMERIC(10,2) NOT NULL DEFAULT 0,
    type_vehicule_id    BIGINT NOT NULL REFERENCES types_vehicule(id),
    statut_vehicule_id  BIGINT NOT NULL REFERENCES statuts_vehicule(id)
);
CREATE INDEX idx_vehicules_type ON vehicules(type_vehicule_id);
CREATE INDEX idx_vehicules_statut ON vehicules(statut_vehicule_id);

CREATE TABLE chauffeurs (
    id                      BIGSERIAL PRIMARY KEY,
    nom                     VARCHAR(100) NOT NULL,
    prenom                  VARCHAR(100),
    telephone               VARCHAR(30),
    numero_permis           VARCHAR(40) NOT NULL UNIQUE,
    date_expiration_permis  DATE,
    actif                   BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE statuts_mission (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- PLANIFIEE | EN_COURS | TERMINEE | ANNULEE
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE missions_logistiques (
    id                  BIGSERIAL PRIMARY KEY,
    reference_mission   VARCHAR(40) NOT NULL UNIQUE,
    date_depart_prevue  TIMESTAMP,
    date_arrivee_prevue TIMESTAMP,
    date_depart_reelle  TIMESTAMP,
    date_arrivee_reelle TIMESTAMP,
    vehicule_id         BIGINT NOT NULL REFERENCES vehicules(id),
    chauffeur_id        BIGINT NOT NULL REFERENCES chauffeurs(id),
    statut_mission_id   BIGINT NOT NULL REFERENCES statuts_mission(id),
    observations        TEXT
);
CREATE INDEX idx_missions_vehicule ON missions_logistiques(vehicule_id);
CREATE INDEX idx_missions_chauffeur ON missions_logistiques(chauffeur_id);
CREATE INDEX idx_missions_statut ON missions_logistiques(statut_mission_id);

-- ----------------------------------------------------------------------------
-- Livraison en ville
-- ----------------------------------------------------------------------------
-- La conception fournie definissait "ZoneLivraison" (id, libelle, tarif_base)
-- et "ZoneLivraisonTarif" (id, libelle, commune, distance_km, actif), qui
-- representent en realite le meme objet metier. Elles sont fusionnees ici.

CREATE TABLE zones_livraison (
    id          BIGSERIAL PRIMARY KEY,
    libelle     VARCHAR(100) NOT NULL,   -- ex: Centre Ville, Zone Nord...
    commune     VARCHAR(100),
    distance_km NUMERIC(6,2),
    tarif_base  NUMERIC(12,2) NOT NULL DEFAULT 0,
    actif       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE modes_calcul_livraison (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,  -- POIDS | VOLUME | ZONE | POIDS_ZONE | VOLUME_ZONE
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE tarifs_livraison (
    id                  BIGSERIAL PRIMARY KEY,
    zone_livraison_id   BIGINT NOT NULL REFERENCES zones_livraison(id),
    mode_calcul_id      BIGINT NOT NULL REFERENCES modes_calcul_livraison(id),
    prix_base           NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_par_kg         NUMERIC(12,2) NOT NULL DEFAULT 0,
    prix_par_m3         NUMERIC(12,2) NOT NULL DEFAULT 0,
    date_debut_validite DATE NOT NULL,
    date_fin_validite   DATE,
    CHECK (date_fin_validite IS NULL OR date_fin_validite >= date_debut_validite)
);
CREATE INDEX idx_tarifs_livraison_zone ON tarifs_livraison(zone_livraison_id);

CREATE TABLE livraisons (
    id                BIGSERIAL PRIMARY KEY,
    mission_id        BIGINT NOT NULL REFERENCES missions_logistiques(id),
    client_id         BIGINT NOT NULL REFERENCES utilisateurs(id),
    adresse_livraison VARCHAR(255) NOT NULL,
    zone_livraison_id BIGINT NOT NULL REFERENCES zones_livraison(id),
    poids_total       NUMERIC(10,2) NOT NULL CHECK (poids_total >= 0),
    volume_total      NUMERIC(10,3) NOT NULL CHECK (volume_total >= 0),
    date_prevue       TIMESTAMP,
    date_livraison    TIMESTAMP,
    montant_livraison NUMERIC(12,2)
);
CREATE INDEX idx_livraisons_mission ON livraisons(mission_id);
CREATE INDEX idx_livraisons_client ON livraisons(client_id);

-- Preuve de livraison (module futur, prevu dans le cahier des charges 2.9)
CREATE TABLE preuves_livraison (
    id              BIGSERIAL PRIMARY KEY,
    livraison_id    BIGINT NOT NULL UNIQUE REFERENCES livraisons(id),
    date_validation TIMESTAMP NOT NULL DEFAULT now(),
    signature_client TEXT,    -- signature electronique (image/svg encode ou reference fichier)
    photo_colis     VARCHAR(255),  -- chemin / URL de la photo
    commentaire     TEXT
);

CREATE TABLE facturation_livraison (
    id              BIGSERIAL PRIMARY KEY,
    livraison_id    BIGINT NOT NULL REFERENCES livraisons(id),
    tarif_livraison_id BIGINT NOT NULL REFERENCES tarifs_livraison(id),
    poids_facture   NUMERIC(10,2),
    volume_facture  NUMERIC(10,3),
    montant_calcule NUMERIC(12,2) NOT NULL,
    montant_final   NUMERIC(12,2) NOT NULL,   -- peut inclure frais d'attente, manutention, urgence...
    date_facturation TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_facturation_livraison_liv ON facturation_livraison(livraison_id);

-- ----------------------------------------------------------------------------
-- Maintenance et historique des deplacements
-- ----------------------------------------------------------------------------

CREATE TABLE types_maintenance (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE maintenances_vehicule (
    id                  BIGSERIAL PRIMARY KEY,
    vehicule_id         BIGINT NOT NULL REFERENCES vehicules(id),
    type_maintenance_id BIGINT NOT NULL REFERENCES types_maintenance(id),
    date_maintenance    DATE NOT NULL,
    kilometrage         NUMERIC(10,2),
    cout                NUMERIC(12,2),
    description         TEXT,
    prochaine_maintenance DATE
);
CREATE INDEX idx_maintenances_vehicule ON maintenances_vehicule(vehicule_id);

CREATE TABLE historique_vehicule (
    id                  BIGSERIAL PRIMARY KEY,
    vehicule_id         BIGINT NOT NULL REFERENCES vehicules(id),
    mission_id          BIGINT REFERENCES missions_logistiques(id),
    date_depart         TIMESTAMP,
    date_arrivee        TIMESTAMP,
    kilometrage_depart  NUMERIC(10,2),
    kilometrage_arrivee NUMERIC(10,2),
    distance_parcourue  NUMERIC(10,2) GENERATED ALWAYS AS (kilometrage_arrivee - kilometrage_depart) STORED
);
CREATE INDEX idx_historique_vehicule_veh ON historique_vehicule(vehicule_id);

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
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,  -- MAINTENANCE | CARBURANT | SALAIRES | ELECTRICITE_FROID
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE depenses (
    id              BIGSERIAL PRIMARY KEY,
    date_depense    DATE NOT NULL DEFAULT CURRENT_DATE,
    categorie_id    BIGINT NOT NULL REFERENCES categories_depense(id),
    montant         NUMERIC(14,2) NOT NULL CHECK (montant >= 0),
    description     TEXT,
    mode_paiement_id BIGINT REFERENCES modes_paiement(id),
    vehicule_id     BIGINT REFERENCES vehicules(id),     -- pour rattacher carburant/maintenance a un vehicule
    utilisateur_id  BIGINT REFERENCES utilisateurs(id)    -- saisi par
);
CREATE INDEX idx_depenses_categorie ON depenses(categorie_id);
CREATE INDEX idx_depenses_date ON depenses(date_depense);

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
    id                  BIGSERIAL PRIMARY KEY,
    date_snapshot       DATE NOT NULL,
    zone_id             BIGINT NOT NULL REFERENCES zones(id),
    capacite_totale_m3  NUMERIC(12,3) NOT NULL CHECK (capacite_totale_m3 >= 0),
    capacite_occupee_m3 NUMERIC(12,3) NOT NULL CHECK (capacite_occupee_m3 >= 0),
    taux_occupation     NUMERIC(6,2) GENERATED ALWAYS AS (
        CASE WHEN capacite_totale_m3 = 0 THEN 0
             ELSE round((capacite_occupee_m3 / capacite_totale_m3) * 100, 2)
        END
    ) STORED,
    UNIQUE (date_snapshot, zone_id)
);
CREATE INDEX idx_occupation_espaces_date ON occupation_espaces(date_snapshot);

CREATE TABLE stats_clients (
    id                  BIGSERIAL PRIMARY KEY,
    date_debut          DATE NOT NULL,
    date_fin            DATE NOT NULL,
    volume_stocke_m3    NUMERIC(12,3),
    duree_moyenne_jours NUMERIC(8,2),
    nb_entrees          INT NOT NULL DEFAULT 0,
    nb_sorties          INT NOT NULL DEFAULT 0,
    chiffre_affaires    NUMERIC(14,2) NOT NULL DEFAULT 0,
    client_id           BIGINT NOT NULL REFERENCES utilisateurs(id),
    CHECK (date_fin >= date_debut)
);
CREATE INDEX idx_stats_clients_client ON stats_clients(client_id);

CREATE TABLE flux_entrees_sorties (
    id          BIGSERIAL PRIMARY KEY,
    date        DATE NOT NULL,
    type_flux   VARCHAR(10) NOT NULL CHECK (type_flux IN ('ENTREE', 'SORTIE')),
    type_detail VARCHAR(50),   -- livraison / retour / perte...
    quantite    NUMERIC(12,3) NOT NULL,
    volume_m3   NUMERIC(12,3) NOT NULL,
    mouvement_id BIGINT NOT NULL REFERENCES mouvements(id)
);
CREATE INDEX idx_flux_date ON flux_entrees_sorties(date);
CREATE INDEX idx_flux_mouvement ON flux_entrees_sorties(mouvement_id);

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
    id                          BIGSERIAL PRIMARY KEY,
    date_snapshot               DATE NOT NULL,
    rang                        INT NOT NULL CHECK (rang > 0),
    quantite_totale              NUMERIC(12,3) NOT NULL,
    duree_moyenne_stockage_jours NUMERIC(8,2),
    produit_id                  BIGINT NOT NULL REFERENCES produits(id),
    UNIQUE (date_snapshot, rang)
);
CREATE INDEX idx_top_produits_produit ON top_produits(produit_id);

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

-- ============================================================================
-- 10. DONNEES DE REFERENCE (seed) - valeurs issues du cahier des charges
-- ============================================================================
-- Ces tables "lookup" doivent etre peuplees pour que l'application puisse
-- fonctionner. Les libelles ci-dessous reprennent ceux du cahier des
-- charges ; ils peuvent etre completes/modifies sans aucun impact sur le
-- reste du schema (c'est tout l'interet des tables de reference).

INSERT INTO roles (code, libelle) VALUES
    ('ADMIN', 'Administrateur'),
    ('GESTIONNAIRE', 'Gestionnaire d''entrepot'),
    ('RESPONSABLE_LOGISTIQUE', 'Responsable logistique'),
    ('COMPTABLE', 'Comptable'),
    ('CLIENT', 'Client');

INSERT INTO types_zone (code, libelle, controle_temperature, acces_restreint, journalisation_acces, charge_lourde_possible) VALUES
    ('ETA', 'Etagere classique', FALSE, FALSE, FALSE, FALSE),
    ('CHF', 'Frigo / Chambre froide', TRUE, FALSE, FALSE, FALSE),
    ('SEC', 'Zone securisee', FALSE, TRUE, TRUE, FALSE),
    ('SOL', 'Zone au sol', FALSE, FALSE, FALSE, TRUE);

INSERT INTO types_mouvement (code, libelle, sens) VALUES
    ('RETOUR_CLIENT', 'Retour client', 'ENTREE'),
    ('RECEPTION', 'Reception', 'ENTREE'),
    ('LIVRAISON_CLIENT', 'Livraison client', 'SORTIE'),
    ('TRANSFERT_INTERNE', 'Transfert interne', 'SORTIE'),
    ('PERTE_DESTRUCTION', 'Perte / Destruction', 'SORTIE'),
    ('EXPEDITION', 'Expedition', 'SORTIE');

INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
    ('EN_ATTENTE', 'En attente', 1),
    ('EN_CONTROLE', 'En controle', 2),
    ('VALIDE', 'Valide', 3),
    ('EXPEDIE', 'Expedie', 4),
    ('ANNULE', 'Annule', 5);

INSERT INTO modes_paiement (code, libelle) VALUES
    ('ESPECES', 'Especes'),
    ('MOBILE_MONEY', 'Mobile Money'),
    ('VIREMENT', 'Virement bancaire'),
    ('CHEQUE', 'Cheque');

INSERT INTO types_contrat (code, libelle) VALUES
    ('ABONNE', 'Abonne'),
    ('NON_ABONNE', 'Non abonne');

INSERT INTO statuts_demande_stockage (code, libelle) VALUES
    ('EN_ATTENTE', 'Demande en attente'),
    ('ACCEPTEE', 'Demande acceptee'),
    ('REFUSEE', 'Demande refusee');

INSERT INTO statuts_renouvellement (code, libelle) VALUES
    ('EN_ATTENTE', 'En attente'),
    ('ACCEPTEE', 'Acceptee'),
    ('REFUSEE', 'Refusee');

INSERT INTO unites_duree (code, libelle) VALUES
    ('JOUR', 'Jour'),
    ('SEMAINE', 'Semaine'),
    ('MOIS', 'Mois');

INSERT INTO types_vehicule (code, libelle) VALUES
    ('CAMION_LEGER', 'Camion leger'),
    ('CAMION_FRIGO', 'Camion frigorifique'),
    ('FOURGONNETTE', 'Fourgonnette'),
    ('MOTO_LIVRAISON', 'Moto de livraison');

INSERT INTO statuts_vehicule (code, libelle) VALUES
    ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission'),
    ('EN_MAINTENANCE', 'En maintenance'),
    ('HORS_SERVICE', 'Hors service');

INSERT INTO statuts_mission (code, libelle) VALUES
    ('PLANIFIEE', 'Planifiee'),
    ('EN_COURS', 'En cours'),
    ('TERMINEE', 'Terminee'),
    ('ANNULEE', 'Annulee');

-- Exemples a adapter selon le parc reel
INSERT INTO types_maintenance (code, libelle) VALUES
    ('REVISION', 'Revision periodique'),
    ('REPARATION', 'Reparation'),
    ('VIDANGE', 'Vidange'),
    ('PNEUS', 'Changement pneumatiques');

INSERT INTO modes_calcul_livraison (code, libelle) VALUES
    ('POIDS', 'Par poids'),
    ('VOLUME', 'Par volume'),
    ('ZONE', 'Par zone'),
    ('POIDS_ZONE', 'Poids + Zone'),
    ('VOLUME_ZONE', 'Volume + Zone');

INSERT INTO categories_depense (code, libelle) VALUES
    ('MAINTENANCE', 'Maintenance et reparations'),
    ('CARBURANT', 'Carburant'),
    ('SALAIRES', 'Salaires et charges sociales'),
    ('ELECTRICITE_FROID', 'Electricite et froid');
