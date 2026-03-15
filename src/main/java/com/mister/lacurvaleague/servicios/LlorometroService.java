package com.mister.lacurvaleague.servicios;

import com.mister.lacurvaleague.modelos.Llorometro;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LloroDetalleDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LlorometroDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.MisterLlorosDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingLlorosDTO;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.LlorometroRepository;
import com.mister.lacurvaleague.repository.MisterRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LlorometroService {

    @Autowired
    LlorometroRepository llorometroRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    MisterRepository misterRepository;

    public List<Llorometro> getLlorosJornadaX(Long jornadaId){
        return llorometroRepository.getLlorosJornadaX(jornadaId);
    }

    public List<LlorometroDTO> getAllLlorosParaVista() {
        List<Llorometro> lloros = llorometroRepository.findAll();

        // 2. Agrupamos por número de jornada
        Map<Integer, List<Llorometro>> porJornada = lloros.stream()
            .collect(Collectors.groupingBy(l -> l.getJornada().getNumeroJornada()));

        List<LlorometroDTO> listaFinal = new ArrayList<>();

        porJornada.forEach((numJornada, listaLloros) -> {
            LlorometroDTO jornadaDto = new LlorometroDTO();
            jornadaDto.setNumeroJornada(numJornada);

            Map<String, List<Llorometro>> porMister = listaLloros.stream()
                .collect(Collectors.groupingBy(l -> l.getMister().getNombreEquipo()));

            List<MisterLlorosDTO> listaEquiposDto = new ArrayList<>();
            porMister.forEach((nombreEquipo, llorosMister) -> {
                MisterLlorosDTO misterDto = new MisterLlorosDTO();
                misterDto.setNombreMister(nombreEquipo);

                List<LloroDetalleDTO> detalles = llorosMister.stream()
                    .map(l -> {
                        LloroDetalleDTO d = new LloroDetalleDTO();
                        d.setMotivo(l.getMotivo());
                        return d;
                    }).collect(Collectors.toList());

                misterDto.setLloros(detalles);
                listaEquiposDto.add(misterDto);
            });

            jornadaDto.setEquiposLloros(listaEquiposDto);
            listaFinal.add(jornadaDto);
        });
        return listaFinal;
    }

    public List<RankingLlorosDTO> getLlorosEquipos(){
        return llorometroRepository.getLlorosEquipos();
    }

    /**
     * Conversor de lloros según la cantidad total de todos los misters o solamente de uno.
     * @param misterId
     * @return
     */
    public Map<String, Object> getLloros3UltimasJornadas(Long misterId){
        
        int cantidadLloros = llorometroRepository.getLloros3UltimasJornadas(misterId);
        
        //Si no tenemos misterId, calculamos el máximo de lloros "globales".
        int maximoCriterio = (misterId == null) ? 15 : 6; 
        
        // 3. Calculamos porcentaje
        double porcentaje = (cantidadLloros * 100.0) / maximoCriterio;
        if (porcentaje > 100) porcentaje = 100; //No deberiamos sobrepasar el 100% de lloros, pero por si acaso.
        if (porcentaje == 0) { porcentaje = 5; //Si no tenemos lloros registrados, ponemos 5% para que se vea la linea con algo de contenido.
            
        }

        // 4. Determinamos el texto del nivel
        String etiqueta;
        if (porcentaje < 30) etiqueta = "BAJO";
        else if (porcentaje < 70) etiqueta = "MEDIO";
        else etiqueta = "ALTO";

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("porcentaje", (int) porcentaje);
        respuesta.put("etiqueta", etiqueta);
        
        return respuesta;
    }
}