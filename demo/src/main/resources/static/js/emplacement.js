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
        loading.textContent = '';

        const data = await fetch(`/api/zone/${id_zone}`)
            .then(response => response.json());

        console.log('Données de la zone:', data);

        setTimeout(() => {

            for (let niveau = 3; niveau >= 1; niveau--) {
                for (let colonne = 1; colonne <= 3; colonne++) {

                    const emplacement = document.createElement('div');
                    emplacement.className = 'emplacement';

                    for (const stock_emp of data) {
                        const emp = stock_emp.emplacement;

                        if (emp.etage.numero_etage === niveau && emp.colonne === colonne) {

                            emplacement.textContent = stock_emp.produit.nom;

                            if (!emp.actif) {
                                emplacement.classList.add('disponible');
                            } else {
                                emplacement.classList.add('occupe');
                            }

                            break;
                        }
                    }

                    schemaContainer.appendChild(emplacement);
                }
            }

        }, 500);

    } catch (error) {
        console.error('Erreur lors de la récupération des données de la zone:', error);
    }
}