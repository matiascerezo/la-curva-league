package com.mister.lacurvaleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Jugador;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

}
