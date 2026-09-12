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

    /**
     * Recibe una posición abreviada y devuelve posicion completa.
     * @param posicionAbr
     * @return
     */
   static String getPosicionCompleta(String posicionAbr) {
        if (posicionAbr == null) return "DESC";
        return switch (posicionAbr.toLowerCase()) {
            case "dl" -> "Delantero";
            case "mc" -> "Centrocampista";
            case "df" -> "Defensa";
            case "pt" -> "Portero";
            default -> "DESC";
        };
    }
}
