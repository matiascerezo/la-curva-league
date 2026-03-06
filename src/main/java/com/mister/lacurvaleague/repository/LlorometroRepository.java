package com.mister.lacurvaleague.repository;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Llorometro;
import com.mister.lacurvaleague.modelos.dto.LlorometroDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingLlorosDTO;

@Repository
public interface LlorometroRepository extends JpaRepository<Llorometro, Long> {

    @Query(value = "SELECT * FROM LLOROMETRO l where l.jornada_id = :jornadaId", nativeQuery = true)
    List<Llorometro> getLlorosJornadaX(Long jornadaId);

    @Query(value = "SELECT * FROM LLOROMETRO l where l.mister_id = :misterId", nativeQuery = true)
    List<Llorometro> getLlorosXMister(Long misterId);

    @Query(value = "SELECT l FROM LLOROMETRO l where l.mister_id = :misterId", nativeQuery = true)
    List<LlorometroDTO> getLlorosTotales();

    @Query(value = "SELECT l.motivo FROM LLOROMETRO l", nativeQuery = true)
    Set<String> findAllMotivos();

    
    @Query("SELECT new com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingLlorosDTO(" +
				"jor.numeroJornada, m.imgEquipo, m.nombreEquipo, l.motivo) " +
				"FROM Llorometro l "+
				"JOIN l.mister m "+
				"JOIN l.jornada jor "+
				"ORDER BY nombreEquipo ASC")
		List<RankingLlorosDTO> getLlorosEquipos();

}
