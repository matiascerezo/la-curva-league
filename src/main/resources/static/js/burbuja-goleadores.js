// Variable global para controlar la posición actual
// Se inicializa en -1 para mostrar la vista "Todo" al cargar
let indiceActual = -1; 

function actualizarVista() {
    // 1. Ocultamos todos los bloques de jornadas
    document.querySelectorAll('.bloque-jornada-goles').forEach(bloque => {
        bloque.style.display = 'none';
    });

    const label = document.getElementById('labelTipoVista');
    const titulo = document.getElementById('numJornadaActual');

    // 2. Lógica para mostrar el bloque correspondiente
    if (indiceActual === -1) {
        label.innerText = "RESUMEN";
        titulo.innerText = "Todo";
        const vistaTodo = document.getElementById('jornada-goles-todos');
        if(vistaTodo) vistaTodo.style.display = 'block';
    } else {
        // Obtenemos el ID de la jornada (ej: 1, 2, 26...)
        // Nota: Asegúrate de que las jornadas están disponibles en el DOM
        const bloquesJornada = document.querySelectorAll('.bloque-jornada-goles:not(#jornada-goles-todos)');
        const jId = bloquesJornada[indiceActual].id.replace('jornada-goles-', '');
        
        label.innerText = "JORNADA";
        titulo.innerText = jId;
        
        const target = document.getElementById('jornada-goles-' + jId);
        if (target) target.style.display = 'block';
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

// Abre el modal al pulsar el número central
function abrirSelector() {
    document.getElementById('selectorJornadaModal').style.display = 'block';
}

function cerrarSelector() {
    document.getElementById('selectorJornadaModal').style.display = 'none';
}

// Función para salto directo
function irAJornadaDirecta(jornada) {
    if (jornada === -1) {
        indiceActual = -1;
    } else {
        // Buscamos la posición de esa jornada en el array de jornadas disponibles
        const bloquesJornada = document.querySelectorAll('.bloque-jornada-goles:not(#jornada-goles-todos)');
        for(let i = 0; i < bloquesJornada.length; i++) {
            if(bloquesJornada[i].id === 'jornada-goles-' + jornada) {
                indiceActual = i;
                break;
            }
        }
    }
    cerrarSelector();
    actualizarVista();
}

// Cerrar si pulsan fuera del cuadro blanco
window.onclick = function(event) {
    let modal = document.getElementById('selectorJornadaModal');
    if (event.target == modal) cerrarSelector();
}