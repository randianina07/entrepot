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

    try {    
        
        setTimeout(() => {
            loading.textContent = '';
            // const data = await fetch(`/api/zone/${id_zone}`).then(response => response.json());

            for (let niveau = 3 ; niveau >= 1; niveau--) {
                for (let colonne = 1; colonne <= 3; colonne++) {

                    const emplacement = document.createElement('div');
                    emplacement.className = 'emplacement';
                    emplacement.textContent = `${niveau}-${colonne}`;

                    schemaContainer.appendChild(emplacement);

                }
            }


        }, 500); 

    } catch (error) {
        console.error('Erreur lors de la récupération des données de la zone:', error);
    }
    
}