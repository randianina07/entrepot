# Configuration Spring Security - Tech-Entrepôt

## Vue d'ensemble

Spring Security a été configuré pour:
1. Forcer l'authentification avant l'accès aux pages
2. Gérer les accès selon les rôles des utilisateurs
3. Utiliser l'entité Utilisateur existante pour l'authentification

## Rôles et Permissions

### Rôles Définis
- **ADMIN** - Accès complet à toutes les fonctionnalités
- **GESTIONNAIRE** - Gestion complète sauf certaines fonctions admin
- **RESPONSABLE_LOGISTIQUE** - Gestion de la logistique (véhicules, chauffeurs, livraisons, missions, maintenances)
- **COMPTABLE** - Accès aux statistiques et rapports
- **CLIENT** - Accès limité à ses propres données

### Permissions par Rôle

| Fonctionnalité | ADMIN | GESTIONNAIRE | RESPONSABLE_LOGISTIQUE | COMPTABLE | CLIENT |
|---------------|-------|--------------|-------------------------|-----------|--------|
| Login/Inscription | ✓ | ✓ | ✓ | ✓ | ✓ |
| Tableau de bord | ✓ | ✓ | ✓ | ✓ | ✓ |
| Mouvements | ✓ | ✓ | ✓ | ✓ | ✓ |
| Stockages | ✓ | ✓ | ✓ | ✓ | ✓ |
| Statistiques BI | ✓ | ✓ | ✗ | ✓ | ✗ |
| Chauffeurs | ✓ | ✓ | ✓ | ✗ | ✗ |
| Véhicules | ✓ | ✓ | ✓ | ✗ | ✗ |
| Livraisons | ✓ | ✓ | ✓ | ✗ | ✗ |
| Missions | ✓ | ✓ | ✓ | ✗ | ✗ |
| Maintenances | ✓ | ✓ | ✓ | ✗ | ✗ |
| Clients | ✓ | ✓ | ✗ | ✗ | ✗ |
| Utilisateurs | ✓ | ✓ | ✗ | ✗ | ✗ |
| Contrats | ✓ | ✓ | ✗ | ✗ | ✗ |

## Configuration Technique

### 1. Dépendance Maven
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. SecurityConfig
Localisation: `src/main/java/com/entrepot/gestion/config/SecurityConfig.java`

**Règles d'accès:**
- `/login`, `/utilisateurs/nouveau`, `/utilisateurs/enregistrer` → Accessibles à tous
- `/css/**`, `/js/**`, `/images/**`, `/webjars/**` → Ressources statiques accessibles
- `/`, `/mouvements/**` → Authentification requise
- `/accueil`, `/choose-type_zones`, `/type-zone/**`, `/recherche`, `/faire-recherche` → Authentification requise
- `/dashboard/**` → ADMIN, GESTIONNAIRE, COMPTABLE
- `/chauffeurs/**`, `/vehicules/**`, `/livraisons/**`, `/missions/**`, `/maintenances/**` → ADMIN, GESTIONNAIRE, RESPONSABLE_LOGISTIQUE
- `/clients/**`, `/utilisateurs/**` → ADMIN, GESTIONNAIRE
- `/contrats/**` → ADMIN, GESTIONNAIRE

### 3. CustomUserDetailsService
Localisation: `src/main/java/com/entrepot/gestion/security/CustomUserDetailsService.java**

**Fonctionnalités:**
- Charge l'utilisateur depuis la base de données via email
- Vérifie si l'utilisateur est actif
- Convertit le rôle en autorité Spring Security (ROLE_CODE)
- Gère les exceptions pour utilisateurs non trouvés ou désactivés

### 4. UtilisateurRepository
Localisation: `src/main/java/com/entrepot/gestion/repository/UtilisateurRepository.java`

**Méthodes ajoutées:**
- `Optional<Utilisateur> findByEmail(String email)` - Recherche par email
- `boolean existsByEmail(String email)` - Vérifie l'existence

### 5. Entité Utilisateur
Localisation: `src/main/java/com/entrepot/gestion/model/Utilisateur.java`

**Champs utilisés pour l'authentification:**
- `email` - Identifiant unique
- `motDePasseHash` - Mot de passe hashé
- `role` - Relation avec l'entité Role
- `actif` - Statut du compte

## Configuration application.properties

```properties
# Security
spring.security.user.name=admin
spring.security.user.password=admin
spring.security.user.roles=ADMIN
```

## Flux d'Authentification

1. **Accès à une page protégée**
   - Spring Security intercepte la requête
   - Redirection vers `/login` si non authentifié

2. **Soumission du formulaire de login**
   - LoginController reçoit les credentials
   - CustomUserDetailsService charge l'utilisateur
   - DaoAuthenticationProvider vérifie le mot de passe
   - Si succès → Redirection vers `/mouvements/tableau-de-bord`
   - Si échec → Redirection vers `/login?error=true`

3. **Accès selon le rôle**
   - SecurityConfig vérifie les autorisations
   - Si autorisé → Accès à la page
   - Si non autorisé → Erreur 403 (Forbidden)

## Pages Publiques

Les pages suivantes sont accessibles sans authentification:
- `/login` - Page de connexion
- `/utilisateurs/nouveau` - Formulaire d'inscription
- `/utilisateurs/enregistrer` - Enregistrement utilisateur

## Prochaines Étapes

1. **Créer un utilisateur admin par défaut** lors du démarrage de l'application
2. **Intégrer BCrypt** pour le hashage des mots de passe dans UtilisateurController
3. **Activer CSRF** pour la sécurité des formulaires
4. **Ajouter la gestion des sessions** et timeout
5. **Implémenter "Remember Me"** fonctionnalité
6. **Ajouter la déconnexion automatique** après inactivité

## Test

Pour tester l'authentification:
1. Redémarrez l'application après avoir ajouté Spring Security
2. Accédez à `http://localhost:8080` → Redirection vers `/login`
3. Connectez-vous avec un utilisateur existant dans la base de données
4. Vérifiez que l'accès est limité selon le rôle
