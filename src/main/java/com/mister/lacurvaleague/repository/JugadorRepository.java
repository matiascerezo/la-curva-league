package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Jugador;
import com.mister.lacurvaleague.servicios.MisterService.EquipoLastreDTO;
import com.mister.lacurvaleague.servicios.MisterService.JugadoresMasPuntosDTO;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$EquipoLastreDTO(m.imgEquipo, m.nombreEquipo, SUM(j.puntos)) " +
       "FROM Jugador j " +
       "JOIN j.equipo e " +
       "JOIN e.mister m "+
       "WHERE j.puntos < 0 " +
       "GROUP BY m.imgEquipo, m.nombreEquipo " +
       "ORDER BY SUM(j.puntos) ASC")
    List<EquipoLastreDTO> getPuntosNegativosXEquipo(Pageable pageable);

    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$JugadoresMasPuntosDTO(" +
                "e.mister.imgEquipo, e.mister.nombreEquipo, j.nombre, j.posicionCorta, SUM(j.puntos)) " +
            "FROM Jugador j " +
            "JOIN j.equipo e " +
            "WHERE (:nombreEquipo IS NULL OR e.mister.nombreEquipo = :nombreEquipo) " +
            "GROUP BY e.mister.imgEquipo, e.mister.nombreEquipo, j.nombre, j.posicionCorta " +
            "ORDER BY SUM(j.puntos) DESC")
    List<JugadoresMasPuntosDTO> getJugadoresMasPuntos(String nombreEquipo, Pageable pageable);
}
