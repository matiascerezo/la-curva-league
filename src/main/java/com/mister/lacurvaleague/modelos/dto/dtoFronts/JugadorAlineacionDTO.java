package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorAlineacionDTO {
    private Long id;
    private Long jugadorId;
    private String posicion;
    private String posicionCorta;
    private String nombre;
    private Integer puntos;
    private Integer goles;
    private Integer asistencias;
    private Integer amarillas;
    private Integer rojas;
    private Boolean partidoJugado;
    private Boolean xiIdeal;
    private Boolean penaltisParados;
    private Boolean pendiente;
    private Boolean capitan;
    private Double multiplicadorCapitan;
}