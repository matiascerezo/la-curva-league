package com.mister.lacurvaleague.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.TarjetasDTO;

@Repository
public interface MisterRepository extends JpaRepository<Mister, Long> {

    @Query(value = "SELECT m.id " +
                "FROM MISTER m " + 
                "WHERE m.NOMBRE_EQUIPO = :nombreEquipo", nativeQuery = true)
    Long getMisterIdByNombreMister(String misterNombre);

    @Query(value = "SELECT * FROM MISTER m WHERE m.NOMBRE_MISTER = ?", nativeQuery = true)
    Mister getMisterByNombreMister(String misterNombre);

    @Query(value = "SELECT m.* FROM MISTER m WHERE m.url_equipo = ?", nativeQuery = true)
    Optional<Mister> findByNombreEquipo(String urlEquipo);

    @Query(value = "SELECT m.NOMBRE_EQUIPO AS mister, SUM(J.AMARILLAS) AS amarillas, SUM(J.ROJAS) AS rojas,SUM(J.AMARILLAS) + (SUM(J.ROJAS)) AS totalTarjetas " +
                    "FROM MISTER M "+
                    "JOIN EQUIPO E ON M.ID = E.MISTER_ID "+
                    "JOIN JUGADOR J ON E.ID = J.EQUIPO_ID "+
                    "GROUP BY M.NOMBRE_EQUIPO "+
                    "ORDER BY NOMBRE_EQUIPO DESC", nativeQuery = true)
    List<TarjetasDTO> getTarjetasTotales();

}
