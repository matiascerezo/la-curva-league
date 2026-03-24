package com.mister.lacurvaleague.modelos;

import java.time.LocalDate;

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
    private String misterComprador;
    private String misterVendedor;
    private String nombreJugador;
    private String posicionJugador;
    private Double precioPagado;
    private LocalDate fechaCompra;
}
