package com.mister.lacurvaleague.modelos.dto.dtoFronts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import com.mister.lacurvaleague.modelos.Jugador;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlineacionJornadaIndividualDTO {
    Long misterId;
    Integer numeroJornada;
    String esquema;
    List<Jugador> jugadores;
}