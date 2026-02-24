package com.mister.lacurvaleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.JugadorReal;

@Repository
public interface JugadorRealRepository extends JpaRepository<JugadorReal, String> {

}