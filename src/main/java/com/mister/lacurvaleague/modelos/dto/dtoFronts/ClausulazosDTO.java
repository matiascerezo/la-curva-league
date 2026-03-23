package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import java.util.Date;

import lombok.Value;

@Value
public class ClausulazosDTO {

    private String misterComprador;
    private String misterVendedor;
    private String nombreJugador;
    private String posicionJugador;
    private Double precioPagado;
    private Date fechaCompra;
 
}
