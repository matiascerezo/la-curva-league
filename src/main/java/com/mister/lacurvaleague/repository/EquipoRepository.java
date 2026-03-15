package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.AsistenciaDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.GoleadorDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingAsistenciasDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingGolesDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MvpDTO;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

        @Query(value = "SELECT m.img_equipo, m.nombre_equipo as nombreEquipo, SUM(e.puntos_jornada) as puntosTotales, "+
								"SUM(e.puntos_jornada) - LAG(SUM(e.puntos_jornada)) OVER (ORDER BY SUM(e.puntos_jornada) DESC) AS difPuntos " +
						"FROM mister m " +
						"join equipo e on e.mister_id = m.MISTER_ID " +
						"GROUP BY m.nombre_equipo, m.img_equipo " +
						"ORDER BY puntosTotales DESC", nativeQuery = true)
        List<ClasificacionGeneralDTO> getClasificacionGeneral();

        @Query(value = "SELECT j.numero_jornada as jornadaId, e.puntos_jornada as puntosJornada, e.posicion_jornada as posicionJornada, m.nombre_equipo as nombreEquipo "+
                       "FROM EQUIPO e "+
                       "join mister m on m.MISTER_ID = e.mister_id " +
                       "join jornada j on j.jornada_id = e.jornada_id " +
                       "where e.mister_id = :misterId " +
                       "ORDER BY j.numero_jornada DESC", nativeQuery = true)
        List<ClasificacionEquipoDTO> getClasificacionEquipo(Long misterId);

        @Query(value = """
				WITH GolesPorJugador AS (
						SELECT 
							m.img_equipo AS imgEquipo, 
							m.nombre_equipo AS equipo, 
							j.nombre AS nombrePichichi, 
							j.posicion AS posicionPichichi, 
							j.posicion_corta AS posicionCorta, 
							SUM(j.goles) AS golesTotalesJugador, 
							SUM(SUM(j.goles)) OVER (PARTITION BY m.nombre_equipo) AS golesTotalesEquipo 
						FROM jugador j 
						JOIN equipo e ON j.equipo_id = e.equipo_id 
						JOIN mister m ON e.mister_id = m.mister_id 
						GROUP BY m.img_equipo, m.nombre_equipo, j.nombre, j.posicion, j.posicion_corta 
					), 
					RankingPichichis AS ( 
						SELECT *, 
							ROW_NUMBER() OVER ( 
								PARTITION BY equipo 
								ORDER BY golesTotalesJugador DESC, nombrePichichi ASC 
							) AS ranking 
						FROM GolesPorJugador 
					) 
					SELECT 
						imgEquipo, 
						equipo, 
						golesTotalesEquipo, 
						nombrePichichi AS pichichi, 
						posicionPichichi AS posicion, 
						posicionCorta, 
						golesTotalesJugador AS golesPichichi, 
						(golesTotalesEquipo * 1.0 / NULLIF((SELECT MAX(jornada_id) FROM jornada), 0)) AS mediaGolesXJornada 
					FROM RankingPichichis 
					WHERE ranking = 1 
					ORDER BY golesTotalesEquipo DESC""", nativeQuery = true)
				List<GoleadorDTO> getGolesYGoleadoresXEquipo();

        @Query(value = """
				WITH AsistenciasPorJugador AS (
					SELECT 
						m.img_equipo AS imgEquipo, 
						m.nombre_equipo AS equipo, 
						j.nombre AS maxAsistente, 
						j.posicion AS posicion, 
						j.posicion_corta AS posicionCorta, 
						SUM(j.asistencias) AS asistencias, 
						SUM(SUM(j.asistencias)) OVER (PARTITION BY m.nombre_equipo) AS asistenciasTotalesEquipo 
					FROM jugador j 
					JOIN equipo e ON j.equipo_id = e.equipo_id 
					JOIN mister m ON e.mister_id = m.mister_id 
					GROUP BY m.img_equipo, m.nombre_equipo, j.nombre, j.posicion, j.posicion_corta 
				), 
				RankingAsistentes AS ( 
					SELECT *, 
						ROW_NUMBER() OVER ( 
							PARTITION BY equipo 
							ORDER BY asistencias DESC, maxAsistente ASC 
						) AS ranking 
					FROM AsistenciasPorJugador 
				) 
				SELECT 
					imgEquipo, 
					equipo, 
					asistenciasTotalesEquipo, 
					maxAsistente, 
					posicion, 
					posicionCorta, 
					asistencias, 
					CAST(asistenciasTotalesEquipo * 1.0 / NULLIF((SELECT MAX(jornada_id) FROM jornada), 0) AS NUMERIC(10,2)) AS mediaAsistenciasXJornada 
				FROM RankingAsistentes 
				WHERE ranking = 1 
				ORDER BY asistenciasTotalesEquipo DESC
				""", nativeQuery = true)
			List<AsistenciaDTO> getAsistenciasYAsistentesXEquipo();

		@Query("SELECT new com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingAsistenciasDTO(" +
				"m.imgEquipo, m.nombreEquipo, j.asistencias, j.nombre, j.posicion, j.posicionCorta, jor.numeroJornada) " +
				"FROM Jugador j " + // Asumiendo que la Entidad se llama Jugador
				"JOIN j.equipo e " + // Relación en la clase Jugador
				"JOIN e.mister m " + // Relación en la clase Equipo
				"JOIN e.jornada jor " + // Relación en la clase Equipo
				"WHERE j.asistencias > 0 " +
				"ORDER BY nombreEquipo ASC, asistencias DESC")
		List<RankingAsistenciasDTO> getAsistenciasEquipos();

		@Query("SELECT new com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingGolesDTO(" +
				"m.imgEquipo, m.nombreEquipo, j.goles, j.nombre, j.posicion, j.posicionCorta, jor.numeroJornada) " +
				"FROM Jugador j " +
				"JOIN j.equipo e " +
				"JOIN e.mister m " + 
				"JOIN e.jornada jor " + 
				"WHERE j.goles > 0 " +
				"ORDER BY nombreEquipo ASC, goles DESC")
		List<RankingGolesDTO> getGolesEquipos();

		@Query(value = """
				SELECT 
					m.img_equipo AS imgEquipo,
					m.nombre_equipo AS nombreEquipo,
					j.nombre AS nombreJugador,
					j.posicion_corta AS posicion,
					COUNT(j.xi_ideal) AS totalMvps
				FROM jugador j
				JOIN equipo e ON j.equipo_id = e.equipo_id
				JOIN mister m ON e.mister_id = m.mister_id
				WHERE j.xi_ideal = true
				GROUP BY j.nombre, j.posicion_corta, m.nombre_equipo, m.img_equipo
				ORDER BY totalMvps DESC, j.nombre ASC
				LIMIT 3
				""", nativeQuery = true)
		List<MvpDTO> getMVP();

		@Query(value = """
				SELECT 
					m.img_equipo AS imgEquipo, 
					m.nombre_equipo AS nombreEquipo, 
					j.nombre AS nombreJugador, 
					j.posicion_corta AS posicion, 
					SUM(j.puntos) AS totalMvps --realmente = puntos 
				FROM jugador j 
				JOIN equipo e ON j.equipo_id = e.equipo_id 
				JOIN mister m ON e.mister_id = m.mister_id 
				WHERE j.puntos < 0 
				GROUP BY j.nombre, j.posicion_corta, m.nombre_equipo, m.img_equipo 
				ORDER BY totalMvps ASC, j.nombre ASC 
				LIMIT 7
				""", nativeQuery = true)
		List<MvpDTO> getPeoresJugadores();
}
