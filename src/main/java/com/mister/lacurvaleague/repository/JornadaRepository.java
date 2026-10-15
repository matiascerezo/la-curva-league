package com.mister.lacurvaleague.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.mister.lacurvaleague.modelos.Jornada;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MejorJornadaDTO;

public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    Jornada findByNumeroJornada(int numeroJornada);

    @Query(value = "SELECT MAX(j.numero_jornada) AS jornadaActual " +
                   "FROM JORNADA j", nativeQuery = true)
    String getJornadaActual();

    @Query("SELECT j.numeroJornada FROM Jornada j")
    Set<Integer> findAllNumerosJornada();

    @Query(value = """
                    SELECT 
                        m.img_equipo AS imgEquipo, 
                        m.nombre_equipo AS equipo, 
                        CAST(COALESCE(e.puntos_jornada, 0) AS BIGINT) as puntos 
                    FROM mister m
                    LEFT JOIN equipo e ON e.mister_id = m.mister_id
                        GROUP BY m.img_equipo, m.nombre_equipo, CAST(COALESCE(e.puntos_jornada, 0) AS BIGINT)
                    ORDER BY puntos DESC, equipo ASC
                    LIMIT 3
                    """, nativeQuery = true)
    List<MejorJornadaDTO> getTop3MejoresJornadas();

    @Query(value = """
                    SELECT 
                        m.img_equipo AS imgEquipo, 
                        m.nombre_equipo AS equipo, 
                        CAST(e.puntos_jornada AS BIGINT) AS puntos
                    FROM mister m
                    LEFT JOIN equipo e ON e.mister_id = m.mister_id
                        GROUP BY m.img_equipo, m.nombre_equipo, e.puntos_jornada
                    ORDER BY puntos ASC, equipo ASC
                    LIMIT 3
                    """, nativeQuery = true)
    List<MejorJornadaDTO> getTop3PeoresJornadas();

    @Query("SELECT j.jornadaId FROM Jornada j WHERE j.numeroJornada = :numeroJornada")
    Long findIdByNumeroJornada(@Param("numeroJornada") int numeroJornada);

    @Query("SELECT j.observaciones FROM Jornada j WHERE j.jornadaId = :jornada")
    String findObservacionesByJornadaId(@Param("jornada") int jornada );


}

