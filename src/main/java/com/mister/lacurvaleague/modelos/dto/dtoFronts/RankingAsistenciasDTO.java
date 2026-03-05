package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingAsistenciasDTO implements FormatPosicion {

    private String imgEquipo;
    private String nombreEquipo;
    private Integer asistencias;
    private String nombreJugador;
    private String posicion;
    private Integer numeroJornada;
}

