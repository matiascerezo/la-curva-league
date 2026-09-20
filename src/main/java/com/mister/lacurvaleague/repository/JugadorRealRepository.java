package com.mister.lacurvaleague.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mister.lacurvaleague.modelos.JugadorReal;

public interface JugadorRealRepository extends JpaRepository<JugadorReal, String> {

    @Query("SELECT j.nombreJugador FROM JugadorReal j")
    Set<String> findAllNombres();

}