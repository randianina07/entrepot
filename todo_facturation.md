# TO DO FACTURATION

+ [x] Prérequis
    + [x] inserer les parametres.
        + [x] 2 données unités durée 
        + [x] 8 données tarifs zones 
    + [x] ajouter colonne qté emplacement dans la table demande_stockage et contrat

    + [x] créer une table abonnement_stockage : id, utilisateur_id, contrat_id, duree_mois, type_zone_id

+ [] modifier formulaire demande stockage 
    + [] ajouter nouvel attribut quantiteEmplacement:
      + [] DemandeStockage
      + [] Contrat
    + [] ajouter champ qte_emplacement
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

            + [] Comment:
                + [] ProfilController : 
                    + [] afficher la page du profil
                    + [] afficher le formulaire de changement de mot de passe 
                    + [] récupérer les données envoyées par le formulaire
                    + [] appeler le service 

                + [x] UtilisateurService
                    + [x] Créer les méthodes :
                        + [x] Utilisaetur getUtilisateurConnecte() : retourne l'utilisateur connecte 
                        + [x] UtilisateurInfo getProfil() : retourne les informations à afficher sur le profil
                        + [x] void changerMotDePasse() : 
                            - récupère l'utilisateur connecté
                            - vérifie l'ancien mot de passe 
                            - chiffre le nouveau mot de passe 
                            - sauvegarde en base 

                + [] UtilisateurRepository
                    + [] findByEmail()

                + [] AuthDetails
                    + [] Utilisateur utilisateur 

                + [] SecurityContextHolder 
                    + récupérer l'id de l'utilisateur par Spring Security

                + [] Vue :
                    + [] dossier : profil
                        + créer les pages : 
                            + [] index.html
                            + [] changerMotDePasse.html

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





























