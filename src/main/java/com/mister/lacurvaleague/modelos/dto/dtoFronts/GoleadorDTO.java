package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import java.math.BigDecimal;

import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoleadorDTO implements FormatPosicion {

    private String imgEquipo;
    private String equipo;
    private Long golesTotalesEquipo;
    private String pichichi; 
    private String posicion; 
    private Integer golesPichichi;
    private BigDecimal mediaGolesXJornada;

    
}