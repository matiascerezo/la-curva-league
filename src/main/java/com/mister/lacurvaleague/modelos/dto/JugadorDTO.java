package com.mister.lacurvaleague.modelos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Creamos Getters, Setters, toString, equals y hashCode
@NoArgsConstructor //Creamos el constructor vacío
@AllArgsConstructor //Creamos el constructor con todos los campos
public class JugadorDTO {

    private String nombre;
    private int puntos;
    private Integer goles;
    private Integer asistencias;
    private Integer amarillas;
    private Integer rojas;
    private Boolean partidoJugado;
    private boolean xiIdeal;
    private Integer penaltisParados;

}
