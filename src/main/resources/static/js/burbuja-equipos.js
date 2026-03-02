
function filtrarPorRango(inicio, fin, elementoPulsado) {
    document.querySelectorAll('.contenedor-filtros-jornada .burbuja-jornada').forEach(btn => btn.classList.remove('active'));
    if (elementoPulsado) elementoPulsado.classList.add('active');

    document.querySelectorAll('.bloque-jornada').forEach(fila => {
        const numJornada = parseInt(fila.id.replace('jornada-bloque-', ''));
        fila.style.display = (numJornada >= inicio && numJornada <= fin) ? '' : 'none';
    });
}

document.addEventListener("DOMContentLoaded", () => {
    // Inicializar Puntos (si existe el contenedor)
    const burbujaTablaActiva = document.querySelector('.contenedor-filtros-jornada .burbuja-jornada.active');
    if (burbujaTablaActiva) burbujaTablaActiva.click();
});