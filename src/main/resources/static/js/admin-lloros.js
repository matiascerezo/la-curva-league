document.addEventListener('DOMContentLoaded', () => {
    cargarEquipos('selectEquipo');
    cargarJornadas();
});


/**
 * Carga los números de jornada disponibles desde la BBDD
 */
async function cargarJornadas() {
    try {
        const respuesta = await fetch('/mantenimiento/admin/listaJornadas');
        const jornadas = await respuesta.json();

        // Convertir a array y ordenar numéricamente
        const jornadasOrdenadas = Array.from(jornadas).sort((a, b) => a - b);

        const selectJornada = document.getElementById('numeroJornada');
        selectJornada.innerHTML = '<option value="">-- Selecciona Jornada --</option>';

        jornadasOrdenadas.forEach(numJornada => {
            const option = document.createElement('option');
            option.value = numJornada;
            option.textContent = `Jornada ${numJornada}`;
            selectJornada.appendChild(option);
        });
    } catch (error) {
        console.error('Error al cargar la lista de Jornadas:', error);
    }
}

/**
 * Petición POST para guardar el lloro vinculado a la jornada
 */
async function guardarLloro(event) {
    event.preventDefault();

    const equipo = document.getElementById('selectEquipo').value;
    const jornada = parseInt(document.getElementById('numeroJornada').value, 10);
    const texto = document.getElementById('textoLloro').value.trim();

    const datos = {
        nombreEquipo: equipo,
        numeroJornada: jornada,
        textoLloro: texto
    };

    try {
        const respuesta = await fetch('/mantenimiento/admin/guardarLloro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        if (respuesta.ok) {
            alert('¡Lloro registrado correctamente!');
            document.getElementById('formLloro').reset();
            actualizarLogo('misterEquipo', 'imgEquipo');
            actualizarContador();
        } else {
            alert('Error en el servidor al registrar el lloro.');
        }
    } catch (error) {
        console.error('Error enviando el lloro:', error);
        alert('Error de conexión con el servidor.');
    }
}

function actualizarContador() {
    const textarea = document.getElementById('textoLloro');
    const contador = document.getElementById('contadorChars');
    contador.textContent = textarea.value.length;
}