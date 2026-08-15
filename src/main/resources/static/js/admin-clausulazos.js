
document.addEventListener('DOMContentLoaded', () => {
    cargarEquipos('misterComprador', 'misterVendedor');
    configurarDatePicker();
});


/**
 * Configura el datepicker con valor por defecto "hoy" y limita a fechas futuras
 */
function configurarDatePicker() {
    const inputFecha = document.getElementById('fechaCompra');
    const hoy = new Date().toISOString().split('T')[0];
    inputFecha.value = hoy;
    inputFecha.max = hoy;
}

/**
 * Envía el clausulazo vía POST
 */
async function guardarClausulazo(event) {
    event.preventDefault();

    const comprador = document.getElementById('misterComprador').value;
    const vendedor = document.getElementById('misterVendedor').value;

    if (comprador === vendedor) {
        alert('El míster comprador y vendedor no pueden ser el mismo equipo.');
        return;
    }

    const datos = [{
        misterComprador: comprador,
        misterVendedor: vendedor,
        nombreJugador: document.getElementById('nombreJugador').value.trim(),
        posicionJugador: document.getElementById('posicionJugador').value,
        precioPagado: parseInt(document.getElementById('precioPagado').value, 10),
        fechaCompra: document.getElementById('fechaCompra').value
    }];

    try {
        const respuesta = await fetch('/mantenimiento/admin/guardarClausulazo', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        if (respuesta.ok) {
            const mensaje = await respuesta.text();
            alert('¡Operación realizada con éxito!\n' + mensaje);
            
            // Resetear formulario y restaurar logos por defecto
            document.getElementById('formClausulazo').reset();
            actualizarLogo('misterComprador', 'imgComprador');
            actualizarLogo('misterVendedor', 'imgVendedor');
            configurarDatePicker();
        } else {
            alert('Error en el servidor al guardar el clausulazo.');
        }
    } catch (error) {
        console.error('Error en la petición POST:', error);
        alert('Error de comunicación con el servidor.');
    }
}