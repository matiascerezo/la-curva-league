package com.mister.lacurvaleague.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.AsistenciaDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.GoleadorDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingAsistenciasDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingGolesDTO;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.MisterRepository;

@Service
public class MisterService {

    @Autowired
    private JornadaRepository jornadaRepository;
    
    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private MisterRepository misterRepository;

    public List<ClasificacionEquipoDTO> obtenerPuntosEquipoXJornada(String nombreMister){
        return equipoRepository.getClasificacionEquipo(getIdEquipo(nombreMister));
    }

    public List<ClasificacionEquipoDTO> obtenerPuntosEquipoXJornadaByURL(String nombreMisterURL){
        Long idMister = getMisterByURL(nombreMisterURL).getMisterId();
        return equipoRepository.getClasificacionEquipo(idMister);
    }

    public Mister getMisterByURL(String nombreMisterURL){
        return misterRepository.findByNombreEquipo(nombreMisterURL);
    }

    public Long getIdEquipo(String nombreMister) {
        return misterRepository.getMisterByNombreEquipo(nombreMister).getMisterId();
    }

    public List<ClasificacionGeneralDTO> getClasificacionGeneral(){
        return equipoRepository.getClasificacionGeneral();
    }

    public Optional<Equipo> getEquipo(long id) {
        return equipoRepository.findById(id);
    }

    public String getJornadaActual() {
        return jornadaRepository.getJornadaActual();
    }

    public String getNombreEquipo(long id){
        return misterRepository.findById(id).stream().map(m -> m.getNombreEquipo()).toString();
    }

    public String getNombreEquipoByURL(String nombreEquipoURL) {
        return misterRepository.findByNombreEquipo(nombreEquipoURL).getNombreEquipo();
    }

    public List<GoleadorDTO> getGolesYGoleadoresXEquipo(){
        return equipoRepository.getGolesYGoleadoresXEquipo();
    }

    public List<AsistenciaDTO> getAsistenciasYAsistentesXEquipo(){
        return equipoRepository.getAsistenciasYAsistentesXEquipo();
    }

    public List<RankingAsistenciasDTO> getAsistenciasEquipos(){
        return equipoRepository.getAsistenciasEquipos();
    }

    public List<RankingGolesDTO> getGolesEquipos(){
        return equipoRepository.getGolesEquipos();
    }

    public String getImgEquipo(String nombreMisterURL){
        return getMisterByURL(nombreMisterURL).getImgEquipo();
    }
}
