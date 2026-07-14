(() => {
    const DEFAULT_LEVELS = [3, 2, 1];
    const DEFAULT_COLUMNS = [1, 2, 3];

    function getElement(id) {
        return document.getElementById(id);
    }

    function formatValue(value, fallback = '-') {
        return value === null || value === undefined || value === '' ? fallback : value;
    }

    function openModal() {
        const popup = getElement('popup');
        if (popup) {
            popup.classList.add('is-open');
            popup.setAttribute('aria-hidden', 'false');
        }
    }

    function closeModal() {
        const popup = getElement('popup');
        if (popup) {
            popup.classList.remove('is-open');
            popup.setAttribute('aria-hidden', 'true');
        }

        const loadingMessage = getElement('loading-message');
        if (loadingMessage) {
            loadingMessage.textContent = 'Selectionne une zone pour afficher ses emplacements.';
            loadingMessage.className = 'empty-state';
        }

        const grid = getElement('etagere');
        if (grid) {
            grid.innerHTML = '';
        }

        const detail = getElement('detailProduit');
        if (detail) {
            detail.innerHTML = `
                <div class="detail-title">Details du produit</div>
                <div class="detail-muted">Clique sur un emplacement occupe pour afficher ses informations.</div>
            `;
        }
    }

    function renderDetail(stock) {
        const detail = getElement('detailProduit');
        if (!detail) {
            return;
        }

        const stocks = Array.isArray(stock) ? stock : (stock ? [stock] : []);

        if (stocks.length === 0) {
            detail.innerHTML = `
                <div class="detail-title">Emplacement vide</div>
                <div class="detail-muted">Aucun produit n'est associe a cet emplacement.</div>
            `;
            return;
        }

        const firstStock = stocks[0] || {};
        const emplacement = firstStock.emplacement || {};

        detail.innerHTML = `
            <div class="detail-title">${formatValue(emplacement.code, 'Emplacement')}</div>
            <div class="detail-muted mb-3">${stocks.length} produit(s) stocke(s) dans cet emplacement</div>
            <div class="storage-details">
                <span><strong>Emplacement :</strong> ${formatValue(emplacement.code, '-')}</span>
                <span><strong>Etage :</strong> ${formatValue(emplacement.etage?.numero_etage, '-')}</span>
                <span><strong>Colonne :</strong> ${formatValue(emplacement.colonne, '-')}</span>
            </div>
            <div class="mt-3">
                <div class="detail-title" style="font-size: 1rem;">Produits</div>
                <div class="storage-details">
                    ${stocks.map((item) => {
                        const produit = item.produit || {};
                        return `
                            <div class="storage-detail-card p-3 mb-2">
                                <div><strong>${formatValue(produit.nom, 'Produit inconnu')}</strong></div>
                                <div class="detail-muted">Code : ${formatValue(produit.code, 'Code non disponible')}</div>
                                <div class="detail-muted">Quantite : ${formatValue(item.quantite, '0')}</div>
                                <div class="detail-muted">Volume unitaire : ${formatValue(produit.volumeUnitaireM3, '-')} m3</div>
                                <div class="detail-muted">Description : ${formatValue(produit.description, 'Aucune description')}</div>
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    }

    function buildSlot(stocks, level, column) {
        const slot = document.createElement('button');
        slot.type = 'button';
        slot.className = 'emplacement-slot';

        const items = Array.isArray(stocks) ? stocks : (stocks ? [stocks] : []);
        const firstStock = items[0] || null;
        const emplacement = firstStock?.emplacement || null;
        const productName = firstStock?.produit?.nom;

        if (items.length > 0 && emplacement) {
            slot.classList.add('is-occupied');
            slot.innerHTML = `
                <div class="slot-legend">
                    <span class="chip is-success">Occupe</span>
                    <span>${items.length} produit(s)</span>
                </div>
                <div>
                    <div class="slot-code">${formatValue(emplacement.code, 'Emplacement')}</div>
                    <div class="detail-muted">${formatValue(productName, 'Produit inconnu')}${items.length > 1 ? ` + ${items.length - 1} autre(s)` : ''}</div>
                </div>
            `;
            slot.addEventListener('click', () => renderDetail(items));
            return slot;
        }

        slot.classList.add('is-empty');
        slot.innerHTML = `
            <div class="slot-legend">
                <span class="chip">Vide</span>
                <span>L${level} / C${column}</span>
            </div>
            <div>
                <div class="slot-code">Disponible</div>
                <div class="detail-muted">Aucun produit stocke</div>
            </div>
        `;
        slot.addEventListener('click', () => renderDetail(null));
        return slot;
    }

    function renderGrid(data) {
        const grid = getElement('etagere');
        if (!grid) {
            return;
        }

        grid.innerHTML = '';

        const lookup = new Map();
        data.forEach((stock) => {
            const level = stock?.emplacement?.etage?.numero_etage;
            const column = stock?.emplacement?.colonne;
            if (level !== undefined && level !== null && column !== undefined && column !== null) {
                const key = `${level}-${column}`;
                const items = lookup.get(key) || [];
                items.push(stock);
                lookup.set(key, items);
            }
        });

        DEFAULT_LEVELS.forEach((level) => {
            DEFAULT_COLUMNS.forEach((column) => {
                const stocks = lookup.get(`${level}-${column}`) || [];
                grid.appendChild(buildSlot(stocks, level, column));
            });
        });
    }

    async function chargerSchemaEtagere(idZone) {
        const loadingMessage = getElement('loading-message');
        if (loadingMessage) {
            loadingMessage.textContent = 'Chargement de la zone...';
            loadingMessage.className = 'empty-state';
        }

        const detail = getElement('detailProduit');
        if (detail) {
            detail.innerHTML = `
                <div class="detail-title">Details du produit</div>
                <div class="detail-muted">Clique sur un emplacement occupe pour afficher ses informations.</div>
            `;
        }

        try {
            const response = await fetch(`/api/zone/${idZone}`);
            if (!response.ok) {
                throw new Error(`Erreur HTTP ${response.status}`);
            }

            const data = await response.json();
            const stocks = Array.isArray(data) ? data : [];
            renderGrid(stocks);

            if (loadingMessage) {
                loadingMessage.textContent = stocks.length > 0
                    ? `${stocks.length} emplacement(s) charge(s)`
                    : 'Aucun produit trouve dans cette zone.';
                loadingMessage.className = stocks.length > 0 ? 'chip is-accent' : 'empty-state';
            }
        } catch (error) {
            console.error('Erreur lors du chargement de la zone', error);
            if (loadingMessage) {
                loadingMessage.textContent = 'Impossible de charger la zone selectionnee.';
                loadingMessage.className = 'empty-state';
            }
        }
    }

    window.ShowEmplacement = function showEmplacement(idZone) {
        openModal();
        chargerSchemaEtagere(idZone);
    };

    window.closePopup = function closePopup() {
        closeModal();
    };

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeModal();
        }
    });

    document.addEventListener('DOMContentLoaded', () => {
        const popup = getElement('popup');
        if (popup) {
            popup.addEventListener('click', (event) => {
                if (event.target === popup) {
                    closeModal();
                }
            });
        }
    });
})();