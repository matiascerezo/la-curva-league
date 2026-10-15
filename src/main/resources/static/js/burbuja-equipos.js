
// Definimos los rangos basados en tu secuencia de 8
const rangos = [
    { inicio: 1, fin: 38, texto: "Todo" },
    { inicio: 1, fin: 8, texto: "J1-J8" },
    { inicio: 9, fin: 16, texto: "J9-J16" },
    { inicio: 17, fin: 24, texto: "J17-J24" },
    { inicio: 25, fin: 32, texto: "J25-J32" },
    { inicio: 33, fin: 38, texto: "J33-J38" }
];

let indiceRango = 0; // 0 es "Todo"

function actualizarInterfazRango() {

    const titleElement = document.getElementById('title');
    const isInicio = titleElement && titleElement.innerText.includes('Clasificación General');

    if(!isInicio) {
        const r = rangos[indiceRango];
    
        // Actualizar Textos
        document.getElementById('textoRangoActual').innerText = r.texto;
        document.getElementById('labelRango').innerText = (indiceRango === 0) ? "RESUMEN" : "PUNTUACIÓN";
        
        // Gestionar visibilidad de flechas (el < desaparece en Todo)
        document.getElementById('btnPrev').style.visibility = (indiceRango === 0) ? 'hidden' : 'visible';
        document.getElementById('btnSigui').style.visibility = (indiceRango === rangos.length - 1) ? 'hidden' : 'visible';

        // Llamar a tu función original para filtrar tabla y gráfico
        filtrarPorRango(r.inicio, r.fin, null);
    }
}

function cambiarRango(direccion) {
    indiceRango += direccion;
    if (indiceRango < 0) indiceRango = 0;
    if (indiceRango >= rangos.length) indiceRango = rangos.length - 1;
    actualizarInterfazRango();
}

function irARangoDirecto(indice) {
    indiceRango = indice;
    cerrarSelector();
    actualizarInterfazRango();
}

function abrirSelectorRangos() { document.getElementById('modalRangos').style.display = 'block'; }
function cerrarSelector() { document.getElementById('modalRangos').style.display = 'none'; }

// Inicialización
document.addEventListener("DOMContentLoaded", () => {
    actualizarInterfazRango();
});

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

function cargarTablaClasificacionPorRango(rango, elemento) {
    // 1. Efecto visual: Cambiar botón activo
    document.querySelectorAll('.btn-rango').forEach(btn => btn.classList.remove('active'));
    elemento.classList.add('active');

    // 2. Cambiar el texto de la cabecera de puntos (opcional)
    const cabecera = document.getElementById('cabeceraPuntos');
    if (rango === 0) cabecera.innerText = "Puntos Totales";
    else if (rango === 1) cabecera.innerText = "Puntos Ida";
    else cabecera.innerText = "Puntos Vuelta";

    // 3. Llamada al fragmento de Spring
    fetch(`/clasificacion/${rango}`)
        .then(response => response.text())
        .then(html => {
            // Reemplazamos solo el contenido del tbody
            document.getElementById('contenedor-filas').innerHTML = html;
        })
        .catch(err => console.error("Error al cargar el rango:", err));
}

async function compartirImagenClasificacion() {
    // 1. Elemento HTML que contiene la tabla de clasificación
    const tablaElemento = document.querySelector('#tabla-clasificacion'); 

    if (!tablaElemento) {
        alert('No se encontró la tabla de clasificación.');
        return;
    }

    try {
        // 2. Generar el canvas de la tabla
        const canvas = await html2canvas(tablaElemento, {
            scale: 2, // Mayor resolución
            useCORS: true, // Permitir cargar escudos/imágenes externas
            backgroundColor: '#ffffff'
        });

        // 3. Convertir a Blob/Imagen
        canvas.toBlob(async (blob) => {
            const file = new File([blob], 'clasificacion-lcl.png', { type: 'image/png' });

            // 4. Usar la Web Share API (Nativa en móviles iOS/Android)
            if (navigator.canShare && navigator.canShare({ files: [file] })) {
                await navigator.share({
                    files: [file],
                    title: 'La Curva League',
                    text: '🏆 ¡Así va la clasificación de La Curva League!'
                });
            } else {
                // Fallback para escritorio: Descargar la imagen y abrir WhatsApp Web
                const link = document.createElement('a');
                link.download = 'clasificacion-lcl.png';
                link.href = canvas.toDataURL();
                link.click();
                
                const textoWhatsApp = encodeURIComponent("🏆 ¡Mira la clasificación actualizada de La Curva League! https://www.lacurvaleague.es/inicio");
                window.open(`https://api.whatsapp.com/send?text=${textoWhatsApp}`, '_blank');
            }
        }, 'image/png');

    } catch (error) {
        console.error('Error al generar la imagen:', error);
    }
}