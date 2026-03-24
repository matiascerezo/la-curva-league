package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Clausulazos;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazosDTO;

@Repository
public interface ClausulazoRepository extends JpaRepository<Clausulazos, Long> {

        @Query("SELECT new com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazosDTO( "+
                        "m1.imgEquipo as imgComprador, "+
                        "m1.nombreEquipo as misterComprador, "+
                        "m2.imgEquipo as imgVendedor, "+
                        "m2.nombreEquipo as misterVendedor, "+
                        "c.nombreJugador, "+
                        "c.posicionJugador, "+
                        "c.precioPagado, "+
                        "c.fechaCompra) "+
                    "FROM Clausulazos c "+
                    "JOIN Mister m1 ON c.misterComprador = m1.nombreEquipo "+
                    "JOIN Mister m2 ON c.misterVendedor = m2.nombreEquipo")
    List<ClausulazosDTO> getAllClausulazos();
}
