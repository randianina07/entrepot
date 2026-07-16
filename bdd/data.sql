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