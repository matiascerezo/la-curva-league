package com.mister.lacurvaleague.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Mister;

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

}
