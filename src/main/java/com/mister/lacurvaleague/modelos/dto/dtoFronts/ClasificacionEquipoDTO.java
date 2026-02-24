package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import lombok.Value;

@Value
public class ClasificacionEquipoDTO {

    Long jornadaId;
    int puntosJornada;
    int posicionJornada;
    String nombreEquipo;
 
}
