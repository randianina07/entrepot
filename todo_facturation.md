# TO DO FACTURATION

+ [x] Prérequis
    + [x] inserer les parametres.
        + [x] 2 données unités durée 
        + [x] 8 données tarifs zones 
    + [x] ajouter colonne qté emplacement dans la table demande_stockage et contrat

    + [x] créer une table abonnement_stockage : id, utilisateur_id, contrat_id, duree_mois, type_zone_id

+ [] modifier formulaire demande stockage 
    + [x] ajouter nouvel attribut quantiteEmplacement:
      + [x] DemandeStockage
      + [x] Contrat
    + [x] ajouter champ qte_emplacement
    + [] rendre intéractive :
        + [] si abonné : 
            + [] champ date fin stockage supprimé
            + [] champ durée en mois apparait

        + [] si non abonné : 
            + [] champ date fin stockage apparait

+ [] modifier validation demande
    + [] apres creation du contrat, insertion dans la table abonnement (transaction)

+ [] créer profil utilisateur 
    + [] modification mot de passe 
        + [] si admin : modification direct
        + [] si client : formulaire de modification

    + [] liste contrats
        + [] trier par abonnement et normale(paiement direct)
            + [] trier : en cours et terminé
            + [] voir facture (bouton) /mbola tsy atao mandeha fa poziny fotsiny

+ [] facturation 
    + [] calcul d'intervalle de date mois/jour (utiliser une fonction native java pour calculer la durée mois/jour)
    + [] prendre une date pour voir la facture 
        + [] vérifier si la date est équivalente à une date fin
            + [] si non abonnement : calcule de date normale
            + [] si abonnement : 





























