(() => {
	function getMovementCode() {
		const codeElement = document.querySelector('[data-mouvement-code]');
		return codeElement?.textContent?.trim() || '';
	}

	function showCopiedFeedback(button) {
		const label = button.textContent;
		button.disabled = true;
		button.textContent = 'Copie';

		window.setTimeout(() => {
			button.textContent = label;
			button.disabled = false;
		}, 1200);
	}

	async function copyMovementCode(button) {
		const movementCode = getMovementCode();
		if (!movementCode) {
			return;
		}

		try {
			await navigator.clipboard.writeText(movementCode);
			showCopiedFeedback(button);
		} catch (error) {
			console.error('Impossible de copier le code du mouvement', error);
		}
	}

	function bindCopyButton() {
		const button = document.querySelector('[data-copy-mouvement-code]');
		if (!button) {
			return;
		}

		button.addEventListener('click', () => copyMovementCode(button));
	}

	document.addEventListener('DOMContentLoaded', bindCopyButton);
})();
