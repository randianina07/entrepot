-- ============================================================================
-- JEU DE DONNEES DE TEST (SEED DATA)
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. UTILISATEURS (Internes et Clients)
--    Note : Les mots de passe sont fictifs. Les roles de reference doivent exister.
-- ----------------------------------------------------------------------------
INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, actif) VALUES
    ('admin@entrepot.com', 'hash_admin_2026', (SELECT id FROM roles WHERE code = 'ADMIN'), TRUE),
    ('gestionnaire1@entrepot.com', 'hash_gest_1', (SELECT id FROM roles WHERE code = 'GESTIONNAIRE'), TRUE),
    ('logistique@entrepot.com', 'hash_log_1', (SELECT id FROM roles WHERE code = 'RESPONSABLE_LOGISTIQUE'), TRUE),
    ('compta@entrepot.com', 'hash_compta_1', (SELECT id FROM roles WHERE code = 'COMPTABLE'), TRUE),
    -- Clients
    ('client.alfa@gmail.com', 'hash_client_alfa', (SELECT id FROM roles WHERE code = 'CLIENT'), TRUE),
    ('client.beta@corporate.com', 'hash_client_beta', (SELECT id FROM roles WHERE code = 'CLIENT'), TRUE),
    ('client.gamma@distrib.com', 'hash_client_gamma', (SELECT id FROM roles WHERE code = 'CLIENT'), TRUE);

INSERT INTO utilisateurs_info (utilisateur_id, nom, prenom, numero, adresse, secteur) VALUES
    ((SELECT id FROM utilisateurs WHERE email = 'admin@entrepot.com'), 'Dupont', 'Jean', '+33102030405', '10 Rue de l''Entrepot', 'Direction'),
    ((SELECT id FROM utilisateurs WHERE email = 'gestionnaire1@entrepot.com'), 'Martin', 'Pierre', '+33611223344', '42 Avenue des Palettes', 'Logistique Interne'),
    -- Profils Clients
    ((SELECT id FROM utilisateurs WHERE email = 'client.alfa@gmail.com'), 'Alfa', 'Societe', '+33699887766', 'Z.I. Secteur Nord, Lot 4', 'Agroalimentaire'),
    ((SELECT id FROM utilisateurs WHERE email = 'client.beta@corporate.com'), 'Beta Corp', 'Logistique', '+33144556677', '99 Boulevard HighTech', 'Industriel'),
    ((SELECT id FROM utilisateurs WHERE email = 'client.gamma@distrib.com'), 'Gamma Distribution', 'Commercial', '+33388776655', 'Parc d''activites Sud', 'Grande Distribution');

-- ----------------------------------------------------------------------------
-- 2. PRODUITS
-- ----------------------------------------------------------------------------
INSERT INTO types_produits (code, libelle) VALUES
    ('ALIMENTAIRE', 'Produits Alimentaires'),
    ('INDUSTRIEL', 'Composants Industriels'),
    ('SENSIBLE', 'Materiel Sensible Electronique'),
    ('VALEUR', 'Marchandises a Haute Valeur');

INSERT INTO produits (code, nom, description, type_produit_id, volume_unitaire_m3, poids_unitaire_kg, actif) VALUES
    ('PROD-ALIM-01', 'Palette de Tomates', 'Tomates fraiches en caisses', (SELECT id FROM types_produits WHERE code = 'ALIMENTAIRE'), 1.2000, 400.000, TRUE),
    ('PROD-ALIM-02', 'Lots de Fromages', 'Produits laitiers sous chaine du froid', (SELECT id FROM types_produits WHERE code = 'ALIMENTAIRE'), 0.8000, 250.000, TRUE),
    ('PROD-IND-01', 'Bobine de Cable Cuivre', 'Bobine industrielle pour chantier', (SELECT id FROM types_produits WHERE code = 'INDUSTRIEL'), 2.5000, 1200.000, TRUE),
    ('PROD-SEC-01', 'Carton Smartphones X100', 'Electronique securisee de forte valeur', (SELECT id FROM types_produits WHERE code = 'SENSIBLE'), 0.1500, 25.000, TRUE);

-- ----------------------------------------------------------------------------
-- 3. STRUCTURE DE STOCKAGE (Zones & Emplacements)
-- ----------------------------------------------------------------------------
-- Configuration des types de zones
UPDATE types_zone SET type_produit_id = (SELECT id FROM types_produits WHERE code = 'ALIMENTAIRE') WHERE code = 'CHF';
UPDATE types_zone SET type_produit_id = (SELECT id FROM types_produits WHERE code = 'SENSIBLE') WHERE code = 'SEC';

