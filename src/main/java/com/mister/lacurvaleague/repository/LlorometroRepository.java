package com.mister.lacurvaleague.repository;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mister.lacurvaleague.modelos.Llorometro;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LlorometroDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingLlorosDTO;
import com.mister.lacurvaleague.servicios.MisterService.MistersMasLloronesDTO;

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

    @Query("SELECT 1 FROM Llorometro l JOIN l.jornada jor where jor.numeroJornada = :numeroJornada")
    String existenLlorosJornadasX(@Param("numeroJornada") int numeroJornada);
    
    @Query("SELECT new com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingLlorosDTO(" +
				"jor.numeroJornada, m.imgEquipo, m.nombreEquipo, l.motivo) " +
				"FROM Llorometro l "+
				"JOIN l.mister m "+
				"JOIN l.jornada jor "+
				"ORDER BY nombreEquipo ASC")
	List<RankingLlorosDTO> getLlorosEquipos();

    
    @Query(value = "SELECT COUNT(*) FROM llorometro l " +
                    "WHERE l.jornada_id IN (" +
                                            "  SELECT jornada_id FROM jornada " +
                                            "  ORDER BY numero_jornada DESC " +
                                            "  LIMIT 3" +
                                            ")" +
                    "AND (:misterId IS NULL OR l.mister_id = :misterId)", nativeQuery = true)
    int getLloros3UltimasJornadas(@Param("misterId") Long misterId);

    @Query(value = "SELECT new com.mister.lacurvaleague.servicios.MisterService$MistersMasLloronesDTO( "+ 
                        "m.imgEquipo, m.nombreEquipo, COUNT(*) AS llorosTotales)" +
                    "FROM Llorometro l " +
                    "JOIN l.mister m "+
                    "GROUP BY imgEquipo, nombreEquipo "+
                    "ORDER BY llorosTotales DESC")
    List<MistersMasLloronesDTO> getListJugadoresMasLlorosTotales(String nombreEquipo, Pageable pageable);

}
