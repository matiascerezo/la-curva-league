package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import java.math.BigDecimal;
import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;


public interface GoleadorDTO extends FormatPosicion {
    String getImgEquipo();
    String getEquipo();
    Long getGolesTotalesEquipo();
    String getPichichi();
    String getPosicion();
    String getPosicionCorta();
    Integer getGolesPichichi();
    BigDecimal getMediaGolesXJornada();
}