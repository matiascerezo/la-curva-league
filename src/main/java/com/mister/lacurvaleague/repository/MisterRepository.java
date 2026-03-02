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

    @Query(value = "SELECT * FROM MISTER m WHERE m.NOMBRE_EQUIPO = :nombreEquipo", nativeQuery = true)
    Mister getMisterByNombreEquipo(String nombreEquipo);

    @Query(value = "SELECT * FROM MISTER m WHERE m.NOMBRE_MISTER = :misterNombre", nativeQuery = true)
    Mister getMisterByNombreMister(String misterNombre);

    @Query(value = "SELECT m.* FROM MISTER m WHERE m.url_equipo = :urlEquipo", nativeQuery = true)
    Optional<Mister> findByNombreEquipo(String urlEquipo);

    @Query(value = "SELECT m.NOMBRE_EQUIPO AS mister, SUM(J.AMARILLAS) AS amarillas, SUM(J.ROJAS) AS rojas,SUM(J.AMARILLAS) + (SUM(J.ROJAS)) AS totalTarjetas " +
                    "FROM MISTER M "+
                    "JOIN EQUIPO E ON M.MISTER_ID = E.MISTER_ID "+
                    "JOIN JUGADOR J ON E.equipo_id = J.EQUIPO_ID "+
                    "GROUP BY M.NOMBRE_EQUIPO "+
                    "ORDER BY totalTarjetas DESC", nativeQuery = true)
    List<TarjetasDTO> getTarjetasTotales();

}
