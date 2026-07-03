truncate table types_mouvement;
truncate table statuts_mouvement;
truncate table roles;
truncate table utilisateurs;
truncate table utilisateurs_info;
truncate table types_produits;
truncate table produits;
truncate table types_zone;
truncate table zones;
truncate table emplacements;

INSERT INTO types_mouvement (code, libelle, sens) VALUES
    ('ENTREE', 'Entree', 'ENTREE'),
    ('SORTIE', 'Sortie', 'SORTIE'),
    ('TRANSFERT', 'Transfert', 'SORTIE'),
    ('RETOUR', 'Retour', 'ENTREE');

INSERT INTO statuts_mouvement (code, libelle, ordre) VALUES
    ('BROUILLON', 'Brouillon', 1),
    ('VALIDE', 'Valide', 2),
    ('ANNULE', 'Annule', 3);

INSERT INTO roles (code, libelle) VALUES
    ('ADMIN', 'Administrateur'),
    ('GESTIONNAIRE', 'Gestionnaire'),
    ('RESPONSABLE_LOGISTIQUE', 'Responsable Logistique'),
    ('CLIENT', 'Client');

INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, actif, date_creation) VALUES
    ('admin@entrepot.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, true, now()),
    ('client1@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 4, true, now());

INSERT INTO utilisateurs_info (utilisateur_id, nom, prenom, numero, adresse, secteur) VALUES
    (1, 'Admin', 'System', '001234567', 'Adresse Admin', 'Centre'),
    (2, 'Client', 'Test', '002345678', 'Adresse Client', 'Nord');

INSERT INTO types_produits (code, libelle) VALUES
    ('ALIMENTAIRE', 'Alimentaire'),
    ('INDUSTRIEL', 'Industriel');

INSERT INTO produits (code, nom, description, type_produit_id, volume_unitaire_m3, poids_unitaire_kg, actif) VALUES
    ('PROD001', 'Produit A', 'Description produit A', 1, 0.5, 10.0, true),
    ('PROD002', 'Produit B', 'Description produit B', 2, 1.0, 20.0, true);

INSERT INTO types_zone (code, libelle, controle_temperature, acces_restreint) VALUES
    ('ETA', 'Etagere', false, false),
    ('CHF', 'Chambre Froide', true, true);

INSERT INTO zones (code, libelle, type_zone_id, volume_total_m3) VALUES
    ('A1', 'Allee A1', 1, 100.0),
    ('B1', 'Allee B1', 2, 50.0);

INSERT INTO emplacements (code, zone_id, capacite_volume_m3, actif) VALUES
    ('ETA-A1-N1', 1, 10.0, true),
    ('ETA-A1-N2', 1, 10.0, true),
    ('CHF-B1-N1', 2, 5.0, true);