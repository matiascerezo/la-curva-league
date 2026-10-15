package com.mister.lacurvaleague.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mister.lacurvaleague.modelos.Clausulazos;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazoIndividualDTO;
import com.mister.lacurvaleague.servicios.MisterService.MistersMasClausulazosDTO;
import com.mister.lacurvaleague.servicios.MisterService.MistersMasGastonesDTO;

public interface ClausulazoRepository extends JpaRepository<Clausulazos, Long> {

    @Query("SELECT new com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazoIndividualDTO( "+
                    "m1.imgEquipo as imgComprador, "+
                    "m1.nombreEquipo as misterComprador, "+
                    "m2.imgEquipo as imgVendedor, "+
                    "m2.nombreEquipo as misterVendedor, "+
                    "c.nombreJugador, "+
                    "c.posicionJugador, "+
                    "c.precioPagado, "+
                    "c.fechaCompra, "+
                    "c.jugadorRealId) "+
                "FROM Clausulazos c "+
                "JOIN Mister m1 ON c.misterComprador = m1.nombreEquipo "+
                "JOIN Mister m2 ON c.misterVendedor = m2.nombreEquipo " +
                "ORDER BY c.fechaCompra desc, c.clausulazoId desc")
    List<ClausulazoIndividualDTO> getAllClausulazosOrdenadosFechaDesc();

    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$MistersMasClausulazosDTO( "+
                    "m1.imgEquipo as imgComprador, " +
                    "m1.nombreEquipo as misterComprador, " +
                    "COUNT(c.nombreJugador) as totalClausulazos) "+ 
                "FROM Mister m1 " +
                "LEFT JOIN Clausulazos c ON c.misterComprador = m1.nombreEquipo " +
                "GROUP BY m1.imgEquipo, m1.nombreEquipo " +
                "ORDER BY totalClausulazos DESC")
    List<MistersMasClausulazosDTO> findTotalClausulazosPorMister();

    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$MistersMasClausulazosDTO( "+
                    "m1.imgEquipo as imgVendedor, " +
                    "m1.nombreEquipo as misterVendedor, " +
                    "COUNT(c.nombreJugador) as totalClausulazos) "+ 
                "FROM Mister m1 " +
                "LEFT JOIN Clausulazos c ON c.misterVendedor = m1.nombreEquipo " +
                "GROUP BY m1.imgEquipo, m1.nombreEquipo " +
                "ORDER BY totalClausulazos DESC")
    List<MistersMasClausulazosDTO> findTotalClausulazosRecibidosPorMister();

    @Query("SELECT new com.mister.lacurvaleague.servicios.MisterService$MistersMasGastonesDTO( "+
                    "m1.imgEquipo as imgComprador, " +
                    "m1.nombreEquipo as misterComprador, " +
                    "COALESCE(SUM(c.precioPagado),0) as totalGastado) "+ 
                "FROM Mister m1 " +
                "LEFT JOIN Clausulazos c ON c.misterComprador = m1.nombreEquipo " +
                "GROUP BY m1.imgEquipo, m1.nombreEquipo " +
                "ORDER BY totalGastado DESC")
    List<MistersMasGastonesDTO> findTotalDineroGastadoClausulazosPorCantidad();
}
