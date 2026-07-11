-- Création de l'utilisateur administrateur
INSERT INTO utilisateurs (
    email,
    mot_de_passe_hash,
    role_id
)
VALUES (
    'admin@entrepot.com',
    '$2a$10$',
    '1'
);

-- Informations de l'administrateur
INSERT INTO utilisateurs_info (
    utilisateur_id,
    nom,
    prenom,
    numero,
    adresse,
    secteur
)
VALUES (
    (SELECT id FROM utilisateurs WHERE email = 'admin@entrepot.com'),
    'Admin',
    'Super',
    '0340000000',
    'Antananarivo',
    'Administration'
);