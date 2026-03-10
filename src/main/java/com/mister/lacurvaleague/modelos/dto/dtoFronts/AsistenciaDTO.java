package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import java.math.BigDecimal;
import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;

public interface AsistenciaDTO extends FormatPosicion {
    String getImgEquipo();
    String getEquipo();
    Long getAsistenciasTotalesEquipo();
    String getMaxAsistente(); 
    String getPosicion(); 
    String getPosicionCorta(); 
    Integer getAsistencias();
    BigDecimal getMediaAsistenciasXJornada();
}