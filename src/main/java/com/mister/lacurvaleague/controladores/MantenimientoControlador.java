package com.mister.lacurvaleague.controladores;

import com.mister.lacurvaleague.repository.JornadaRepository;
import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mister.lacurvaleague.servicios.MantenimientoDatosService;



@RestController
@RequestMapping("/mantenimiento")
public class MantenimientoControlador {

    private final MantenimientoDatosService mantenimientoService;

    MantenimientoControlador(JornadaRepository jornadaRepository, MantenimientoDatosService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @GetMapping("/cargarJornada/{numeroJornada}")
    public String cargarDatosJornada(@PathVariable int numeroJornada) {
       return mantenimientoService.procesarTodasLasJornadas(numeroJornada);
    }

    @GetMapping("/cargarTodasJornadas")
    public String cargarDatosTodasLasJornadas() {
        return mantenimientoService.procesarTodasLasJornadas(null);
    }

    @GetMapping("/cargarJugadores")
    public String cargarDatosTodosLosJugadores() {
        return mantenimientoService.cargarJugadoresReales(true, null);
    }

    @GetMapping("/cargarMisters")
    public String cargarMisters() {
        return mantenimientoService.cargarMisters(null);
    }

    @GetMapping("/cargarLloros")
    public String cargarLlorometro() {
        return mantenimientoService.procesarTodosLosLloros(true, null, null);
    }

    
    @GetMapping(value = "/cargarTodo")
    public ResponseEntity<Void> cargarTodo(){
        long inicio = System.currentTimeMillis();
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- INFORME DE CARGA ---\n");
        reporte.append(cargarMisters()).append("\n");
        reporte.append(cargarDatosTodosLosJugadores()).append("\n");
        reporte.append(cargarDatosTodasLasJornadas()).append("\n");
        reporte.append(cargarLlorometro()).append("\n");
        long fin = System.currentTimeMillis();
        reporte.append("Tiempo total: " + (fin - inicio) / 1000.0 + " segundos");
        reporte.append("------------------------\n");
        reporte.append("Estado: ¡Todo ha ido bien!");

       System.out.println(reporte.toString());

       return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("/inicio"))
            .build();
    }
}
