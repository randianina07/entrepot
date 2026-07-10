
## Refonte complète de la Navbar (Très important)

La barre de navigation actuelle doit être entièrement repensée afin d'offrir une expérience utilisateur digne d'un logiciel ERP professionnel.

Ne conserve pas la disposition actuelle si elle n'est pas optimale. Réorganise entièrement les éléments tout en conservant les fonctionnalités existantes.

### Objectifs

Créer une navbar :

* moderne
* minimaliste
* élégante
* professionnelle
* responsive
* ergonomique
* cohérente avec le reste de l'application

### Organisation

Réorganiser intelligemment tous les éléments.

Les menus doivent être classés de façon logique.

Par exemple :

* Tableau de bord
* Produits
* Catégories
* Fournisseurs
* Clients
* Entrées de stock
* Sorties de stock
* Mouvements
* Rapports
* Utilisateurs
* Paramètres

Adapter cette organisation aux fonctionnalités réellement présentes dans le projet. Ne pas créer de menus sans correspondance dans le backend.

### Structure

Créer une navigation claire avec :

* logo de l'application
* nom du système
* menu principal
* barre de recherche (si elle existe déjà)
* notifications (si elles existent déjà)
* profil utilisateur
* menu déroulant utilisateur
* bouton de déconnexion

Réorganiser leur position pour une meilleure ergonomie.



### Design

Utiliser :

* des icônes cohérentes (Bootstrap Icons si déjà disponibles) ;
* des espacements harmonieux ;
* des effets de survol (hover) subtils ;
* une animation douce pour les menus déroulants ;
* une indication visuelle claire de la page active ;
* une hiérarchie visuelle des éléments.

### Ergonomie

Mettre en avant les actions les plus importantes.

Regrouper les fonctionnalités similaires.

Supprimer les éléments redondants.

Réduire les clics nécessaires pour accéder aux fonctionnalités principales.

### Contraintes

* Ne créer aucun nouveau fichier.
* Réécrire uniquement les fichiers existants.
* Conserver tous les liens et routes Spring Boot.
* Ne modifier ni les contrôleurs ni le backend.
* Conserver les variables Thymeleaf (`th:*`).
* Préserver toutes les fonctionnalités existantes.

L'objectif est d'obtenir une barre de navigation comparable à celle d'un logiciel ERP moderne comme Odoo, ERPNext, Zoho Inventory, Microsoft Dynamics ou SAP Business One, avec une organisation claire, intuitive et adaptée à une utilisation professionnelle quotidienne.
