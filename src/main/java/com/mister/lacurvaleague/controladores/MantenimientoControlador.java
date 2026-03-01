package com.mister.lacurvaleague.controladores;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mister.lacurvaleague.servicios.MantenimientoDatosService;



@RestController
@RequestMapping("/mantenimiento")
public class MantenimientoControlador {

    @Autowired
    private MantenimientoDatosService mantenimientoService;

    @GetMapping("/cargarJornada/{numeroJornada}")
    public String cargarDatosJornada(@PathVariable int numeroJornada) {
        //mantenimientoService.procesarJornada(numeroJornada);
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

    @GetMapping("/cargarLloros")
    public String cargarLlorometro() {
        return mantenimientoService.procesarTodosLosLloros();
    }
    
    
    @GetMapping(value = "/cargarTodo")
    public ResponseEntity<Void> cargarTodo(){
        StringBuilder reporte = new StringBuilder();
        
        reporte.append("--- INFORME DE CARGA ---\n");
        reporte.append(cargarMisters()).append("\n");
        reporte.append(cargarDatosTodosLosJugadores()).append("\n");
        reporte.append(cargarDatosTodasLasJornadas()).append("\n");
        reporte.append(cargarLlorometro()).append("\n");
        reporte.append("------------------------\n");
        reporte.append("Estado: ¡Todo ha ido bien!");

       System.out.println(reporte.toString());
       return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("/inicio"))
            .build();
    }
}
