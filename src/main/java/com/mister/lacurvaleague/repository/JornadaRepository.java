package com.mister.lacurvaleague.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Jornada;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MejorJornadaDTO;

@Repository
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
                        e.puntos_jornada AS puntos 
                    FROM mister m
                    JOIN equipo e ON e.mister_id = m.mister_id
                    ORDER BY puntos DESC, equipo ASC
                    LIMIT 3
                    """, nativeQuery = true)
    List<MejorJornadaDTO> getTop3MejoresJornadas();

    @Query(value = """
                    SELECT 
                        m.img_equipo AS imgEquipo, 
                        m.nombre_equipo AS equipo, 
                        e.puntos_jornada AS puntos 
                    FROM mister m
                    JOIN equipo e ON e.mister_id = m.mister_id
                    ORDER BY puntos ASC, equipo ASC
                    LIMIT 3
                    """, nativeQuery = true)
    List<MejorJornadaDTO> getTop3PeoresJornadas();

}

