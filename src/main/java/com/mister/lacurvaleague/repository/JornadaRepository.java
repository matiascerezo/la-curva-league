package com.mister.lacurvaleague.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Jornada;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    Optional<Jornada> findByNumeroJornada(int numeroJornada);


    @Query(value = "SELECT MAX(id) AS jornadaActual " +
                   "FROM JORNADA j", nativeQuery = true)
    String getJornadaActual();

}
