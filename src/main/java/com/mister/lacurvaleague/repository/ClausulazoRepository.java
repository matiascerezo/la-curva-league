package com.mister.lacurvaleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Clausulazos;

@Repository
public interface ClausulazoRepository extends JpaRepository<Clausulazos, Long> {

}
