document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('file-input');
    const fileMsg = document.getElementById('file-msg');
    

    if (fileInput) {
        fileInput.addEventListener('change', () => {
            const fileName = fileInput.files[0]?.name || "sin archivo.";
            fileMsg.textContent = fileName;
            
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('file-input');
    const fileMsg = document.getElementById('file-msg');
    const removeBtn = document.getElementById('remove-file');
    const fileBox = document.getElementById('file-box');

    if (fileInput && fileMsg && removeBtn) {
        
        // Al cambiar el fichero
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                fileMsg.textContent = fileInput.files[0].name;
                fileBox.textContent = "";
                removeBtn.style.display = 'flex';
            }
        });

        removeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation(); 
            
            fileInput.value = '';
            fileBox.textContent = "Elegir archivo";
            fileMsg.textContent = 'o arrastra y suelta aquí';
            removeBtn.style.display = 'none';
        });
    }
});

// Estructura en memoria para la alineación actual
let alineacionActual = {};
let slotSeleccionado = null;

/**
 * Redibuja las líneas tácticas del campo de fútbol según el esquema seleccionado (DEF-MED-DEL).
 * @param {string} esquema Ejemplo: "4-3-3", "5-4-1", etc.
 */
function cambiarEsquemaTactico(esquema) {
    const partes = esquema.split('-').map(Number); // [DEF, MED, DEL]
    const defCount = partes[0];
    const medCount = partes[1];
    const delCount = partes[2];

    renderizarLinea('lineaDEL', 'DEL', delCount);
    renderizarLinea('lineaMED', 'MED', medCount);
    renderizarLinea('lineaDEF', 'DEF', defCount);
    renderizarLinea('lineaPOR', 'POR', 1);
}

/**
 * Generar dinámicamente cada fila de jugadores.
 */
function renderizarLinea(idContenedor, posicion, cantidad) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) return;
    
    contenedor.innerHTML = '';

    for (let i = 1; i <= cantidad; i++) {
        const slotId = `${posicion}-${i}`;
        const datos = alineacionActual[slotId];
        const estaOcupado = datos && datos.nombre !== '';

        const div = document.createElement('div');
        div.className = `slot-jugador ${estaOcupado ? 'ocupado' : ''} ${datos?.xiIdeal ? 'xi-ideal' : ''} ${datos && !datos.haJugado ? 'no-jugo' : ''}`;
        div.onclick = () => abrirEdicionJugador(posicion, i);

        // Texto informativo del badge
        let infoBadge = 'Pendiente';
        if (estaOcupado) {
            if (!datos.haJugado) {
                infoBadge = 'No jugó (0 pts)';
            } else {
                infoBadge = `⭐ ${datos.puntos} pts | ⚽ ${datos.goles}`;
            }
        }

        div.innerHTML = `
            ${datos?.xiIdeal ? '<span class="star-icon">⭐</span>' : ''}
            <div class="slot-posicion">${posicion}</div>
            <div class="slot-nombre" id="nombre-${slotId}">${datos ? datos.nombre : 'Sin Asignar'}</div>
            <span class="status-badge ${estaOcupado ? 'badge-listo' : 'badge-vacio'}" id="badge-${slotId}">
                ${infoBadge}
            </span>
        `;
        contenedor.appendChild(div);
    }
}

/**
 * Abre el modal emergente con los datos actuales del jugador.
 */
function abrirEdicionJugador(posicion, indice) {
    slotSeleccionado = `${posicion}-${indice}`;
    const datos = alineacionActual[slotSeleccionado] || {
        nombre: '',
        puntos: 0,
        goles: 0,
        asistencias: 0,
        amarillas: 0,
        rojas: 0,
        haJugado: true,
        xiIdeal: false
    };

    document.getElementById('modalTitulo').textContent = `Posición ${posicion} #${indice}`;
    document.getElementById('inputNombre').value = datos.nombre;
    document.getElementById('inputPuntos').value = datos.puntos;
    document.getElementById('inputGoles').value = datos.goles;
    document.getElementById('inputAsistencias').value = datos.asistencias;
    document.getElementById('inputAmarillas').value = datos.amarillas;
    document.getElementById('inputRojas').value = datos.rojas;
    document.getElementById('checkHaJugado').checked = datos.haJugado;
    document.getElementById('checkXiIdeal').checked = datos.xiIdeal;

    document.getElementById('modalJugador').style.display = 'flex';
}

