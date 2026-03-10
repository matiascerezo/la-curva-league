package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import lombok.Value;

@Value
public class ClasificacionGeneralDTO {

    String imgEquipo;
    String nombreEquipo;
    Long puntosTotales;
    Long difPuntos; //diferencia con el de arriba
 
}
