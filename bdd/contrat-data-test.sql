INSERT INTO types_produits (code, libelle) VALUES
('ALIMENTAIRE', 'Produits alimentaires'),
('INDUSTRIEL', 'Produits industriels'),
('SENSIBLE', 'Produits sensibles nécessitant des conditions particulières'),
('VALEUR', 'Produits de grande valeur');

INSERT INTO types_zone (
    code,
    libelle,
    type_produit_id,
    controle_temperature,
    acces_restreint,
    journalisation_acces,
    charge_lourde_possible
) VALUES
(
    'ETA',
    'Etagere classique',
    2,
    FALSE,
    FALSE,
    FALSE,
    FALSE
),
(
    'CHF',
    'Frigo / Chambre froide',
    1,
    TRUE,
    FALSE,
    FALSE,
    FALSE
),
(
    'SEC',
    'Zone securisee',
    4,
    FALSE,
    TRUE,
    TRUE,
    FALSE
),
(
    'SOL',
    'Zone au sol',
    2,
    FALSE,
    FALSE,
    FALSE,
    TRUE
);

INSERT INTO zones (
    code,
    libelle,
    type_zone_id,
    volume_total_m3
) VALUES
('A1', 'Allee A1 - Etagere classique', 1, 500.000),
('A2', 'Allee A2 - Etagere classique', 1, 500.000),
('F1', 'Chambre froide F1', 2, 300.000),
('S1', 'Zone securisee S1', 3, 200.000),
('Z1', 'Zone au sol Z1', 4, 800.000);

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

INSERT INTO unites_duree (code, libelle) VALUES
('JOUR', 'Par jour'),
('MOIS', 'Par mois');

INSERT INTO tarifs_zone (
    type_zone_id,
    unite_duree_id,
    prix_m3,
    date_debut_validite
) VALUES
(1, 1, 100.00, CURRENT_DATE),
(1, 2, 2500.00, CURRENT_DATE),

(2, 1, 200.00, CURRENT_DATE),
(2, 2, 5000.00, CURRENT_DATE),

(3, 1, 300.00, CURRENT_DATE),
(3, 2, 7500.00, CURRENT_DATE),

(4, 1, 80.00, CURRENT_DATE),
(4, 2, 2000.00, CURRENT_DATE);