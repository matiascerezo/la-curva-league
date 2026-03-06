package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingLlorosDTO implements FormatPosicion {

    private int numeroJornada;
    private String imgEquipo;
    private String nombreEquipo;
    private String motivo;
}

