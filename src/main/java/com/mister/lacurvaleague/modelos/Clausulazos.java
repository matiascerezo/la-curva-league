package com.mister.lacurvaleague.modelos;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Clausulazos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clausulazoId;

    @Column(name = "mister_comprador", nullable = false)
    private String misterComprador;

    @Column(name = "mister_vendedor", nullable = false)
    private String misterVendedor;

    private String posicionJugador;

    @Column(name = "nombre_jugador", nullable = false)
    private String nombreJugador;

    @Column(name = "precio_pagado", nullable = false)
    private Double precioPagado;

    @Column(name = "fecha_compra")
    private Date fechaCompra;
}
