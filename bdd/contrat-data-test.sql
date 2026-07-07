INSERT INTO roles(code, libelle)
VALUES
('ADMIN', 'Administrateur'),
('GESTIONNAIRE', 'Gestionnaire'),
('RESPONSABLE_LOGISTIQUE', 'Responsable logistique'),
('COMPTABLE', 'Comptable'),
('CLIENT', 'Client');

INSERT INTO utilisateurs(
    id,
    email,
    mot_de_passe_hash,
    role_id
)
VALUES(
    1,
    'admin@entrepot.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiM9mQbn5Pqz0D5h5N6wsPBRDsoZ6eG',
    1
);

INSERT INTO types_contrat(code, libelle)
VALUES
('ABONNE', 'Abonné'),
('NON_ABONNE', 'Non abonné');