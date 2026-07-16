INSERT INTO unites_duree (code, libelle) VALUES
('JOUR', 'Jour'),
('MOIS', 'Mois');

INSERT INTO tarifs_zone (
    type_zone_id,
    unite_duree_id,
    prix_m3,
    date_debut_validite,
    date_fin_validite
) VALUES
-- Frigo
(1, 1, 1500,  '2026-01-01', NULL),
(1, 2, 35000, '2026-01-01', NULL),

-- Entana sarobidy
(2, 1, 1500,  '2026-01-01', NULL),
(2, 2, 35000, '2026-01-01', NULL),

-- Étagère
(3, 1, 1000,  '2026-01-01', NULL),
(3, 2, 20000, '2026-01-01', NULL),

-- Zone 4
(4, 1, 1000,  '2026-01-01', NULL),
(4, 2, 20000, '2026-01-01', NULL);