TRUNCATE TABLE
    flux_entrees_sorties,
    lignes_mouvement,
    stocks_emplacement,
    top_produits,
    stats_clients,
    missions_logistiques,
    mouvements,
    vehicules,
    chauffeurs,
    utilisateurs_info,
    utilisateurs,
    produits,
    emplacements,
    zone,
    type_zone,
    type_produit,
    allees,
    allee,
    etage,
    roles,
    statuts_mission,
    statuts_vehicule,
    types_vehicule,
    statuts_mouvement,
    types_mouvement
RESTART IDENTITY CASCADE;

INSERT INTO types_mouvement (code, libelle, sens) VALUES
    ('ENTREE', 'Entree', 'ENTREE'),
    ('SORTIE', 'Sortie', 'SORTIE'),
    ('TRANSFERT', 'Transfert', 'SORTIE'),
    ('RETOUR', 'Retour', 'ENTREE');

INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
    ('BROUILLON', 'Brouillon', 1),
    ('VALIDE', 'Valide', 2),
    ('ANNULE', 'Annule', 3);

INSERT INTO statuts_mission (code, libelle) VALUES
    ('PLANIFIEE', 'Planifiee'),
    ('EN_COURS', 'En cours'),
    ('TERMINEE', 'Terminee');

INSERT INTO statuts_vehicule (code, libelle) VALUES
    ('DISPONIBLE', 'Disponible'),
    ('EN_MISSION', 'En mission'),
    ('MAINTENANCE', 'Maintenance');

INSERT INTO types_vehicule (code, libelle, description) VALUES
    ('CAMION', 'Camion', 'Vehicule de transport principal'),
    ('VAN', 'Van', 'Vehicule leger de livraison');

INSERT INTO roles (code, libelle) VALUES
    ('ADMIN', 'Administrateur'),
    ('GESTIONNAIRE', 'Gestionnaire'),
    ('RESPONSABLE_LOGISTIQUE', 'Responsable Logistique'),
    ('CLIENT', 'Client');

INSERT INTO type_produit (code, libelle) VALUES
    ('ALIMENTAIRE', 'Alimentaire'),
    ('INDUSTRIEL', 'Industriel');

INSERT INTO type_zone (code, libelle, type_produit_id) VALUES
    ('ETA', 'Etagere', 1),
    ('CHF', 'Chambre Froide', 2),
    ('SEC', 'Zone Securisee', 1);

INSERT INTO allee (libelle) VALUES
    ('Allee Principale');

INSERT INTO allees (code) VALUES
    ('A01');


INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, date_creation, actif) VALUES
    ('admin@entrepot.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 1, '2026-07-01 08:00:00', true),
    ('client1@test.com', '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6', 4, '2026-07-01 08:05:00', true);

INSERT INTO utilisateurs_info (utilisateur_id, nom, prenom, numero, adresse, secteur) VALUES
    (1, 'Admin', 'System', '001234567', 'Adresse Admin', 'Centre'),
    (2, 'Client', 'Test', '002345678', 'Adresse Client', 'Nord');

INSERT INTO chauffeurs (nom, prenom, telephone, numero_permis, date_expiration_permis, actif) VALUES
    ('Rakoto', 'Jean', '0320000001', 'PERMIS-001', '2028-12-31', true);

INSERT INTO produits (code, nom, description, type_produit_id, volume_unitaire_m3, poids_unitaire_kg, actif) VALUES
    ('PROD001', 'Produit A', 'Description produit A', 1, 0.5000, 10.000, true),
    ('PROD002', 'Produit B', 'Description produit B', 2, 1.0000, 20.000, true);

INSERT INTO zone (libelle, volume_total_m3, allees_id, type_zone_id) VALUES
    ('Zone A1', 100.0, 1, 1),
    ('Zone B1', 50.0, 1, 2);

INSERT INTO emplacements (code, etage_id, allee_id, capacite_volume_m3, charge_max, colonne) VALUES
    ('ETA-A1-N1', 1, 1, 10.0, 100.0, 1),
    ('ETA-A1-N2', 1, 1, 10.0, 100.0, 2),
    ('CHF-A1-N1', 1, 1, 5.0, 50.0, 3);

INSERT INTO vehicules (immatriculation, marque, modele, annee, capacite_volume_m3, capacite_charge_kg, kilometrage_actuel, type_vehicule_id, statut_vehicule_id) VALUES
    ('1012-TAB', 'Isuzu', 'NPR', 2022, 15.000, 3000.00, 12500.00, 1, 1);

INSERT INTO missions_logistiques (reference_mission, date_depart_prevue, date_arrivee_prevue, date_depart_reelle, date_arrivee_reelle, vehicule_id, chauffeur_id, statut_mission_id, observations) VALUES
    ('MIS-2026-001', '2026-07-10 08:00:00', '2026-07-10 12:00:00', '2026-07-10 08:10:00', '2026-07-10 11:55:00', 1, 1, 3, 'Mission test terminee');

INSERT INTO mouvements (code, date_mouvement, type_mouvement_id, statut_mouvement_id, client_id, utilisateur_id, notes) VALUES
    ('MVT-001', '2026-07-10 09:00:00', 1, 2, 2, 1, 'Entree initiale'),
    ('MVT-002', '2026-07-11 15:30:00', 2, 2, 2, 1, 'Sortie partielle');

INSERT INTO lignes_mouvement (mouvement_id, produit_id, emplacement_source_id, emplacement_dest_id, quantite) VALUES
    (1, 1, NULL, 1, 25.000),
    (2, 1, 1, NULL, 5.000);

INSERT INTO flux_entrees_sorties (date, type_flux, type_detail, quantite, volume_m3, mouvement_id) VALUES
    ('2026-07-10', 'ENTREE', 'RECEPTION', 25.000, 12.500, 1),
    ('2026-07-11', 'SORTIE', 'LIVRAISON', 5.000, 2.500, 2);

INSERT INTO stocks_emplacement (emplacement_id, produit_id, zone_id, quantite) VALUES
    (1, 1, 1, 20.000),
    (2, 2, 1, 10.000);

INSERT INTO stats_clients (date_debut, date_fin, volume_stocke_m3, duree_moyenne_jours, nb_entrees, nb_sorties, chiffre_affaires, client_id) VALUES
    ('2026-07-01', '2026-07-31', 12.500, 8.50, 1, 1, 150000.00, 2);

INSERT INTO top_produits (date_snapshot, rang, quantite_totale, duree_moyenne_stockage_jours, produit_id) VALUES
    ('2026-07-13', 1, 25.000, 8.50, 1),
    ('2026-07-13', 2, 10.000, 6.00, 2);
