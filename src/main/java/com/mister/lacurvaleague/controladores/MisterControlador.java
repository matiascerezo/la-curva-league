package com.mister.lacurvaleague.controladores;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Jornada;
import com.mister.lacurvaleague.modelos.Jugador;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.GoleadorDTO;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.JugadorRepository;
import com.mister.lacurvaleague.repository.MisterRepository;
import com.mister.lacurvaleague.servicios.MisterService;


@RestController
@RequestMapping("/stats")
public class MisterControlador {

    @Autowired
    private MisterRepository misterRepository;
    @Autowired
    private MisterService misterService;
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private JornadaRepository jornadaRepository;
    @Autowired
    private EquipoRepository equipoRepository;

    public String getJornadaActual() {
        return misterService.getJornadaActual();
    }
    

    @GetMapping("/tablaPuntos")
    public List<ClasificacionGeneralDTO> getClasificacionGeneral(){
        return misterService.getClasificacionGeneral();
    }

    @GetMapping("/jugadores")
    public List<Jugador> verTodosLosJugadores() {
        // Esto devuelve la lista completa de lo que hay en la tabla
        return jugadorRepository.findAll();
    }

    @GetMapping("/jornadas")
    public List<Jornada> verTodasLasJornadas() {
        // Esto devuelve la lista completa de lo que hay en la tabla
        return jornadaRepository.findAll();
    }

    @GetMapping("/equipos")
    public List<Equipo> verTodosLosEquipos() {
        // Esto devuelve la lista completa de lo que hay en la tabla
        return equipoRepository.findAll();
    }

    public List<Jugador> obtenerGoleadores() {
        return jugadorRepository.findAll()
                                .stream()
                                .filter(j-> j.getGoles()>0)
                                .toList();
    }

    public Long obtenerMisterId(String nombreMister) {
        return misterRepository.getMisterIdByNombreMister(nombreMister);
    }
}