/**
 * Cierra el modal emergente.
 */
function cerrarModal() {
    document.getElementById('modalJugador').style.display = 'none';
    slotSeleccionado = null;
}

/**
 * Guarda o actualiza los datos introducidos en el modal.
 */
function guardarDatosJugador() {
    if (!slotSeleccionado) return;

    const nombre = document.getElementById('inputNombre').value.trim();
    const puntos = parseInt(document.getElementById('inputPuntos').value, 10) || 0;
    const goles = parseInt(document.getElementById('inputGoles').value, 10) || 0;
    const asistencias = parseInt(document.getElementById('inputAsistencias').value, 10) || 0;
    const amarillas = parseInt(document.getElementById('inputAmarillas').value, 10) || 0;
    const rojas = parseInt(document.getElementById('inputRojas').value, 10) || 0;
    const haJugado = document.getElementById('checkHaJugado').checked;
    const xiIdeal = document.getElementById('checkXiIdeal').checked;

    if (nombre !== '') {
        alineacionActual[slotSeleccionado] = { 
            nombre, 
            puntos, 
            goles, 
            asistencias, 
            amarillas, 
            rojas, 
            haJugado, 
            xiIdeal 
        };
    } else {
        delete alineacionActual[slotSeleccionado];
    }

    // Re-renderizar la formación actual para reflejar los cambios en el terreno
    const esquemaActual = document.getElementById('selectEsquema').value;
    cambiarEsquemaTactico(esquemaActual);

    cerrarModal();
}

// Inicializar el terreno de juego cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    const select = document.getElementById('selectEsquema');
    if (select) {
        cambiarEsquemaTactico(select.value);
    }
});

let listaEquiposGlobal = [];

/**
 * Carga la lista de equipos y la almacena globalmente.
 * Soporta selector directo (id="selectEquipo") o múltiples selectores pasados como argumentos.
 */
async function cargarEquipos(...selectIds) {
    // Si no se le pasan IDs, por defecto busca 'selectEquipo'
    if (selectIds.length === 0) selectIds = ['selectEquipo'];

    // Si el primer elemento no existe en la pantalla actual, no hace nada
    const primerSelect = document.getElementById(selectIds[0]);
    if (!primerSelect) return;

    const url = primerSelect.getAttribute('data-url') || '/mantenimiento/admin/listaEquipos';

    try {
        const respuesta = await fetch(url);
        listaEquiposGlobal = await respuesta.json();

        // Poblamos todos los IDs de <select> que se hayan indicado
        selectIds.forEach(id => {
            const select = document.getElementById(id);
            if (!select) return;

            const esPorNombre = select.hasAttribute('data-val-nombre');
            
            select.innerHTML = '<option value="">-- Selecciona un Equipo --</option>';

            listaEquiposGlobal.forEach(equipo => {
                const option = document.createElement('option');
                // Si tiene el atributo data-val-nombre usa nombreEquipo, si no usa misterId
                option.value = esPorNombre ? equipo.nombreEquipo : equipo.misterId;
                option.textContent = `${equipo.nombreEquipo} (${equipo.nombreMister})`;
                select.appendChild(option);
            });
        });

    } catch (error) {
        console.error('Error al cargar la lista de equipos:', error);
    }
}

/**
 * Función UNIVERSAL para actualizar cualquier logo en cualquier pantalla.
 * Funciona buscando tanto por misterId (número) como por nombreEquipo (texto).
 */
function actualizarLogo(selectId, imgId) {
    const select = document.getElementById(selectId);
    const imgElement = document.getElementById(imgId);

    if (!select || !imgElement) return;

    const val = select.value;

    // Buscar coincidencia en el array global tanto por ID como por nombre
    const equipoEncontrado = listaEquiposGlobal.find(e => 
        e.misterId == val || e.nombreEquipo === val
    );

    if (equipoEncontrado && equipoEncontrado.imgEquipo) {
        imgElement.src = equipoEncontrado.imgEquipo;
        imgElement.classList.remove('empty');
        imgElement.style.opacity = '1';
        imgElement.style.filter = 'none';
    } else {
        // Fallback cuando no hay nada seleccionado
        imgElement.src = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ccc'><path d='M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5'/></svg>";
        imgElement.classList.add('empty');
        imgElement.style.opacity = '0.4';
        imgElement.style.filter = 'grayscale(100%)';
    }
}

