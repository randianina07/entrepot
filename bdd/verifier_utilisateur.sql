-- Verifier les roles
SELECT * FROM roles;

-- Verifier l'utilisateur admin applicatif
SELECT u.id, u.email, u.role_id, r.code AS role_code
FROM utilisateurs u
JOIN roles r ON r.id = u.role_id
WHERE u.email = 'admin@entrepot.com';

-- Creer l'admin si absent (mot de passe : admin123)
INSERT INTO utilisateurs (email, mot_de_passe_hash, role_id, actif, date_creation)
SELECT 'admin@entrepot.com',
       '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6',
       (SELECT id FROM roles WHERE code = 'ADMIN' LIMIT 1),
       true,
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'admin@entrepot.com');

-- Reinitialiser le compte admin applicatif (mot de passe : admin123)
UPDATE utilisateurs
SET mot_de_passe_hash = '$2a$10$dXUCIrzukQ84x0UKkr0xZ.4QBMUoyzdM4Cva9CceR8eCnQXuvOW/6',
    actif = true
WHERE email = 'admin@entrepot.com';
