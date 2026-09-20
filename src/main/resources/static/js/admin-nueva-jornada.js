document.addEventListener('DOMContentLoaded', () => {
    configurarDatePicker();
});

function configurarDatePicker() {
    const inputFechaInicio = document.getElementById('fechaInicio');
    const inputFechaFin = document.getElementById('fechaFin');
    const hoy = new Date().toISOString().split('T')[0];
    if (inputFechaInicio && !inputFechaInicio.value) inputFechaInicio.value = hoy;
    if (inputFechaFin && !inputFechaFin.value) inputFechaFin.value = hoy;
}