// Cargar alineación previamente guardada para el equipo seleccionado
async function cargarAlineacionEquipo() {
    const equipoId = document.getElementById('selectEquipo').value;
    const jornadaNum = document.getElementById('jornadaNum').textContent.trim();

    if (!equipoId) {
        alineacionActual = {};
        cambiarEsquemaTactico(document.getElementById('selectEsquema').value);
        return;
    }

    try {
        const respuesta = await fetch(`/api/jornadas/${jornadaNum}/equipos/${equipoId}`);
        if (respuesta.ok) {
            const data = await respuesta.json();
            
            // Si el backend devuelve un esquema guardado (ej. 4-4-2), lo ajustamos en el select
            if (data.esquema) {
                document.getElementById('selectEsquema').value = data.esquema;
            }

            // Convertir la lista devuelta por el servidor a la estructura en memoria (alineacionActual)
            alineacionActual = {};
            if (data.jugadores && Array.isArray(data.jugadores)) {
                data.jugadores.forEach(j => {
                    const slotKey = `${j.posicion}-${j.indiceSlot || 1}`;
                    alineacionActual[slotKey] = {
                        nombre: j.nombreJugador,
                        puntos: j.puntos || 0,
                        goles: j.goles || 0,
                        asistencias: j.asistencias || 0,
                        amarillas: j.amarillas || 0,
                        rojas: j.rojas || 0,
                        haJugado: j.haJugado !== false,
                        xiIdeal: j.xiIdeal === true
                    };
                });
            }
        } else {
            // Si no existe alineación previa, limpiamos el campo
            alineacionActual = {};
        }

        cambiarEsquemaTactico(document.getElementById('selectEsquema').value);
    } catch (error) {
        console.error('Error cargando la alineación del equipo:', error);
        alineacionActual = {};
        cambiarEsquemaTactico(document.getElementById('selectEsquema').value);
    }
}

// Enviar / Guardar (Alta o Modificación) la alineación completa a la API REST
async function guardarAlineacion() {
    const equipoId = document.getElementById('selectEquipo').value;
    const jornadaNum = parseInt(document.getElementById('jornadaNum').textContent.trim(), 10) || 0;
    const esquema = document.getElementById('selectEsquema').value;

    if (!equipoId) {
        alert('Por favor, selecciona un equipo antes de guardar.');
        return;
    }

    // Transformar el objeto en memoria a un array de jugadores
    const listaJugadores = [];
    Object.keys(alineacionActual).forEach(slotKey => {
        const [posicion, indice] = slotKey.split('-');
        const datos = alineacionActual[slotKey];
        if (datos && datos.nombre !== '') {
            listaJugadores.push({
                posicion: posicion,
                indiceSlot: parseInt(indice, 10),
                nombreJugador: datos.nombre,
                puntos: datos.puntos,
                goles: datos.goles,
                asistencias: datos.asistencias,
                amarillas: datos.amarillas,
                rojas: datos.rojas,
                haJugado: datos.haJugado,
                xiIdeal: datos.xiIdeal
            });
        }
    });

    const payload = {
        equipoId: parseInt(equipoId, 10),
        numeroJornada: jornadaNum,
        esquema: esquema,
        jugadores: listaJugadores
    };

    try {
        const respuesta = await fetch('/api/jornadas/guardar-alineacion', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (respuesta.ok) {
            alert('¡Alineación guardada correctamente!');
        } else {
            alert('Error al guardar la alineación en el servidor.');
        }
    } catch (error) {
        console.error('Error enviando la alineación:', error);
        alert('Error de conexión con la API REST.');
    }
}

// Funciones tácticas y de renderizado (se mantienen)
function cambiarEsquemaTactico(esquema) {
    const partes = esquema.split('-').map(Number);
    renderizarLinea('lineaDEL', 'DEL', partes[2]);
    renderizarLinea('lineaMED', 'MED', partes[1]);
    renderizarLinea('lineaDEF', 'DEF', partes[0]);
    renderizarLinea('lineaPOR', 'POR', 1);
}

function renderizarLinea(idContenedor, posicion, cantidad) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) return;
    contenedor.innerHTML = '';

    for (let i = 1; i <= cantidad; i++) {
        const slotId = `${posicion}-${i}`;
        const datos = alineacionActual[slotId];
        const estaOcupado = datos && datos.nombre !== '';

        const div = document.createElement('div');
        div.className = `slot-jugador ${estaOcupado ? 'ocupado' : ''} ${datos?.xiIdeal ? 'xi-ideal' : ''} ${datos && !datos.haJugado ? 'no-jugo' : ''}`;
        div.onclick = () => abrirEdicionJugador(posicion, i);

        let infoBadge = 'Pendiente';
        if (estaOcupado) {
            infoBadge = !datos.haJugado ? 'No jugó (0 pts)' : `⭐ ${datos.puntos} pts | ⚽ ${datos.goles}`;
        }

        div.innerHTML = `
            ${datos?.xiIdeal ? '<span class="star-icon">⭐</span>' : ''}
            <div class="slot-posicion">${posicion}</div>
            <div class="slot-nombre" id="nombre-${slotId}">${datos ? datos.nombre : 'Sin Asignar'}</div>
            <span class="status-badge ${estaOcupado ? 'badge-listo' : 'badge-vacio'}" id="badge-${slotId}">
                ${infoBadge}
            </span>
        `;
        contenedor.appendChild(div);
    }
}

