// Variable global para controlar la posición actual
// Se inicializa en -1 para mostrar la vista "Todo" al cargar
let indiceActual = -1; 

window.onload = function() {
    // 1. Comprobamos si estamos en la pantalla de Lloros
    const bloquesLloros = document.querySelectorAll('.bloque-jornada-lloros');
    const bloquesGoles = document.querySelectorAll('.bloque-jornada-goles');

    if (bloquesLloros.length > 0) {
        // PANTALLA LLOROS: Iniciamos en la ÚLTIMA jornada
        indiceActual = bloquesLloros.length - 1; 
        actualizarVistaLlorometro();
    } else {
        // PANTALLA GOLEADORES: Iniciamos en -1 (Todo)
        indiceActual = -1;
        if (typeof actualizarVista === "function") actualizarVista();
    }
};

function actualizarVista() {
    // 1. Referencias rápidas a elementos del DOM
    const bloquesJornada = document.querySelectorAll('.bloque-jornada-goles:not(#jornada-goles-todos)');
    const totalJornadas = bloquesJornada.length;
    const vistaTodo = document.getElementById('jornada-goles-todos');
    
    const btnPrev = document.getElementById('btnPrev');
    const btnSigui = document.getElementById('btnSigui');
    const label = document.getElementById('labelTipoVista');
    const titulo = document.getElementById('numJornadaActual');

    // 2. Ocultamos todos los bloques primero (Limpieza)
    document.querySelectorAll('.bloque-jornada-goles').forEach(b => b.style.display = 'none');

    // 3. CONTROL DE FLECHAS (Lógica de límites)
    // Flecha Izquierda (<): Se oculta si estamos en "Todo" (-1)
    if (btnPrev) btnPrev.style.visibility = (indiceActual === -1) ? 'hidden' : 'visible';

    // Flecha Derecha (>): Se oculta si estamos en la ÚLTIMA jornada (total - 1)
    if (btnSigui) btnSigui.style.visibility = (indiceActual >= totalJornadas - 1) ? 'hidden' : 'visible';

    // 4. RENDERIZADO DE CONTENIDO
    if (indiceActual === -1) {
        // Vista RESUMEN (Todo)
        label.innerText = "RESUMEN";
        titulo.innerText = "Todo";
        if (vistaTodo) vistaTodo.style.display = 'block';
    } else {
        // Vista JORNADA INDIVIDUAL
        const bloqueActivo = bloquesJornada[indiceActual];
        if (bloqueActivo) {
            bloqueActivo.style.display = 'block';
            
            // Extraemos el número real de la jornada del ID
            const jId = bloqueActivo.id.replace('jornada-goles-', '');
            label.innerText = "JORNADA";
            titulo.innerText = jId;
        }
    }
}

function actualizarVistaLlorometro() {
    const bloques = document.querySelectorAll('.bloque-jornada-lloros');
    const total = bloques.length;
    
    // Ocultar todos
    bloques.forEach(b => b.style.display = 'none');

    const bloqueActivo = bloques[indiceActual];
    if (bloqueActivo) {
        bloqueActivo.style.display = 'block';

        // Extraer número del ID: 'bloque-jornada-lloros-25' -> '25'
        const numJornada = bloqueActivo.id.split('-').pop();
        const display = document.getElementById('numJornadaActual');
        if (display) display.innerText = numJornada;

        // Control de flechas
        const btnPrev = document.getElementById('btnPrev');
        const btnSigui = document.getElementById('btnSigui');
        
        if (btnPrev) btnPrev.style.visibility = (indiceActual === 0) ? 'hidden' : 'visible';
        if (btnSigui) btnSigui.style.visibility = (indiceActual >= total - 1) ? 'hidden' : 'visible';
    }
}

let indiceRango = 0; // 0 es "Todo"

function cambiarJornada(direccion) {
    const bloquesJornada = document.querySelectorAll('.bloque-jornada-goles:not(#jornada-goles-todos)');
    const totalJornadas = bloquesJornada.length;

    indiceActual += direccion;

    // Límites de navegación
    if (indiceActual < -1) indiceActual = -1;
    if (indiceActual >= totalJornadas) indiceActual = totalJornadas - 1;

    actualizarVista();
}

function cambiarJornadaLloros(direccion) {
    const bloques = document.querySelectorAll('.bloque-jornada-lloros');
    const total = bloques.length;

    if (total === 0) return;

    indiceActual += direccion;

    // LÍMITES LLOROS: No bajamos de 0 porque no hay vista "Todo"
    if (indiceActual < 0) indiceActual = 0;
    if (indiceActual >= total) indiceActual = total - 1;

    actualizarVistaLlorometro();
}


// Abre el modal al pulsar el número central
function abrirSelector() {
    document.getElementById('selectorJornadaModal').style.display = 'block';
}

function cerrarSelector() {
    document.getElementById('selectorJornadaModal').style.display = 'none';
}

// Función para salto directo
function irAJornadaDirecta(jornada) {
    const bloquesLloros = document.querySelectorAll('.bloque-jornada-lloros');
    
    if (bloquesLloros.length > 0) {
        // LÓGICA PARA LLOROS
        for(let i = 0; i < bloquesLloros.length; i++) {
            // Buscamos con tu nuevo prefijo de ID: 'bloque-jornada-lloros-'
            if(bloquesLloros[i].id === 'bloque-jornada-lloros-' + jornada) {
                indiceActual = i;
                break;
            }
        }
        cerrarSelector();
        actualizarVistaLlorometro();
    } else {
        // LÓGICA PARA GOLEADORES
        if (jornada === -1) {
            indiceActual = -1;
        } else {
            const bloquesGoles = document.querySelectorAll('.bloque-jornada-goles:not(#jornada-goles-todos)');
            for(let i = 0; i < bloquesGoles.length; i++) {
                if(bloquesGoles[i].id === 'jornada-goles-' + jornada) {
                    indiceActual = i;
                    break;
                }
            }
        }
        cerrarSelector();
        actualizarVista();
    }
}

// Cerrar si pulsan fuera del cuadro blanco
window.onclick = function(event) {
    let modal = document.getElementById('selectorJornadaModal');
    if (event.target == modal) cerrarSelector();
}