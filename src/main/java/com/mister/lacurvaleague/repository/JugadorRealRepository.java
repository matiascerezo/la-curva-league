package com.mister.lacurvaleague.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.JugadorReal;

@Repository
public interface JugadorRealRepository extends JpaRepository<JugadorReal, String> {

    @Query("SELECT j.nombreJugador FROM JugadorReal j")
    Set<String> findAllNombres();

}