function abrirEdicionJugador(posicion, indice) {
    slotSeleccionado = `${posicion}-${indice}`;
    const datos = alineacionActual[slotSeleccionado] || {
        nombre: '', puntos: 0, goles: 0, asistencias: 0, amarillas: 0, rojas: 0, haJugado: true, xiIdeal: false
    };

    document.getElementById('modalTitulo').textContent = `Posición ${posicion} #${indice}`;
    document.getElementById('inputNombre').value = datos.nombre;
    document.getElementById('inputPuntos').value = datos.puntos;
    document.getElementById('inputGoles').value = datos.goles;
    document.getElementById('inputAsistencias').value = datos.asistencias;
    document.getElementById('inputAmarillas').value = datos.amarillas;
    document.getElementById('inputRojas').value = datos.rojas;
    document.getElementById('checkHaJugado').checked = datos.haJugado;
    document.getElementById('checkXiIdeal').checked = datos.xiIdeal;

    document.getElementById('modalJugador').style.display = 'flex';
}

function cerrarModal() {
    document.getElementById('modalJugador').style.display = 'none';
    slotSeleccionado = null;
}

function guardarDatosJugador() {
    if (!slotSeleccionado) return;

    const nombre = document.getElementById('inputNombre').value.trim();
    const puntos = parseInt(document.getElementById('inputPuntos').value, 10) || 0;
    const goles = parseInt(document.getElementById('inputGoles').value, 10) || 0;
    const asistencias = parseInt(document.getElementById('inputAsistencias').value, 10) || 0;
    const amarillas = parseInt(document.getElementById('inputAmarillas').value, 10) || 0;
    const rojas = parseInt(document.getElementById('inputRojas').value, 10) || 0;
    const haJugado = document.getElementById('checkHaJugado').checked;
    const xiIdeal = document.getElementById('checkXiIdeal').checked;

    if (nombre !== '') {
        alineacionActual[slotSeleccionado] = { nombre, puntos, goles, asistencias, amarillas, rojas, haJugado, xiIdeal };
    } else {
        delete alineacionActual[slotSeleccionado];
    }

    cambiarEsquemaTactico(document.getElementById('selectEsquema').value);
    cerrarModal();
}

document.addEventListener('DOMContentLoaded', () => {
    cargarEquipos();
    const selectEsquema = document.getElementById('selectEsquema');
    if (selectEsquema) {
        cambiarEsquemaTactico(selectEsquema.value);
    }
});