# TODO — Mouvements (Entrees / Sorties)

**Projet :** Système de Gestion d'Entrepôt
**Stack :** Spring Boot + PostgreSQL + Thymeleaf
**Date :** 28/06/2026

---

## SANDA — Backend (Couche Donnees & Logique Metier) — 38h

### 1. Entites JPA — 6h
- [x] Creer l'entite `TypeMouvement` (id, code, libelle, sens)
- [x] Creer l'entite `StatutMouvement` (id, code, libelle, ordre)
- [x] Creer l'entite `Mouvement` (id, code, dateMouvement, typeMouvement, statutMouvement, client, utilisateur, notes)
- [x] Creer l'entite `LigneMouvement` (id, mouvement, produit, emplacementSource, emplacementDest, quantite)
- [x] Creer l'entite `FluxEntreesSorties` (id, date, typeFlux, typeDetail, quantite, volumeM3, mouvement)
- [x] Annoter toutes les relations JPA (`@ManyToOne`, `@OneToMany`, `@JoinColumn`)

### 2. Repositories — 3h
- [x] Creer `TypeMouvementRepository` (extends JpaRepository)
- [x] Creer `StatutMouvementRepository` (extends JpaRepository)
- [x] Creer `MouvementRepository` avec methodes :
  - `findByTypeMouvement_Sens(String sens)`
  - `findByStatutMouvement_Code(String code)`
  - `findByClient_Id(Long clientId)`
  - `findByDateMouvementBetween(LocalDateTime debut, LocalDateTime fin)`
- [x] Creer `LigneMouvementRepository` avec `findByMouvement_Id(Long mouvementId)`
- [x] Creer `FluxEntreesSortiesRepository` avec `findByDateBetween(LocalDate debut, LocalDate fin)`

### 3. DTOs — 2h
- [x] Creer `MouvementCreateDTO` (typeMouvementId, clientId, notes)
- [x] Creer `LigneMouvementDTO` (produitId, emplacementSourceId, emplacementDestId, quantite)
- [x] Creer `MouvementDetailDTO` (toutes les infos mouvement + liste des lignes)
- [x] Creer `MouvementListDTO` (version allegee pour la liste)
- [x] Creer `MouvementFiltreDTO` (type, statut, clientId, dateDebut, dateFin)

### 4. Services — Creation — 5h
- [x] Creer `MouvementService`
- [x] Fonction `genererCodeMouvement()` → genère un code unique (ex: MVT-20260628-0001)
- [x] Fonction `creerMouvement(MouvementCreateDTO dto, Long utilisateurId)` → cree le mouvement en statut BROUILLON
- [x] Fonction `ajouterLigne(Long mouvementId, LigneMouvementDTO dto)` → ajoute une ligne au mouvement
- [x] Fonction `supprimerLigne(Long ligneMouvementId)` → supprime une ligne

### 5. Services — Validations Metier — 6h
- [x] Fonction `verifierStockSuffisant(Long emplacementId, Long produitId, Double quantite)` → lève exception si stock insuffisant (SORTIE)
- [x] Fonction `verifierCapaciteEmplacement(Long emplacementId, Double volumeAjoute)` → lève exception si capacite depassee (ENTREE)
- [x] Fonction `validerMouvement(Long mouvementId)` → passe le statut à VALIDE + appelle mise à jour stock
- [x] Fonction `annulerMouvement(Long mouvementId)` → passe le statut à ANNULE + rollback stock si dejà valide

### 6. Services — Mise à Jour Stock — 7h
- [x] Fonction `mettreAJourStockEntree(LigneMouvement ligne)` → incrementer `stocks_emplacement`
- [x] Fonction `mettreAJourStockSortie(LigneMouvement ligne)` → decrementer `stocks_emplacement`
- [x] Fonction `mettreAJourStockTransfert(LigneMouvement ligne)` → decrementer source + incrementer destination dans une seule transaction (`@Transactional`)
- [x] Fonction `rollbackStock(LigneMouvement ligne)` → annulation de la mise à jour

### 7. Services — Statistiques — 5h
- [x] Fonction `enregistrerFlux(Mouvement mouvement)` → insère dans `flux_entrees_sorties` après validation
- [x] Fonction `mettreAJourStatsClient(Long clientId)` → recalcule nb_entrees, nb_sorties, volume_stocke_m3 dans `stats_clients`
- [x] Fonction `mettreAJourTopProduits()` → recalcule le classement dans `top_produits`

### 8. Controllers REST / Web (Sanda) — 3h
- [x] Creer `MouvementController`
- [x] `GET /mouvements` → liste avec filtres (paramètres optionnels en `@RequestParam`)
- [x] `GET /mouvements/{id}` → detail d'un mouvement
- [x] `POST /mouvements` → creer un mouvement
- [x] `POST /mouvements/{id}/lignes` → ajouter une ligne
- [x] `DELETE /mouvements/{id}/lignes/{ligneId}` → supprimer une ligne
- [x] `POST /mouvements/{id}/valider` → valider le mouvement
- [x] `POST /mouvements/{id}/annuler` → annuler le mouvement

