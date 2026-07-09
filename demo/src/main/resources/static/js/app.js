document.addEventListener('DOMContentLoaded', () => {
  const currentYear = new Date().getFullYear();
  document.querySelectorAll('[data-year]').forEach((element) => {
    element.textContent = currentYear;
  });
});
