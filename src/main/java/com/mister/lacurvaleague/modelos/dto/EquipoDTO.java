package com.mister.lacurvaleague.modelos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Esta anotación crea: Getters, Setters, toString, equals y hashCode
@NoArgsConstructor // Crea el constructor vacío (necesario para Jackson)
@AllArgsConstructor // Crea un constructor con todos los campos
public class EquipoDTO {

    private String nombreEquipo;
    private String nombreMister;
    private int puntosJornada;
    private int posicionJornada;
    private List<JugadorDTO> jugadores;
}