-- Zones (Niveau 2)
INSERT INTO zones (code, libelle, type_zone_id, volume_total_m3) VALUES
    ('Z-FRIGO-A', 'Allee A - Chambre Froide', (SELECT id FROM types_zone WHERE code = 'CHF'), 50.000),
    ('Z-STD-B', 'Allee B - Rayonnage Standard', (SELECT id FROM types_zone WHERE code = 'ETA'), 200.000),
    ('Z-SEC-C', 'Allee C - Zone Securisee', (SELECT id FROM types_zone WHERE code = 'SEC'), 30.000),
    ('Z-SOL-D', 'Zone au Sol - Charges Lourdes', (SELECT id FROM types_zone WHERE code = 'SOL'), 500.000);

-- Emplacements precis (Niveau 3)
INSERT INTO emplacements (code, zone_id, capacite_volume_m3, actif) VALUES
    ('CHF-A1-N1', (SELECT id FROM zones WHERE code = 'Z-FRIGO-A'), 5.000, TRUE),
    ('CHF-A1-N2', (SELECT id FROM zones WHERE code = 'Z-FRIGO-A'), 5.000, TRUE),
    ('ETA-B1-N1', (SELECT id FROM zones WHERE code = 'Z-STD-B'), 10.000, TRUE),
    ('ETA-B1-N2', (SELECT id FROM zones WHERE code = 'Z-STD-B'), 10.000, TRUE),
    ('SEC-C1-N1', (SELECT id FROM zones WHERE code = 'Z-SEC-C'), 3.000, TRUE),
    ('SOL-D1-P1', (SELECT id FROM zones WHERE code = 'Z-SOL-D'), 50.000, TRUE),
    ('SOL-D1-P2', (SELECT id FROM zones WHERE code = 'Z-SOL-D'), 50.000, TRUE);

-- ----------------------------------------------------------------------------
-- 4. CONTRATS & FACTURATION
-- ----------------------------------------------------------------------------
-- Tarifs de zone de reference
INSERT INTO tarifs_zone (type_zone_id, unite_duree_id, prix_m3, date_debut_validite, date_fin_validite) VALUES
    ((SELECT id FROM types_zone WHERE code = 'CHF'), (SELECT id FROM unites_duree WHERE code = 'MOIS'), 25.00, '2026-01-01', NULL),
    ((SELECT id FROM types_zone WHERE code = 'ETA'), (SELECT id FROM unites_duree WHERE code = 'MOIS'), 12.00, '2026-01-01', NULL),
    ((SELECT id FROM types_zone WHERE code = 'SEC'), (SELECT id FROM unites_duree WHERE code = 'MOIS'), 45.00, '2026-01-01', NULL),
    ((SELECT id FROM types_zone WHERE code = 'SOL'), (SELECT id FROM unites_duree WHERE code = 'JOUR'), 0.50, '2026-01-01', NULL);

-- Demande de stockage 1 (Abonne - Client Alfa - Frigo)
INSERT INTO demandes_stockage (utilisateur_id, type_zone_id, type_contrat_id, volume_espace_m3, date_debut, date_fin) VALUES
    ((SELECT id FROM utilisateurs WHERE email = 'client.alfa@gmail.com'), (SELECT id FROM types_zone WHERE code = 'CHF'), (SELECT id FROM types_contrat WHERE code = 'ABONNE'), 20.000, '2026-01-01', '2026-12-31');

INSERT INTO historique_etat_demande (demande_stockage_id, statut_id, date_statut) VALUES
    (1, (SELECT id FROM statuts_demande_stockage WHERE code = 'ACCEPTEE'), '2026-01-01 09:00:00');

INSERT INTO contrats (demande_stockage_id, utilisateur_id, type_contrat_id, date_creation, date_debut, date_fin, description) VALUES
    (1, (SELECT id FROM utilisateurs WHERE email = 'client.alfa@gmail.com'), (SELECT id FROM types_contrat WHERE code = 'ABONNE'), '2026-01-01 10:00:00', '2026-01-01', '2026-12-31', 'Contrat Annuel Stockage Frigo - Alfa');

-- Facture associee a ce contrat (Mois de Janvier)
INSERT INTO factures (contrat_id, mode_paiement_id, volume_espace_m3, prix_facture, date_emission, date_paiement) VALUES
    (1, (SELECT id FROM modes_paiement WHERE code = 'VIREMENT'), 20.000, 500.00, '2026-01-31', '2026-02-05');

-- ----------------------------------------------------------------------------
-- 5. CHAUFFEURS & VEHICULES
-- ----------------------------------------------------------------------------
INSERT INTO chauffeurs (nom, prenom, telephone, numero_permis, date_expiration_permis, actif) VALUES
    ('Gaza', 'Paul', '+33600112233', 'PERMIS-12345-A', '2030-12-31', TRUE),
    ('Diallo', 'Moussa', '+33644556677', 'PERMIS-98765-B', '2028-06-15', TRUE);

