package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingGolesDTO implements FormatPosicion {

    private String imgEquipo;
    private String nombreEquipo;
    private Integer goles;
    private String nombreJugador;
    private String posicion;
    private Integer numeroJornada;
}

