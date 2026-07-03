-- ============================================================================
-- DONNEES DE REFERENCE - TYPES ET STATUTS DE MOUVEMENT
-- ============================================================================

-- Insérer les types_mouvement
INSERT INTO types_mouvement (id, code, libelle, sens) VALUES
(1, 'ENTREE', 'Entrée de stock', 'ENTREE'),
(2, 'SORTIE', 'Sortie de stock', 'SORTIE'),
(3, 'TRANSFERT', 'Transfert interne', 'ENTREE'),
(4, 'RETOUR', 'Retour client', 'ENTREE');

-- Insérer les statuts_mouvement
INSERT INTO statuts_mouvement (id, code, libelle, ordre) VALUES
(1, 'BROUILLON', 'Brouillon', 1),
(2, 'VALIDE', 'Validé', 2),
(3, 'ANNULE', 'Annulé', 3);
