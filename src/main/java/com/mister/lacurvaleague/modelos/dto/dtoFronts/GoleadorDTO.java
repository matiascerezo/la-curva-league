package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoleadorDTO {

    private String equipo;
    private Long golesTotalesEquipo;
    private String pichichi; 
    private String posicion; 
    private Integer golesPichichi;
    private BigDecimal mediaGolesXJornada;

    public String getPosicionAbreviada() {
        if (this.posicion == null) return "DESC";
        return switch (this.posicion.toLowerCase()) {
            case "delantero" -> "DL";
            case "centrocampista" -> "MC";
            case "defensa" -> "DF";
            case "portero" -> "PT";
            default -> "DESC";
        };
    }
}