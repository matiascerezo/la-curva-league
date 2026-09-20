package com.mister.lacurvaleague.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.mister.lacurvaleague.modelos.Esquema;


public interface EsquemaRepository extends JpaRepository<Esquema, Long> {

    Esquema findByEquipoEquipoId(Long equipoId);

    @Modifying
    @Query("DELETE FROM Esquema e WHERE e.equipo.equipoId = :equipoId")
    void eliminarPorEquipoId(@Param("equipoId") Long equipoId);
}
