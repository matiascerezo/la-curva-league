package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.GoleadorDTO;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

        @Query(value = "SELECT m.nombre_equipo as nombreEquipo, SUM(e.puntos_jornada) as puntosTotales " +
            "FROM mister m " +
            "join equipo e on e.mister_id = m.MISTER_ID " +
            "GROUP BY m.nombre_equipo " +
            "ORDER BY puntosTotales DESC", nativeQuery = true)
        List<ClasificacionGeneralDTO> getClasificacionGeneral();

        @Query(value = "SELECT j.numero_jornada as jornadaId, e.puntos_jornada as puntosJornada, e.posicion_jornada as posicionJornada, m.nombre_equipo as nombreEquipo "+
                       "FROM EQUIPO e "+
                       "join mister m on m.MISTER_ID = e.mister_id " +
                       "join jornada j on j.jornada_id = e.jornada_id " +
                       "where e.mister_id = :misterId " +
                       "ORDER BY j.numero_jornada DESC", nativeQuery = true)
        List<ClasificacionEquipoDTO> getClasificacionEquipo(Long misterId);

        @Query(value =  "SELECT equipo, golesTotalesEquipo, nombrePichichi AS pichichi, posicionPichichi AS posicion, golesPichichi, "+
						"(golesTotalesEquipo * 1.0 / (SELECT MAX(jornada_id) FROM jornada)) AS mediaGolesXJornada "+
						"FROM ( "+
								"SELECT m.nombre_equipo AS equipo, j.nombre AS nombrePichichi,"+
									"j.posicion AS posicionPichichi, j.goles AS golesPichichi,"+
									"SUM(j.goles) OVER (PARTITION BY m.nombre_equipo) AS golesTotalesEquipo, "+
									"ROW_NUMBER() OVER (PARTITION BY m.nombre_equipo "+ 
                                                        "ORDER BY j.goles DESC, j.nombre ASC "+
                                                        ") as ranking "+
								"FROM jugador j "+
								"JOIN equipo e ON j.equipo_id = e.equipo_id "+
								"JOIN mister m ON e.mister_id = m.MISTER_ID "+
							") AS subq "+
							"WHERE ranking = 1 "+ 
							"ORDER BY golesTotalesEquipo DESC", nativeQuery = true)
        List<GoleadorDTO> getGolesYGoleadoresXEquipo();
}
