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

		const capacityLabel = row.querySelector('[id^="capacite-restante-"]');
		if (capacityLabel) {
			capacityLabel.textContent = 'Capacite restante: --';
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

	function updateCapacityHint(selectElement) {
		const row = selectElement.closest(ligneSelector);
		if (!row) {
			return;
		}

		const selectedOption = selectElement.options[selectElement.selectedIndex];
		const capacityLabel = row.querySelector('[id^="capacite-restante-"]');
		if (!capacityLabel) {
			return;
		}

		const capacityValue = selectedOption?.dataset?.capaciteVolumeM3;
		if (capacityValue) {
			capacityLabel.textContent = `Capacite restante: ${capacityValue} m³`;
		} else {
			capacityLabel.textContent = 'Capacite restante: --';
		}
	}

	function bindEvents() {
		const container = getLignesContainer();
		if (!container) {
			return;
		}

		container.addEventListener('change', (event) => {
			const target = event.target;
			if (target instanceof HTMLSelectElement && target.classList.contains('emplacement-select')) {
				updateCapacityHint(target);
			}
		});
	}

	function init() {
		refreshRows();
		bindEvents();
	}

	window.ajouterLigne = buildNewRow;
	window.supprimerLigne = removeRow;

	document.addEventListener('DOMContentLoaded', init);
})();
