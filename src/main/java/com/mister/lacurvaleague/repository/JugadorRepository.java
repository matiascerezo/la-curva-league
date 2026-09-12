package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Jugador;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.JugadorDTO;
import com.mister.lacurvaleague.servicios.MisterService.EquipoLastreDTO;
import com.mister.lacurvaleague.servicios.MisterService.JugadoresMasPuntosDTO;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$EquipoLastreDTO(" +
       "m.imgEquipo, " +
       "m.nombreEquipo, " +
       "COALESCE(SUM(CASE WHEN j.puntos < 0 THEN j.puntos ELSE 0 END), 0L)) " +
       "FROM Equipo e " +
       "RIGHT JOIN e.mister m " + 
       "LEFT JOIN e.jugadores j " +
       "GROUP BY m.imgEquipo, m.nombreEquipo " +
       "ORDER BY COALESCE(SUM(CASE WHEN j.puntos < 0 THEN j.puntos ELSE 0 END), 0L) ASC")
    List<EquipoLastreDTO> getPuntosNegativosXEquipo(Pageable pageable);


    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$JugadoresMasPuntosDTO(" +
                "e.mister.imgEquipo, e.mister.nombreEquipo, j.nombre, j.posicionCorta, SUM(j.puntos)) " +
            "FROM Jugador j " +
            "JOIN j.equipo e " +
            "WHERE (:nombreEquipo IS NULL OR e.mister.nombreEquipo = :nombreEquipo) " +
            "GROUP BY e.mister.imgEquipo, e.mister.nombreEquipo, j.nombre, j.posicionCorta " +
            "ORDER BY SUM(j.puntos) DESC")
    List<JugadoresMasPuntosDTO> getJugadoresMasPuntos(String nombreEquipo, Pageable pageable);

    List<JugadorDTO> findByEquipo(Equipo equipo);
}
