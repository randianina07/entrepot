const loading = document.getElementById('loading-message');

function ShowEmplacement(id_zone) {

    document.getElementById("popup").style.display = "flex";
    chargerSchemaEtagere(id_zone);

}

function closePopup() {
    document.getElementById("popup").style.display = "none";
    loading.textContent = 'Loading...';
}

async function chargerSchemaEtagere(id_zone) {

    const schemaContainer = document.getElementById('etagere');
    schemaContainer.innerHTML = '';
    const detailProduit = document.createElement('div');

    try {
        loading.textContent = '';

        const data = await fetch(`/api/zone/${id_zone}`)
            .then(response => response.json());

        console.log('Données de la zone:', data);

        setTimeout(() => {

            for (let niveau = 3; niveau >= 1; niveau--) {
                for (let colonne = 1; colonne <= 3; colonne++) {

                    let produitTrouve = false;
                    const emplacement = document.createElement('div');
                    emplacement.className = 'emplacement';
                    
                    for (const stock_emp of data) {
                        const emp = stock_emp.emplacement;
                        const produit = stock_emp.produit;

                        if (emp.etage.numero_etage === niveau && emp.colonne === colonne) {

                            produitTrouve = true;
                            emplacement.textContent = stock_emp.produit.nom;

                            if (emp.actif) {
                                emplacement.classList.add('occupe');
                            }
                            
                            emplacement.addEventListener('click' , () => {         
                                
                                if (emplacement.textContent == null) {
                                    
                                    console.log('blbabalbal');

                                }
                                DetailProduitPopUp(detailProduit , produit);

                            });
                            
                            break;
                        }
                    }

                    if (!produitTrouve) {

                        emplacement.classList.add("vide");

                        emplacement.addEventListener("click", () => {
                            alert("Aucun produit n'est stocke dans cet emplacement.");
                        });
                    }


                    schemaContainer.appendChild(emplacement);
                }
            }

        }, 500);

    } catch (error) {
        console.error('Erreur lors de la récupération des données de la zone:', error);
    }
}

function createRow(label, value){

    const row = document.createElement("div");
    row.className = "product-row";

    const l = document.createElement("span");
    l.className = "product-label";
    l.textContent = label;

    const v = document.createElement("span");
    v.className = "product-value";
    v.textContent = value;

    row.appendChild(l);
    row.appendChild(v);

    return row;
}

function DetailProduitPopUp(detailProduit,produit) {

        const popupContent = document.createElement("div");
        popupContent.className = "popup-content";
        
        const header = document.createElement("div");
        header.className = "popup-header";
        
        const titre = document.createElement("h2");
        titre.textContent = "Details du produit";

        const close = document.createElement("span");
        close.className = "close-popup";
        close.innerHTML = "&times;";
        header.appendChild(titre);
        header.appendChild(close);
        
        const body = document.createElement("div");
        
        body.className = "popup-body";
        body.appendChild(createRow("Nom: ", produit.nom));
        body.appendChild(createRow("Code: ", produit.code));
        body.appendChild(createRow("Description: ", produit.description));
        body.appendChild(createRow("Volume: ", produit.volume_unitaire_m3+" m³"));
        
        popupContent.appendChild(header);
        popupContent.appendChild(body);
        
        detailProduit.innerHTML = "";
        detailProduit.className = "popup-detail-produit";
        detailProduit.appendChild(popupContent);
        
        
        close.addEventListener("click", () => {
            detailProduit.remove();
        });
        
        detailProduit.addEventListener("click", (e) => {
            if(e.target === detailProduit){
                detailProduit.remove();
            }
        });
        
        document.body.appendChild(detailProduit);

}