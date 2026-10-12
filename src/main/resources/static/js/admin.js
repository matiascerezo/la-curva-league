// Estructura en memoria
let alineacionActual = {};
let slotSeleccionado = null;
let listaEquiposGlobal = [];
let listaGlobalJugadores = [];

// Inicialización de eventos al cargar el DOM
document.addEventListener('DOMContentLoaded', () => {
    // 1. Gestión de inputs de archivos
    const fileInput = document.getElementById('file-input');
    const fileMsg = document.getElementById('file-msg');
    const removeBtn = document.getElementById('remove-file');
    const fileBox = document.getElementById('file-box');

    if (fileInput) {
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                if (fileMsg) fileMsg.textContent = fileInput.files[0].name;
                if (fileBox) fileBox.textContent = '';
                if (removeBtn) removeBtn.style.display = 'flex';
            } else {
                if (fileMsg) fileMsg.textContent = 'sin archivo.';
            }
        });
    }

    if (removeBtn && fileInput) {
        removeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            fileInput.value = '';
            if (fileBox) fileBox.textContent = 'Elegir archivo';
            if (fileMsg) fileMsg.textContent = 'o arrastra y suelta aquí';
            removeBtn.style.display = 'none';
        });
    }

    // 2. Carga inicial de datos, táctica y jugadores reales de la bbdd
    cargarEquipos();
    precargarJugadores();
    const selectEsquema = document.getElementById('selectEsquema');
    if (selectEsquema) {
        cambiarEsquemaTactico(selectEsquema.value);
    }
});

/**
 * Redibuja las líneas tácticas del campo de fútbol según el esquema seleccionado (DF-MC-DL).
 */
function cambiarEsquemaTactico(esquema) {
    const partes = esquema.split('-').map(Number);
    renderizarLinea('lineaDL', 'DL', partes[2]);
    renderizarLinea('lineaMC', 'MC', partes[1]);
    renderizarLinea('lineaDF', 'DF', partes[0]);
    renderizarLinea('lineaPT', 'PT', 1);
}

/**
 * Genera dinámicamente cada fila de jugadores.
 */
