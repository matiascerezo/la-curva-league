package com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas;

import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MvpDTO implements FormatPosicion{
    
    private String imgEquipo;
    private String nombreEquipo;
    private String nombreJugador;
    private String posicionCorta;
    private Long totalMvps;
}
