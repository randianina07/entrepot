(() => {
	const ligneSelector = '.ligne-mouvement';

	function getLignesContainer() {
		return document.getElementById('lignesContainer');
	}

	function getLignes() {
		return Array.from(document.querySelectorAll(ligneSelector));
	}

	function clearRowState(row) {
		row.querySelectorAll('input, textarea').forEach((field) => {
			field.value = '';
			field.classList.remove('is-valid', 'is-invalid');
		});

		row.querySelectorAll('select').forEach((field) => {
			field.selectedIndex = 0;
			field.classList.remove('is-valid', 'is-invalid');
		});

		const zoneSelect = row.querySelector('.zone-select');
		if (zoneSelect) {
			zoneSelect.innerHTML = "<option value=''>Selectionner une zone</option>";
		}

		const stockLabel = row.querySelector('[id^="stock-disponible-"]');
		if (stockLabel) {
			stockLabel.textContent = 'Stock disponible: --';
		}

		const quantiteInput = row.querySelector('.quantite-input');
		if (quantiteInput) {
			quantiteInput.setAttribute('data-max-stock', '0');
			quantiteInput.removeAttribute('max');
		}

		const erreurLabel = row.querySelector('[id^="erreur-quantite-"]');
		if (erreurLabel) {
			erreurLabel.textContent = '';
		}
	}

	function renameRowFields(row, index) {
		row.setAttribute('data-ligne-index', String(index));

		row.querySelectorAll('[name]').forEach((field) => {
			field.name = field.name.replace(/lignes\[\d+\]/, `lignes[${index}]`);
		});

		row.querySelectorAll('[id]').forEach((field) => {
			field.id = field.id.replace(/-\d+$/, `-${index}`);
		});
	}

	function refreshRows() {
		const rows = getLignes();
		rows.forEach((row, index) => renameRowFields(row, index));

		rows.forEach((row) => {
			const removeButton = row.querySelector('.supprimer-ligne');
			if (removeButton) {
				removeButton.disabled = rows.length <= 1;
			}
		});
	}

	function buildNewRow() {
		const container = getLignesContainer();
		const firstRow = container?.querySelector(ligneSelector);
		if (!container || !firstRow) {
			return null;
		}

		const newRow = firstRow.cloneNode(true);
		clearRowState(newRow);
		container.appendChild(newRow);
		refreshRows();
		return newRow;
	}

	function removeRow(button) {
		const row = button.closest(ligneSelector);
		const container = getLignesContainer();
		if (!row || !container) {
			return;
		}

		if (getLignes().length === 1) {
			clearRowState(row);
			return;
		}

		row.remove();
		refreshRows();
	}

	async function mettreAJourStockDisponible(element) {
		const row = element.closest(ligneSelector);
		if (!row) {
			return;
		}

		const quantiteInput = row.querySelector('.quantite-input');
		const stockLabel = row.querySelector('[id^="stock-disponible-"]');
		const produitSelect = row.querySelector('.produit-select');
		const emplacementSelect = row.querySelector('.emplacement-select');

		if (!quantiteInput || !stockLabel || !produitSelect || !emplacementSelect) {
			return;
		}

		const produitId = produitSelect.value;
		const emplacementId = emplacementSelect.value;

		if (produitId && emplacementId) {
			try {
				const response = await fetch(`/api/mouvements/stock?produitId=${produitId}&emplacementId=${emplacementId}`);
				if (response.ok) {
					const stockDisponible = await response.json();
					stockLabel.textContent = `Stock disponible: ${stockDisponible}`;
					quantiteInput.dataset.maxStock = String(stockDisponible);
					quantiteInput.max = String(stockDisponible);
					validerQuantite(quantiteInput);
					return;
				}
			} catch (error) {
				console.error("Erreur lors de la recuperation du stock", error);
			}
		}

		stockLabel.textContent = 'Stock disponible: --';
		quantiteInput.dataset.maxStock = '0';
		quantiteInput.removeAttribute('max');
		validerQuantite(quantiteInput);
	}

	async function mettreAJourZonesProduit(element) {
		const row = element.closest(ligneSelector);
		if (!row) {
			return;
		}

		const zoneSelect = row.querySelector('.zone-select');
		if (!zoneSelect) {
			return;
		}

		const produitId = element.value;
		zoneSelect.innerHTML = "<option value=''>Chargement...</option>";

		if (!produitId) {
			zoneSelect.innerHTML = "<option value=''>Selectionner une zone</option>";
			return;
		}

		try {
			const response = await fetch(`/mouvements/zones-produit/${produitId}`);
			if (response.ok) {
				const zones = await response.json();
				zoneSelect.innerHTML = "<option value=''>Selectionner une zone</option>";
				zones.forEach((zone) => {
					zoneSelect.innerHTML += `<option value="${zone.id}">${zone.libelle}</option>`;
				});
				return;
			}
		} catch (error) {
			console.error('Erreur lors du chargement des zones', error);
		}

		zoneSelect.innerHTML = "<option value=''>Selectionner une zone</option>";
	}

	function validerQuantite(input) {
		const row = input.closest(ligneSelector);
		if (!row) {
			return;
		}

		const erreurDiv = row.querySelector('[id^="erreur-quantite-"]');
		const maxStock = Number.parseFloat(input.getAttribute('data-max-stock') || '0') || 0;
		const quantite = Number.parseFloat(input.value || '0') || 0;

		if (maxStock > 0 && quantite > maxStock) {
			if (erreurDiv) {
				erreurDiv.textContent = `Quantite maximale: ${maxStock}`;
			}
			input.classList.add('is-invalid');
			input.classList.remove('is-valid');
			return;
		}

		if (quantite > 0) {
			if (erreurDiv) {
				erreurDiv.textContent = '';
			}
			input.classList.remove('is-invalid');
			input.classList.add('is-valid');
			return;
		}

		if (erreurDiv) {
			erreurDiv.textContent = '';
		}
		input.classList.remove('is-invalid', 'is-valid');
	}

	function bindEvents() {
		const container = getLignesContainer();
		if (!container) {
			return;
		}

		container.addEventListener('change', (event) => {
			const target = event.target;
			if (target instanceof HTMLSelectElement && target.classList.contains('produit-select')) {
				mettreAJourZonesProduit(target);
			}
			if (target instanceof HTMLSelectElement && target.classList.contains('emplacement-select')) {
				mettreAJourStockDisponible(target);
			}
		});

		container.addEventListener('input', (event) => {
			const target = event.target;
			if (target instanceof HTMLInputElement && target.classList.contains('quantite-input')) {
				validerQuantite(target);
			}
		});
	}

	function init() {
		refreshRows();
		bindEvents();
	}

	window.ajouterLigne = buildNewRow;
	window.supprimerLigne = removeRow;
	window.mettreAJourStockDisponible = mettreAJourStockDisponible;
	window.mettreAJourZonesProduit = mettreAJourZonesProduit;
	window.validerQuantite = validerQuantite;

	document.addEventListener('DOMContentLoaded', init);
})();
