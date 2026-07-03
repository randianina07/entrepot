# TO DO

## Fiche Client
Information générale du client
Historique des stocks par client
Contrats de stockage
Durées de stockage contractuelles

### Client
#### Login
+ [] Base:
    + [] table utilisatreurs 

+ [] Créer le modele Utilisateurs 

+ [] UtilisateursService: 
    + [] gestion login 
        + [] si role = client => dasboard
        + [] gestion de mot de passe

+ [] UtilisateursController
    + [] routage pour chaque méthode
 
#### profil client 
+ [] Base 
    + [] tables : type_contrat, modes_paiement, statuts_demande_stockage, demandes_stockage, historique_etat_demande, contrats, statuts_renouvellement, demandes_renouvellement, historique_renouvellement, renouvellements_contrat

+ [] Créer les modeles pour chaque table 

+ [] Creer un formulaire de demande de contrat
    *Nous utiliserons la classe DemandeContratController pour les demandes et DemandeContratController pour les renouvellements de contrat et les actions liées aux contrats*
    + [] DemandeStockageService pour ajouter une nouvelle demande
        + [] DemandeStockageStatutService : insert
        + [] DemandeStockageService : insert
        + [] transaction de demande_stockage et demande_stockage_statut
    + [] DemandeContratController
        + [] post: pour prendre les donnees du formulaire et les enregistrer

+ [] Formulaire de renouvellement de contrat
    + [] DemandecontratService pour ajouter une nouvelle demande
        + [] DemandecontratStatutService : insert
        + [] DemandecontratService : insert
        + [] transaction de demande_contrat et demande_contrat_statut
    + [] ContratController
        + [] post: pour prendre les donnees du formulaire et les enregistrer

+ [] Créer les modeles pour chaque table
+ [] Etat de stock
+ [] Nombre de demandes en attentes
+ [] Nombre de contrat + etat de contrat(en cours, expiré,...)


## BACK OFFICE
### CRUD client 

+ [] Créer formulaire pour inscrire un client 
    + [] Base : 
        + [] table utilisateurs, utilisateurs_info
    + [] classe modele Utilisateurs et Utilisateurs_info
    + [] UtilisateursService : 
        + [] insertion dans la table utilisateurs et utilisateurs_info
        + [] liste, insertion et modification

### CRUD contrat
+ [] Base 
*Nous avons modifié les tables : contrats, historique_renouvellement, renouvellements_contrat afin d'éviter des problèmes liés au coté client*
+ [] tables : type_contrat, contrats, renouvellement_contrats
    

