Md Baovola (voiture logistique)

(Vehicule ny anjarako)
-Vehicule
    _Model
        class Voiture (ok)
            declaration attribut rehetra izay ao anaty base : getter sy setter (ok) 
        Tout les model qui existe autour de la vehicule
    _repository
        initialisation an'ilay repository (ok) 

    _service 
        _fonction liste voiture (ok)
        _fonction ajoute voiture (ok)
        _fonction suprimer voiture 
        _fonction modifier voiture

    _Controller 
        _lien vehicule (ok)
	        Liste véhicule (ok)

	Disponibilité an inlay véhicule

    Ajouter un véhicule

    Modifier un véhicule

    Affectation à une mission
        Il choisit
        un chauffeur
        un véhicule

    Historique
        Chaque mission ajoute une ligne

    Panne

    Pendant une mission

    Le chauffeur téléphone.

    Le responsable ouvre

-Chauffeur
	Liste chauffeur 
	Declaration de panne de la véhicules (mitent Taha mohatra ka en panne tamponna end andalana , atsoina am telepohone ) (vehicule en mission no mise anzay )

-Maintenance
	Misy listę An’ilay voiture rehetra ao am entrepôt
	Verification du kilométrage d’ une véhicule (Jour non ouvrable ny anaovana an’ilay maintenance) 
	reparation an ilay voiture en cas de panne 
	
-Livraison
	Liste zavatra tokony livrena (Manisy boutton eo akaikiny hoe tokony livrena )
	
-Facturation
	calcule an inlay frais livraison par rapport au detail an inlay entant


j'ai modifier le zone livraison , mode_calcul_livraison , 
tarif_livraison , 
livraison, 
et de rajouter des nouvelle table , commune , entrpot

et une table de geocodingcache : Cette table te permettra de ne pas appeler Google Maps à chaque création de livraison. Lorsqu'une zone est utilisée pour la première fois, tu récupères ses coordonnées via l'API, tu les enregistres, puis les prochaines livraisons utilisent directement ces coordonnées, ce qui réduit les coûts et accélère les calculs. C'est une pratique courante dans les applications de logistique.