### 9. Donnees de Reference (Script SQL / Data.sql) — 1h
- [x] Inserer les `types_mouvement` : ENTREE, SORTIE, TRANSFERT, RETOUR
- [x] Inserer les `statuts_mouvement` : BROUILLON (1), VALIDE (2), ANNULE (3)

---

## MIANGOLA — Frontend (Thymeleaf + Controllers Web) — 36h

### 1. Controllers Web (Thymeleaf) — 5h
- [x] Creer `MouvementViewController`
- [x] `GET /mouvements/liste` → renvoie la vue liste avec les mouvements filtres
- [x] `GET /mouvements/{id}/detail` → renvoie la vue detail d'un mouvement
- [x] `GET /mouvements/nouveau/entree` → renvoie le formulaire creation ENTREE
- [x] `GET /mouvements/nouveau/sortie` → renvoie le formulaire creation SORTIE
- [x] `POST /mouvements/nouveau` → traite le formulaire et redirige
- [x] `GET /mouvements/tableau-de-bord` → renvoie la vue dashboard

### 2. Page : Liste des Mouvements (`liste.html`) — 5h
- [x] Creer le template Thymeleaf `mouvements/liste.html`
- [x] Tableau avec colonnes : Code, Date, Type, Statut, Client, Nb Lignes, Actions
- [x] Badge colore selon le statut (BROUILLON=gris, VALIDE=vert, ANNULE=rouge)
- [x] Formulaire de filtres en haut : type, statut, client, date debut, date fin
- [x] Bouton "Appliquer les filtres" (soumission GET)
- [x] Bouton "Nouveau mouvement Entree" et "Nouveau mouvement Sortie"
- [x] Pagination (liens page precedente / suivante)
- [x] Lien "Voir detail" sur chaque ligne du tableau

### 3. Page : Detail d'un Mouvement (`detail.html`) — 4h
- [x] Creer le template Thymeleaf `mouvements/detail.html`
- [x] Section en-tête : code, date, type, statut, client, operateur, notes
- [x] Tableau des lignes : produit, emplacement source, emplacement destination, quantite
- [x] Bouton "Valider" (visible si statut = BROUILLON, rôle RESPONSABLE_LOGISTIQUE)
- [x] Bouton "Annuler" avec confirmation JavaScript (`confirm()`)
- [x] Bouton "Retour à la liste"
- [x] Message de succès/erreur via `th:if` sur les attributs flash (`redirectAttributes`)

### 4. Page : Formulaire Creation ENTREE (`form-entree.html`) — 7h
- [x] Creer le template Thymeleaf `mouvements/form-entree.html`
- [x] Champ : selection du client (liste deroulante `<select>`)
- [x] Champ : notes (textarea)
- [x] Section dynamique "Lignes" :
  - [x] Selection du produit (liste deroulante)
  - [x] Selection de l'emplacement destination (liste deroulante avec capacite restante affichee)
  - [x] Champ quantite (input number)
  - [x] Bouton "Ajouter une ligne" (JS pour dupliquer le bloc)
  - [x] Bouton "Supprimer" sur chaque ligne
- [x] Bouton "Enregistrer en brouillon"
- [x] Affichage des erreurs de validation sous chaque champ (`th:errors`)

### 5. Page : Formulaire Creation SORTIE (`form-sortie.html`) — 7h
- [x] Creer le template Thymeleaf `mouvements/form-sortie.html`
- [x] Champ : selection du client (liste deroulante)
- [x] Section dynamique "Lignes" :
  - [x] Selection du produit
  - [x] Selection de l'emplacement source (afficher le stock disponible en temps reel via appel AJAX ou th:text)
  - [x] Champ quantite avec validation côte client (max = stock disponible)
  - [x] Bouton "Ajouter une ligne" / "Supprimer"
- [x] Bouton "Enregistrer en brouillon"
- [x] Affichage des erreurs de validation

### 6. Page : Tableau de Bord (`dashboard.html`) — 4h
- [x] Creer le template Thymeleaf `mouvements/dashboard.html`
- [x] Widget "Entrees aujourd'hui" → nb de mouvements ENTREE valides du jour
- [x] Widget "Sorties aujourd'hui" → nb de mouvements SORTIE valides du jour
- [x] Widget "En attente de validation" → nb de mouvements BROUILLON
- [x] Tableau "5 derniers mouvements" avec lien vers le detail
- [x] Alerte si un stock emplacement est < 10% de la capacite totale

### 7. Fragments Thymeleaf Communs — 3h
- [x] Creer `fragments/navbar.html` → barre de navigation commune
- [x] Creer `fragments/alerts.html` → bloc messages succès/erreur reutilisable
- [x] Creer `fragments/pagination.html` → composant pagination reutilisable

### 8. Export — 1h
- [x] Fonction `exporterCSV(MouvementFiltreDTO filtre)` dans le controller → genère et telecharge un fichier CSV de la liste filtree


