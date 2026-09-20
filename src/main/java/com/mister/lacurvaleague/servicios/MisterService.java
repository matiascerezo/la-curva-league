package com.mister.lacurvaleague.servicios;

import com.mister.lacurvaleague.repository.JugadorRepository;
import com.mister.lacurvaleague.repository.LlorometroRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.AsistenciaDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazoIndividualDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.GoleadorDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingAsistenciasDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingGolesDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MejorJornadaDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MvpDTO;
import com.mister.lacurvaleague.repository.ClausulazoRepository;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.MisterRepository;


@Service
public class MisterService {

    private final JugadorRepository jugadorRepository;
    private final JornadaRepository jornadaRepository; 
    private final EquipoRepository equipoRepository;
    private final MisterRepository misterRepository;
    private final LlorometroRepository llorometroRepository;
    private final ClausulazoRepository clausulazoRepository;

    MisterService(JugadorRepository jugadorRepository, JornadaRepository jornadaRepository, EquipoRepository equipoRepository,
                    MisterRepository misterRepository, LlorometroRepository llorometroRepository, ClausulazoRepository clausulazoRepository) {
        this.jugadorRepository = jugadorRepository;
        this.jornadaRepository = jornadaRepository;
        this.equipoRepository = equipoRepository;
        this.misterRepository = misterRepository;
        this.llorometroRepository = llorometroRepository;
        this.clausulazoRepository = clausulazoRepository;
    }

    /**
     * Para el top 3 equipos con jugadores que más puntos han restado.
     */
    public record EquipoLastreDTO(String imgEquipo, String nombreEquipo, Long puntos) {}

    public record JugadoresMasPuntosDTO(String imgEquipo, String nombreEquipo, String nombre, String posicionCorta, Long puntos) {}

    public record MistersMasLloronesDTO(String imgEquipo, String nombreEquipo, Long llorosTotales) {}

    public record MistersMasClausulazosDTO(String imgEquipo, String nombreEquipo, Long totalClausulazos) {}

    public record MistersMasGastonesDTO(String imgEquipo, String nombreEquipo, Double totalGastado) {}

    public record PuntosCapitanesDTO(String imgEquipo, String nombreEquipo, Long puntosXCapitan) {}

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

    public List<ClasificacionGeneralDTO> getClasificacionPrimeraVuelta(){
        return equipoRepository.getClasificacionGeneralPorRango(1,19);
    }

    public List<ClasificacionGeneralDTO> getClasificacionSegundaVuelta(){
        return equipoRepository.getClasificacionGeneralPorRango(20,38);
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

    public List<Mister> getTodosLosMisters(){
        return misterRepository.findAll();
    }

    public List<Mister> getTodosMistersOrdenados() {
        return misterRepository.findAllByOrderByNombreEquipoAsc();
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

    public List<MejorJornadaDTO> getTop3MejoresJornadas() {
        return jornadaRepository.getTop3MejoresJornadas();
    }

    public List<MejorJornadaDTO> getTop3PeoresJornadas() {
        return jornadaRepository.getTop3PeoresJornadas();
    }

    public List<MvpDTO> getMVP(){
        return equipoRepository.getMVP();
    }

    public List<MvpDTO> getPeoresJugadores(){
        return equipoRepository.getPeoresJugadores();
    }

    public List<EquipoLastreDTO> getPuntosNegativosXEquipo(Pageable pag) {
        return jugadorRepository.getPuntosNegativosXEquipo(pag);
    }

    public List<JugadoresMasPuntosDTO> getListJugadoresMasPuntosTotales(String nombreEquipo, Pageable pag) {
        List<JugadoresMasPuntosDTO> resultado = jugadorRepository.getJugadoresMasPuntos(nombreEquipo, pag);    
        return resultado.isEmpty() ? null : resultado;
    }

    public JugadoresMasPuntosDTO getListJugadoresMasPuntosTotXEquipo(String nombreEquipo, Pageable pag) {
        return getListJugadoresMasPuntosTotales(nombreEquipo, pag).get(0);
    }

    public List<MistersMasLloronesDTO> getListJugadoresMasLloronesTotales(String nombreEquipo, Pageable pag) {
        return llorometroRepository.getListJugadoresMasLlorosTotales(nombreEquipo, pag);
    }

    public List<ClausulazoIndividualDTO> getClausulazosTotalesOrderFechaDesc() {
        return clausulazoRepository.getAllClausulazosOrdenadosFechaDesc();
    }

    public List<MistersMasClausulazosDTO> getListMasClausulazosTotales() {
        return clausulazoRepository.findTotalClausulazosPorMister();
    }

    public List<MistersMasClausulazosDTO> getListMasClausulazosRecibidos() {
        return clausulazoRepository.findTotalClausulazosRecibidosPorMister();
    }

    public List<MistersMasGastonesDTO> getListMasGastonesEnClausulazos() {
        return clausulazoRepository.findTotalDineroGastadoClausulazosPorCantidad();
    }

    public Set<Integer> getListaJornadas() {
        return jornadaRepository.findAllNumerosJornada();
    }

    public List<PuntosCapitanesDTO> getPuntosXCapitanes() {
        return jugadorRepository.findTotalPuntosCapitan();
    }
    
}