function renderizarLinea(idContenedor, posicion, cantidad) {
    const contenedor = document.getElementById(idContenedor);
    if (!contenedor) return;

    contenedor.innerHTML = '';

    for (let i = 1; i <= cantidad; i++) {
        const slotId = `${posicion}-${i}`;
        const datos = alineacionActual[slotId];
        const estaOcupado = datos && datos.nombre !== '';
        const pendiente = datos && datos.pendiente;

        const div = document.createElement('div');
        div.className = `slot-jugador ${estaOcupado ? 'ocupado' : ''} ${datos?.xiIdeal ? 'xi-ideal' : ''} ${datos?.pendiente ? 'pendiente' : ''} ${datos && !datos.partidoJugado ? 'no-jugo' : ''}`;
        div.onclick = () => abrirEdicionJugador(posicion, i);

        let infoBadge = 'Pendiente';
        if (estaOcupado) {
            if (!datos.partidoJugado) {
                infoBadge = '❌';
            } else if(pendiente) {
                infoBadge = '⌛';
            } else {
                infoBadge = `⭐ ${datos.puntos} PUNTOS`;
            }
        }

        let badgeCapitanHtml = '';
        if (datos?.capitan) {
            const multText = datos.multiplicadorCapitan ? `x${datos.multiplicadorCapitan}` : '';
            badgeCapitanHtml = `<span class="badge-corner corner-top-left badge-capitan" title="Capitán ${multText}">👑 ${multText}</span>`;
        }

        let badgeGoles = (datos?.goles > 0) ? `<span class="goles-icon-corner" title="Goles: ${datos.goles}">${datos.goles > 1 ? datos.goles : ''}⚽</span>` : '';
        let badgeAsistencias = (datos?.asistencias > 0) ? `<span class="asistencias-icon-corner" title="Asistencias: ${datos.asistencias}">${datos.asistencias > 1 ? datos.asistencias : ''}👟</span>` : '';

        let badgeTarjetasHtml = '';
        if (datos?.rojas > 0) {
            badgeTarjetasHtml = `<span class="badge-corner corner-bottom-left badge-card-roja">🟥</span>`;
        } else if (datos?.amarillas > 0) {
            badgeTarjetasHtml = `<span class="badge-corner corner-bottom-left badge-card-amarilla">🟨 ${datos.amarillas > 1 ? datos.amarillas : ''}</span>`;
        }

        let imgEscudoHtml = '';
        if (estaOcupado) {
            const realIdNum = datos.jugadorRealId ? parseInt(datos.jugadorRealId, 10) : null;
            const jugadorReal = listaGlobalJugadores.find(j => 
                (realIdNum && j.id === realIdNum) || 
                j.nombreCortoJugador === datos.nombre
            );

            if (jugadorReal && jugadorReal.imgEquipo) {
                imgEscudoHtml = `<img src="${jugadorReal.imgEquipo}" alt="Escudo" style="width: 16px; height: 16px; object-fit: contain; vertical-align: middle; margin-right: 4px;" />`;
            }
        }

        div.innerHTML = `
            ${badgeCapitanHtml}
            ${badgeGoles}
            ${badgeAsistencias}
            ${badgeTarjetasHtml}
            <div class="slot-posicion-jornadas pos-${posicion}">${posicion}</div>
            <div class="slot-nombre" id="nombre-${slotId}">
                ${imgEscudoHtml}${datos ? datos.nombre : 'Sin jugador'}
            </div>
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
        jugadorId: null,
        jugadorRealId: null,
        nombre: '',
        puntos: 0,
        goles: 0,
        asistencias: 0,
        amarillas: 0,
        rojas: 0,
        partidoJugado: true,
        xiIdeal: false,
        penaltisParados: 0,
        pendiente: false,
        capitan: false,
        multiplicadorCapitan: 2.0
    };

    document.getElementById('modalTitulo').textContent = `Posición ${posicion} #${indice}`;
    document.getElementById('inputNombre').value = datos.nombre;
    document.getElementById('inputJugadorId').value = datos.jugadorId || '';
    
    const realIdNum = datos.jugadorRealId ? parseInt(datos.jugadorRealId, 10) : null;
    document.getElementById('inputJugadorRealId').value = realIdNum || '';

    // Sincronizar el escudo únicamente mediante actualizarEscudoModal
    actualizarEscudoModal(realIdNum);

    document.getElementById('inputPuntos').value = datos.puntos;
    document.getElementById('inputGoles').value = datos.goles;
    document.getElementById('inputAsistencias').value = datos.asistencias;
    document.getElementById('inputAmarillas').value = datos.amarillas;
    document.getElementById('inputRojas').value = datos.rojas;
    document.getElementById('checkPartidoJugado').checked = datos.partidoJugado;
    document.getElementById('checkXiIdeal').checked = datos.xiIdeal;
    document.getElementById('inputPenaltisParados').value = datos.penaltisParados;

    document.getElementById('pendiente').checked = datos.pendiente;
    document.getElementById('checkCapitan').checked = datos.capitan;
    document.getElementById('selectMultiplicadorCapitan').value = datos.multiplicadorCapitan;

    toggleMultiplicadorCapitan();

    document.getElementById('contenedorPenaltisParados').style.display = posicion === 'PT' ? 'block' : 'none';
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
    const jugadorIdVal = document.getElementById('inputJugadorId').value;
    const jugadorId = jugadorIdVal ? parseInt(jugadorIdVal, 10) : null;
    
    const jugadorRealIdVal = document.getElementById('inputJugadorRealId').value;
    const jugadorRealId = jugadorRealIdVal ? parseInt(jugadorRealIdVal, 10) : null;

    const puntos = parseInt(document.getElementById('inputPuntos').value, 10) || 0;
    const goles = parseInt(document.getElementById('inputGoles').value, 10) || 0;
    const asistencias = parseInt(document.getElementById('inputAsistencias').value, 10) || 0;
    const amarillas = parseInt(document.getElementById('inputAmarillas').value, 10) || 0;
    const rojas = parseInt(document.getElementById('inputRojas').value, 10) || 0;
    const partidoJugado = document.getElementById('checkPartidoJugado').checked;
    const xiIdeal = document.getElementById('checkXiIdeal').checked;
    const penaltisParados = parseInt(document.getElementById('inputPenaltisParados')?.value, 10) || 0;

    const pendiente = document.getElementById('pendiente').checked;
    const capitan = document.getElementById('checkCapitan').checked;
    const multiplicadorCapitan = document.getElementById('selectMultiplicadorCapitan').value;

    if (nombre !== '') {
        alineacionActual[slotSeleccionado] = {
            jugadorId: jugadorId,
            jugadorRealId: jugadorRealId,
            nombre,
            puntos,
            goles,
            asistencias,
            amarillas,
            rojas,
            partidoJugado,
            xiIdeal,
            penaltisParados,
            pendiente,
            capitan,
            multiplicadorCapitan
        };
    } else {
        delete alineacionActual[slotSeleccionado];
    }

    const esquemaActual = document.getElementById('selectEsquema').value;
    cambiarEsquemaTactico(esquemaActual);

    actualizarPuntosTotales();

    cerrarModal();
}

/**
 * Carga la lista de equipos.
 */
async function cargarEquipos(...selectIds) {
    if (selectIds.length === 0) selectIds = ['selectEquipo'];

    const primerSelect = document.getElementById(selectIds[0]);
    if (!primerSelect) return;

    const url = primerSelect.getAttribute('data-url') || '/mantenimiento/admin/listaEquipos';

    try {
        const respuesta = await fetch(url);
        listaEquiposGlobal = await respuesta.json();

        selectIds.forEach(id => {
            const select = document.getElementById(id);
            if (!select) return;

            const esPorNombre = select.hasAttribute('data-val-nombre');
            select.innerHTML = '<option value="">-Selecciona Equipo</option>';

            listaEquiposGlobal.forEach(equipo => {
                const option = document.createElement('option');
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
 * Actualiza el logo del equipo seleccionado.
 */
function actualizarLogo(selectId, imgId) {
    const select = document.getElementById(selectId);
    const imgElement = document.getElementById(imgId);

    if (!select || !imgElement) return;

    const val = select.value;
    const equipoEncontrado = listaEquiposGlobal.find(e =>
        e.misterId == val || e.nombreEquipo === val
    );

    if (equipoEncontrado && equipoEncontrado.imgEquipo) {
        imgElement.src = equipoEncontrado.imgEquipo;
        imgElement.classList.remove('empty');
        imgElement.style.opacity = '1';
        imgElement.style.filter = 'none';
    } else {
        imgElement.src = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ccc'><path d='M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5'/></svg>";
        imgElement.classList.add('empty');
        imgElement.style.opacity = '0.4';
        imgElement.style.filter = 'grayscale(100%)';
    }
}

/**
 * Carga alineación guardada previamente.
 */
async function cargarAlineacionEquipo() {
    const equipoId = document.getElementById('selectEquipo').value;
    const jornadaNum = document.getElementById('selectJornada').value; 

    if (!equipoId || !jornadaNum) {
        alineacionActual = {};
        const esquemaSelect = document.getElementById('selectEsquema');
        if (esquemaSelect) {
            cambiarEsquemaTactico(esquemaSelect.value);
        }
        return; 
    }

    try {
        const respuesta = await fetch(`/mantenimiento/admin/cargarAlineacionEquipo/${jornadaNum}/equipo/${equipoId}?_=${new Date().getTime()}`);
        
        if (respuesta.ok) {
            const textoRespuesta = await respuesta.text();
            
            alineacionActual = {};
            const data = textoRespuesta.trim() ? JSON.parse(textoRespuesta) : alineacionActual = {};

            if (data.esquema) {
                document.getElementById('selectEsquema').value = data.esquema;
            }

            if (data.jugadores && Array.isArray(data.jugadores)) {
                const contadorPorPosicion = {};

                data.jugadores.forEach(j => {
                    const pos = j.posicionCorta;

                    if (!contadorPorPosicion[pos]) {
                        contadorPorPosicion[pos] = 1;
                    } else {
                        contadorPorPosicion[pos]++;
                    }

                    const slotIndex = j.indiceSlot || contadorPorPosicion[pos];
                    const slotKey = `${pos}-${slotIndex}`;

                    
                    alineacionActual[slotKey] = {
                        jugadorId: j.jugadorId,
                        jugadorRealId: j.jugadorRealId,
                        nombre: j.nombre,
                        puntos: j.puntos || 0,
                        goles: j.goles || 0,
                        asistencias: j.asistencias || 0,
                        amarillas: j.amarillas || 0,
                        rojas: j.rojas || 0,
                        partidoJugado: j.partidoJugado !== false,
                        xiIdeal: j.xiIdeal === true,
                        penaltisParados: j.penaltisParados || 0,
                        pendiente: j.pendiente !== false,
                        capitan: j.capitan !== false,
                        multiplicadorCapitan: j.multiplicadorCapitan
                    };
                });
            } else {
                alineacionActual = {};
            }
        } else {
            alert(await respuesta.text());
            alineacionActual = {};
        }
    } catch (error) {
        console.error('Error cargando la alineación del equipo:', error);
        alineacionActual = {};
    } finally {
        const esquemaSelect = document.getElementById('selectEsquema');
        if (esquemaSelect) {
            cambiarEsquemaTactico(esquemaSelect.value);
        }
        actualizarPuntosTotales();
    }
}

/**
 * Guarda la alineación actual en el servidor.
 */
async function guardarAlineacion() {
    const misterId = document.getElementById('selectEquipo').value;
    const jornadaNum = document.getElementById('selectJornada').value;
    const esquema = document.getElementById('selectEsquema').value;
    const puntosJornada = document.getElementById('totalPuntosJornada').textContent;

    if (!misterId) {
        alert('Por favor, selecciona un equipo antes de guardar.');
        return;
    }

    const listaJugadores = [];
    Object.keys(alineacionActual).forEach(slotKey => {
        const [posicion, indice] = slotKey.split('-');
        const datos = alineacionActual[slotKey];
        if (datos && datos.nombre !== '') {
            listaJugadores.push({
                jugadorId: datos.jugadorId,
                jugadorRealId: datos.jugadorRealId,
                posicion: posicion,
                indiceSlot: parseInt(indice, 10),
                nombre: datos.nombre,
                puntos: datos.puntos,
                goles: datos.goles,
                asistencias: datos.asistencias,
                amarillas: datos.amarillas,
                rojas: datos.rojas,
                partidoJugado: datos.partidoJugado,
                xiIdeal: datos.xiIdeal,
                penaltisParados: datos.penaltisParados,
                pendiente: datos.pendiente,
                capitan: datos.capitan,
                multiplicadorCapitan: datos.multiplicadorCapitan
            });
        }
    });

    const datos = {
        misterId: parseInt(misterId, 10),
        numeroJornada: jornadaNum,
        esquema: esquema,
        jugadores: listaJugadores,
        puntosJornada: puntosJornada
    };

    try {
        const respuesta = await fetch('/mantenimiento/admin/guardarAlineacion', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        const mensajeServidor = await respuesta.text();

        if (respuesta.ok) {
            await cargarAlineacionEquipo();
            alert(mensajeServidor);
        }  else {
            alert('Error al guardar la alineación en el servidor.');
        }
    } catch (error) {
        console.error('Error enviando la alineación:', error);
        alert('Error de conexión con la API REST.');
    }
}

function toggleMultiplicadorCapitan() {
    const checkCapitan = document.getElementById("checkCapitan");
    const selectMultiplicador = document.getElementById("selectMultiplicadorCapitan");

    if (checkCapitan && selectMultiplicador) {
        selectMultiplicador.disabled = !checkCapitan.checked;
    }
}

/**
 * Recorre todos los jugadores de la alineación actual y actualiza el contador flotante.
 */
function actualizarPuntosTotales() {
    let sumaTotal = 0;

    Object.values(alineacionActual).forEach(datos => {
        if (datos && datos.nombre && datos.partidoJugado) {
            let ptsJugador = parseInt(datos.puntos, 10) || 0;
            sumaTotal += ptsJugador;
        }
    });

    const elementoTotal = document.getElementById('totalPuntosJornada');
    if (elementoTotal) {
        elementoTotal.textContent = sumaTotal > 999 ? 999 : sumaTotal;
    }
}

// Llamar a esto cuando cargue el DOM por primera vez
async function precargarJugadores() {
    try {
        const response = await fetch('/mantenimiento/admin/jugadores_reales');
        if (response.ok) {
            listaGlobalJugadores = await response.json();
            poblarDatalist();
        }
    } catch (error) {
        console.error('Error al precargar los jugadores:', error);
    }
}

function poblarDatalist() {
    const datalist = document.getElementById('listaSugerenciasJugadores');
    if (!datalist) return;
    
    datalist.innerHTML = '';
    listaGlobalJugadores.forEach(j => {
        const option = document.createElement('option');
        
        option.value = j.nombreCortoJugador;
        option.setAttribute('data-id', j.id);
        
        // Lo que muestro por pantalla en el buscador
        option.label = `${j.equipoReal}`;
        
        datalist.appendChild(option);
    });
}

function actualizarEscudoModal(jugadorRealId) {
    const inputRealId = document.getElementById('inputJugadorRealId');
    const imgEscudo = document.getElementById('imgEscudoEquipoReal');

    // Asignamos el valor en el input oculto
    if (inputRealId) {
        inputRealId.value = jugadorRealId || '';
    }

    // Buscamos unívocamente por ID en la lista global
    let jugadorEncontrado = null;
    if (jugadorRealId) {
        jugadorEncontrado = listaGlobalJugadores.find(j => j.id === parseInt(jugadorRealId, 10));
    }

    // Mostramos u ocultamos la imagen
    if (imgEscudo) {
        if (jugadorEncontrado && jugadorEncontrado.imgEquipo) {
            imgEscudo.src = jugadorEncontrado.imgEquipo;
            imgEscudo.style.display = 'inline-block';
        } else {
            imgEscudo.src = '';
            imgEscudo.style.display = 'none';
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const inputNombre = document.getElementById('inputNombre');
    if (inputNombre) {
        inputNombre.addEventListener('input', (e) => {
            const nombreIngresado = e.target.value.trim();
            const datalist = document.getElementById('listaSugerenciasJugadores');
            if (!datalist) return;

            const optionSeleccionada = Array.from(datalist.options).find(opt => opt.value === nombreIngresado);

            if (optionSeleccionada) {
                const idReal = optionSeleccionada.getAttribute('data-id');
                actualizarEscudoModal(idReal ? parseInt(idReal, 10) : null);
            } else {
                actualizarEscudoModal(null);
            }
        });
    }
});