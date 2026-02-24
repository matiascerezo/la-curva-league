package com.mister.lacurvaleague.modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String posicion;
    private int puntos;
    private int goles = 0;
    private int asistencias = 0;
    private int amarillas = 0;
    private int rojas = 0;
    private Boolean partidoJugado = true;
    private boolean xiIdeal = false;
    private int penaltisParados = 0;

    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

}
