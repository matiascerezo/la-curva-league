package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

        @Query(value = "SELECT m.nombre_equipo as nombreEquipo, SUM(e.puntos_jornada) as puntosTotales " +
            "FROM mister m " +
            "join equipo e on e.mister_id = m.id " +
            "GROUP BY m.nombre_equipo " +
            "ORDER BY puntosTotales DESC", nativeQuery = true)
        List<ClasificacionGeneralDTO> getClasificacionGeneral();

        @Query(value = "SELECT e.jornada_id as jornadaId, e.puntos_jornada as puntosJornada, e.posicion_jornada as posicionJornada, m.nombre_equipo as nombreEquipo "+
                       "FROM EQUIPO e "+
                       "join mister m on m.id = e.mister_id " +
                       "where e.mister_id = ? " +
                       "ORDER BY jornada_id ASC", nativeQuery = true)
        List<ClasificacionEquipoDTO> getClasificacionEquipo(Long misterId);
}
