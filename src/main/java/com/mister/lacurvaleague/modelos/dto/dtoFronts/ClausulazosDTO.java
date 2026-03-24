package com.mister.lacurvaleague.modelos.dto.dtoFronts;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Genera el constructor con todos los campos
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // Crea constructor vacío para Jackson
public class ClausulazosDTO {

    private String imgComprador;
    private String misterComprador;
    private String imgVendedor;
    private String misterVendedor;
    private String nombreJugador;
    private String posicionJugador;
    private Double precioPagado;
    
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaCompra;
 
}
