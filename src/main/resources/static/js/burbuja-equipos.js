
function filtrarPorRango(inicio, fin, elementoPulsado) {
    // 1. Gestión de clases visuales
    document.querySelectorAll('.burbuja-jornada').forEach(btn => btn.classList.remove('active'));
    if (elementoPulsado) elementoPulsado.classList.add('active');

    // 2. Filtrar filas de la tabla
    document.querySelectorAll('.bloque-jornada').forEach(fila => {
        const numJornada = parseInt(fila.id.replace('jornada-bloque-', ''));
        fila.style.display = (numJornada >= inicio && numJornada <= fin) ? '' : 'none';
    });

    // 3. ACTUALIZAR GRÁFICO (Sincronización)
    if (typeof myChart !== 'undefined' && typeof rawData !== 'undefined') {
        // Filtramos los datos originales basados en el rango
        const datosFiltrados = rawData.filter(d => d.numeroJornada >= inicio && d.numeroJornada <= fin);
        
        // Actualizamos labels y puntos del objeto myChart
        myChart.data.labels = datosFiltrados.map(d => 'J' + d.numeroJornada);
        myChart.data.datasets[0].data = datosFiltrados.map(d => d.puntosJornada);
        
        // Renderizamos de nuevo el gráfico con efecto suave
        myChart.update(); 
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // Inicializar Puntos (si existe el contenedor)
    const burbujaTablaActiva = document.querySelector('.contenedor-filtros-jornada .burbuja-jornada.active');
    if (burbujaTablaActiva) burbujaTablaActiva.click();
});