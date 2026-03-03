package com.mister.lacurvaleague.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Jornada;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    Jornada findByNumeroJornada(int numeroJornada);

    @Query(value = "SELECT MAX(j.numero_jornada) AS jornadaActual " +
                   "FROM JORNADA j", nativeQuery = true)
    String getJornadaActual();

    @Query("SELECT j.numeroJornada FROM Jornada j")
    Set<Integer> findAllNumerosJornada();

}

