package com.mister.lacurvaleague.modelos.dto.util;

public interface FormatPosicion {
    
    default String getPosicionAbreviada(String posicion) {
        if (posicion == null) return "DESC";
        return switch (posicion.toLowerCase()) {
            case "delantero" -> "DL";
            case "centrocampista" -> "MC";
            case "defensa" -> "DF";
            case "portero" -> "PT";
            default -> "DESC";
        };
    }
}
