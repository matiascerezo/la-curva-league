package com.mister.lacurvaleague.servicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mister.lacurvaleague.modelos.Jornada;
import com.mister.lacurvaleague.modelos.Llorometro;
import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.dto.LlorometroDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LloroDetalleDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.MisterLlorosDTO;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.LlorometroRepository;
import com.mister.lacurvaleague.repository.MisterRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class LlorometroService {

    @Autowired
    LlorometroRepository llorometroRepository;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    MisterRepository misterRepository;

    public String procesarTodosLosLloros() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Leemos el archivo y lo convertimos en una lista de nuestras jornadas
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/lloros/lloros.json");
            List<LlorometroDTO> listLlorometroDTOs = mapper.readValue(inputStream, new TypeReference<List<LlorometroDTO>>(){});
            for (LlorometroDTO lloroDTO : listLlorometroDTOs) {
                Jornada jornada = jornadaRepository.findByNumeroJornada(lloroDTO.getNumeroJornada());
                for(MisterLlorosDTO equipoLloroDTO : lloroDTO.getEquiposLloros()){
                    Mister mister = misterRepository.getMisterByNombreEquipo(equipoLloroDTO.getNombreMister());
                    for(LloroDetalleDTO lloroDetalleDTO : equipoLloroDTO.getLloros()) {
                        Llorometro lloro = new Llorometro();
                        lloro.setJornada(jornada);
                        lloro.setMister(mister);
                        lloro.setMotivo(lloroDetalleDTO.getMotivo());             
                        llorometroRepository.save(lloro);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al insertar lloros.";
        }
        return "Lloros procesados.";
    }

    public List<Llorometro> getLlorosJornadaX(Long jornadaId){
        return llorometroRepository.getLlorosJornadaX(jornadaId);
    }

    public List<LlorometroDTO> getAllLlorosParaVista() {
        List<Llorometro> entidades = llorometroRepository.findAll();

        // 2. Agrupamos por número de jornada
        Map<Integer, List<Llorometro>> porJornada = entidades.stream()
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
}