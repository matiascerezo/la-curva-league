package com.mister.lacurvaleague.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JugadorReal {

    @Id
    private String nombreCortoJugador;
    private String nombreJugador;
    private String posicion;
}