INSERT INTO vehicules (immatriculation, marque, modele, annee, capacite_volume_m3, capacite_charge_kg, kilometrage_actuel, type_vehicule_id, statut_vehicule_id) VALUES
    ('AA-123-BB', 'Renault', 'Master Frigo', 2022, 12.000, 1500.00, 45000.00, (SELECT id FROM types_vehicule WHERE code = 'CAMION_FRIGO'), (SELECT id FROM statuts_vehicule WHERE code = 'DISPONIBLE')),
    ('CC-456-DD', 'Iveco', 'Daily Benne', 2021, 25.000, 3500.00, 82000.00, (SELECT id FROM types_vehicule WHERE code = 'CAMION_LEGER'), (SELECT id FROM statuts_vehicule WHERE code = 'DISPONIBLE'));

-- ----------------------------------------------------------------------------
-- 6. MOUVEMENTS DE STOCK & ACQUISITION REELLE (Simulation Triggers)
-- ----------------------------------------------------------------------------

-- Etape A : Creation d'un mouvement d'ENTREE (Reception de marchandises pour Client Alfa)
INSERT INTO mouvements (code, date_mouvement, type_mouvement_id, statut_mouvement_id, client_id, utilisateur_id, notes) VALUES
    ('MOV-2026-0001', '2026-01-02 08:30:00', (SELECT id FROM types_mouvement WHERE code = 'RECEPTION'), (SELECT id FROM statuts_mouvement WHERE code = 'EN_ATTENTE'), (SELECT id FROM utilisateurs WHERE email = 'client.alfa@gmail.com'), (SELECT id FROM utilisateurs WHERE email = 'gestionnaire1@entrepot.com'), 'Arrivee premiere palette tomates');

-- Etape B : Ajout d'une ligne de mouvement (Entree pure -> source_id IS NULL)
INSERT INTO lignes_mouvement (mouvement_id, produit_id, emplacement_source_id, emplacement_dest_id, quantite) VALUES
    (1, (SELECT id FROM produits WHERE code = 'PROD-ALIM-01'), NULL, (SELECT id FROM emplacements WHERE code = 'CHF-A1-N1'), 3.000);

-- Etape C : Passage du statut a "VALIDE" pour declencher le trigger `trg_appliquer_mouvement_valide`
-- Cela inserera/mettra a jour automatiquement la table `stocks_emplacement` !
UPDATE mouvements 
SET statut_mouvement_id = (SELECT id FROM statuts_mouvement WHERE code = 'VALIDE') 
WHERE code = 'MOV-2026-0001';


-- Creation d'un 2eme mouvement (Stockage de Smartphones en zone securisee pour Client Gamma)
INSERT INTO mouvements (code, date_mouvement, type_mouvement_id, statut_mouvement_id, client_id, utilisateur_id, notes) VALUES
    ('MOV-2026-0002', '2026-01-05 14:00:00', (SELECT id FROM types_mouvement WHERE code = 'RECEPTION'), (SELECT id FROM statuts_mouvement WHERE code = 'VALIDE'), (SELECT id FROM utilisateurs WHERE email = 'client.gamma@distrib.com'), (SELECT id FROM utilisateurs WHERE email = 'gestionnaire1@entrepot.com'), 'Arrivee Smartphones sensibles');

INSERT INTO lignes_mouvement (mouvement_id, produit_id, emplacement_source_id, emplacement_dest_id, quantite) VALUES
    (2, (SELECT id FROM produits WHERE code = 'PROD-SEC-01'), NULL, (SELECT id FROM emplacements WHERE code = 'SEC-C1-N1'), 10.000);

-- ----------------------------------------------------------------------------
-- 7. LOGISTIQUE & LIVRAISONS EN VILLE
-- ----------------------------------------------------------------------------
INSERT INTO zones_livraison (libelle, commune, distance_km, tarif_base, actif) VALUES
    ('Secteur Centre Ville', 'Nantes', 5.50, 20.00, TRUE),
    ('Zone Industrielle Nord', 'Carquefou', 18.20, 45.00, TRUE);

INSERT INTO tarifs_livraison (zone_livraison_id, mode_calcul_id, prix_base, prix_par_kg, prix_par_m3, date_debut_validite, date_fin_validite) VALUES
    (1, (SELECT id FROM modes_calcul_livraison WHERE code = 'ZONE'), 20.00, 0.00, 0.00, '2026-01-01', NULL),
    (2, (SELECT id FROM modes_calcul_livraison WHERE code = 'POIDS_ZONE'), 45.00, 0.05, 0.00, '2026-01-01', NULL);

