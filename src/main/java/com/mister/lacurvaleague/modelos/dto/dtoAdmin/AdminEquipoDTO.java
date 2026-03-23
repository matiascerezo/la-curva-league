package com.mister.lacurvaleague.modelos.dto.dtoAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEquipoDTO {
    
    private long equipoId;
    private int posicionJornada;
    private int puntosJornada;
    private long jornadaId;
    private long misterId;
}
