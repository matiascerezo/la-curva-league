package com.mister.lacurvaleague.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mister.lacurvaleague.servicios.MantenimientoDatosService;


@RestController
@RequestMapping("/mantenimiento")
public class MantenimientoControlador {

    @Autowired
    private MantenimientoDatosService mantenimientoService;

    @GetMapping("/cargarJornada/{numeroJornada}")
    public String cargarDatosJornada(@PathVariable int numeroJornada) {
        mantenimientoService.procesarJornada(numeroJornada);
        return "¡Datos de la Jornada " + numeroJornada + " cargados correctamente en la base de datos!";
    }

    @GetMapping("/cargarTodasJornadas")
    public String cargarDatosTodasLasJornadas() {
        int jornadas = mantenimientoService.procesarTodasLasJornadas();
        return "Jornadas cargadas: " + jornadas;
    }

    @GetMapping("/cargarJugadores")
    public String cargarDatosTodosLosJugadores() {
        return mantenimientoService.cargarJugadoresReales();
    }

    @GetMapping("/cargarMisters")
    public String cargarMisters() {
        return mantenimientoService.cargarMisters();
    }
    
    @GetMapping(value = "/cargarTodo", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String cargarTodo(){
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("--- INFORME DE CARGA ---\n");
        reporte.append(cargarMisters()).append("\n");
        reporte.append(cargarDatosTodosLosJugadores()).append("\n");
        reporte.append(cargarDatosTodasLasJornadas()).append("\n");
        reporte.append("------------------------\n");
        reporte.append("Estado: ¡Todo ha ido bien!");

        return reporte.toString();
    }
}