-- Creation d'une mission logistique
INSERT INTO missions_logistiques (reference_mission, date_depart_prevue, date_arrivee_prevue, vehicule_id, chauffeur_id, statut_mission_id, observations) VALUES
    ('MIS-2026-001', '2026-01-06 08:00:00', '2026-01-06 12:00:00', (SELECT id FROM vehicules WHERE immatriculation = 'AA-123-BB'), (SELECT id FROM chauffeurs WHERE numero_permis = 'PERMIS-12345-A'), (SELECT id FROM statuts_mission WHERE code = 'TERMINEE'), 'Livraison matinale produits frais');

-- Ajout d'une livraison dans cette mission
INSERT INTO livraisons (mission_id, client_id, adresse_livraison, zone_livraison_id, poids_total, volume_total, date_prevue, date_livraison, montant_livraison) VALUES
    (1, (SELECT id FROM utilisateurs WHERE email = 'client.alfa@gmail.com'), '12 Rue des Maraichers, Nantes', 1, 400.00, 1.200, '2026-01-06 09:00:00', '2026-01-06 09:15:00', 25.00);

-- Facturation de la livraison
INSERT INTO facturation_livraison (livraison_id, tarif_livraison_id, poids_facture, volume_facture, montant_calcule, montant_final, date_facturation) VALUES
    (1, 1, 400.00, 1.200, 20.00, 25.00, '2026-01-06 14:00:00');

-- Preuve de livraison
INSERT INTO preuves_livraison (livraison_id, date_validation, signature_client, photo_colis, commentaire) VALUES
    (1, '2026-01-06 09:15:00', 'SIG_ELECTRONIQUE_ALFA_4589', '/uploads/2026/01/06/colis1.jpg', 'Livre en parfait etat, RAS.');

-- Historique kilometrique du vehicule
INSERT INTO historique_vehicule (vehicule_id, mission_id, date_depart, date_arrivee, kilometrage_depart, kilometrage_arrivee) VALUES
    ((SELECT id FROM vehicules WHERE immatriculation = 'AA-123-BB'), 1, '2026-01-06 08:00:00', '2026-01-06 11:30:00', 45000.00, 45015.50);

-- Mettre a jour le kilometrage reel du vehicule
UPDATE vehicules SET kilometrage_actuel = 45015.50 WHERE immatriculation = 'AA-123-BB';

-- ----------------------------------------------------------------------------
-- 8. GESTION FINANCIERE - DEPENSES OPERATIONNELLES
-- ----------------------------------------------------------------------------
INSERT INTO depenses (date_depense, categorie_id, montant, description, mode_paiement_id, vehicule_id, utilisateur_id) VALUES
    ('2026-01-05', (SELECT id FROM categories_depense WHERE code = 'CARBURANT'), 85.50, 'Plein gazole Renault Master', (SELECT id FROM modes_paiement WHERE code = 'ESPECES'), (SELECT id FROM vehicules WHERE immatriculation = 'AA-123-BB'), (SELECT id FROM utilisateurs WHERE email = 'logistique@entrepot.com')),
    ('2026-01-15', (SELECT id FROM categories_depense WHERE code = 'MAINTENANCE'), 320.00, 'Revision standard Iveco', (SELECT id FROM modes_paiement WHERE code = 'VIREMENT'), (SELECT id FROM vehicules WHERE immatriculation = 'CC-456-DD'), (SELECT id FROM utilisateurs WHERE email = 'compta@entrepot.com')),
    ('2026-01-31', (SELECT id FROM categories_depense WHERE code = 'ELECTRICITE_FROID'), 1450.00, 'Facture electricite Groupe Froid Janvier', (SELECT id FROM modes_paiement WHERE code = 'VIREMENT'), NULL, (SELECT id FROM utilisateurs WHERE email = 'compta@entrepot.com'));

-- ----------------------------------------------------------------------------
-- 9. HISTORIQUE STATISTIQUES & SNAPSHOTS BI
-- ----------------------------------------------------------------------------
INSERT INTO occupation_espaces (date_snapshot, zone_id, capacite_totale_m3, capacite_occupee_m3) VALUES
    ('2026-01-31', (SELECT id FROM zones WHERE code = 'Z-FRIGO-A'), 50.000, 3.600), -- 3 caisses de tomates * 1.2m3
    ('2026-01-31', (SELECT id FROM zones WHERE code = 'Z-SEC-C'), 30.000, 1.500);

INSERT INTO performance_logistique (date_snapshot, nb_livraisons_total, nb_livraisons_ok, delai_moyen_heures, nb_retards) VALUES
    ('2026-01-31', 25, 24, 1.15, 1);

COMMIT;
-- ============================================================================
-- FIN DU JEU DE DONNEES
-- ============================================================================