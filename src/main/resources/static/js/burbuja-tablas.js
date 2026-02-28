function filtrarLloros(numeroJornada) {
    // 1. Estilo de burbujas
    document.querySelectorAll('.burbuja-jornada').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    // 2. Ocultar todos los bloques de tabla
    document.querySelectorAll('.bloque-jornada').forEach(bloque => {
        bloque.style.display = 'none';
    });

    // 3. Mostrar solo el de la jornada seleccionada
    const bloqueMostrar = document.getElementById('jornada-bloque-' + numeroJornada);
    if (bloqueMostrar) {
        bloqueMostrar.style.display = 'block';
    }
}

// Opcional: Mostrar la última jornada por defecto al cargar
document.addEventListener("DOMContentLoaded", () => {
    const ultimaBurbuja = document.querySelector('.burbuja-jornada:last-child');
    if (ultimaBurbuja) ultimaBurbuja.click();
});