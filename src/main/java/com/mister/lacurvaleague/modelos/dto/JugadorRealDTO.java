package com.mister.lacurvaleague.modelos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Esta anotación crea: Getters, Setters, toString, equals y hashCode
@NoArgsConstructor // Crea el constructor vacío (necesario para Jackson)
@AllArgsConstructor // Crea un constructor con todos los campos
public class JugadorRealDTO {

    private String nombreCortoJugador;
    private String nombreJugador;
    private String posicion;
    
}
