package com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MejorJornadaDTO {
    
    private String imgEquipo;
    private String nombreEquipo;
    private Integer puntos;
}
