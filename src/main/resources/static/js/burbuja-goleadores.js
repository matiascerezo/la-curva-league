function filtrarGoles(jornada, elemento) {
    // 1. Gestionar clases de los botones
    document.querySelectorAll('.burbuja-jornada').forEach(b => b.classList.remove('active'));
    elemento.classList.add('active');

    // 2. Ocultar absolutamente todos los bloques de tablas
    document.querySelectorAll('.bloque-jornada-goles').forEach(block => {
        block.style.display = 'none';
    });

    // 3. Mostrar el bloque correcto
    let idParaMostrar;
    if (jornada === 'todos') {
        idParaMostrar = 'jornada-goles-todos';
    } else {
        // Importante: aquí construimos el ID igual que en el th:id del HTML
        idParaMostrar = 'jornada-goles-' + jornada;
    }

    const target = document.getElementById(idParaMostrar);
    if (target) {
        target.style.display = 'block';
    } else {
        console.error("No se encontró la tabla con ID: " + idParaMostrar);
    }
}