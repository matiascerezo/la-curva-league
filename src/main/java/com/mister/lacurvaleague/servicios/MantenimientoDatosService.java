package com.mister.lacurvaleague.servicios;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.Jornada;
import com.mister.lacurvaleague.modelos.Jugador;
import com.mister.lacurvaleague.modelos.JugadorReal;
import com.mister.lacurvaleague.modelos.Llorometro;
import com.mister.lacurvaleague.modelos.dto.EquipoDTO;
import com.mister.lacurvaleague.modelos.dto.JornadaDTO;
import com.mister.lacurvaleague.modelos.dto.JugadorDTO;
import com.mister.lacurvaleague.modelos.dto.JugadorRealDTO;
import com.mister.lacurvaleague.modelos.dto.LlorometroDTO;
import com.mister.lacurvaleague.modelos.dto.MisterDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LloroDetalleDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.MisterLlorosDTO;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.JugadorRealRepository;
import com.mister.lacurvaleague.repository.JugadorRepository;
import com.mister.lacurvaleague.repository.LlorometroRepository;
import com.mister.lacurvaleague.repository.MisterRepository;

import jakarta.transaction.Transactional;

@Service
public class MantenimientoDatosService {

    @Value("${path.json_jornadas}")
    private String PATH_JSON_JORNADA;

    @Value("${path.json_jugadores}")
    private String PATH_JSON_JUGADORES;

    @Value("${path.json_misters}")
    private String PATH_JSON_MISTERS;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JugadorRepository jugadorRepository;   
    @Autowired
    private JugadorRealRepository jugadorRealRepository; 
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private JornadaRepository jornadaRepository;
    @Autowired
    private MisterRepository misterRepository;
    @Autowired
    private LlorometroRepository llorometroRepository;

    /**
     * Método que lee el fichero .json de la jornada recibida por parámetro.
     * @return Jornada con los datos del fichero .json
     */
    @Transactional
    public void procesarJornada(Resource recurso) {
        List<Jornada> listaJornadas = new ArrayList<>();
        List<Equipo> listaEquipos = new ArrayList<>();
        List<Jugador> listaJugadores = new ArrayList<>();

        try (InputStream is = recurso.getInputStream()) {
            
            JornadaDTO jornadaDTO = objectMapper.readValue(is, JornadaDTO.class);
            Set<Integer> jornadasEnBBDD = jornadaRepository.findAllNumerosJornada();
            Map<String, String> mapaPosiciones = jugadorRealRepository.findAll()
                                .stream()
                                .collect(Collectors.toMap(JugadorReal::getNombreCortoJugador, JugadorReal::getPosicion, (p1, p2) -> p1));
            
            if (!jornadasEnBBDD.contains(jornadaDTO.getNumeroJornada())) {
                Jornada jornada = procesarJornada(jornadaDTO);
                listaJornadas.add(jornada);
                
                for (EquipoDTO equipoDTO : jornadaDTO.getEquipos()) {
                    Equipo equipo = procesarEquipo(equipoDTO, jornada);
                    listaEquipos.add(equipo);
                    for (JugadorDTO jugadorDTO : equipoDTO.getJugadores()) { 
                        Jugador j = procesarJugador(jugadorDTO, equipo);
                        String posicion = mapaPosiciones.getOrDefault(jugadorDTO.getNombre(), "Desconocido");
                        j.setPosicion(posicion);
                        listaJugadores.add(j);
                    }
                }
            }         
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el JSON del recurso: " + recurso.getFilename(), e);
        }
        if(!listaJugadores.isEmpty()) {
            jornadaRepository.saveAll(listaJornadas);
            equipoRepository.saveAll(listaEquipos);
            jugadorRepository.saveAll(listaJugadores);
        }
    }

    /**
     * Método que lee la carpeta 'data/jornadas/' y procesa todo lo que hay en formato .json.
     * @return
     */
   public int procesarTodasLasJornadas() {
        int totalJornadas = 0;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources("classpath*:" + PATH_JSON_JORNADA + "*.json");
            totalJornadas = resources.length;

            for (Resource resource : resources) {
                procesarJornada(resource);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Error al localizar JSON en " + PATH_JSON_JORNADA, e);
        }
        return totalJornadas;
    }

    /**
     * Cargar todos los jugadores con su posición real.
     *
     */
    public String cargarJugadoresReales() {

        int jugadoresCargados = 0;
        int jugadoresNoCargados = 0;
        List<JugadorReal> jugadoresRealesACargar = new ArrayList<>();

        try {
            String rutaPathFichero = PATH_JSON_JUGADORES + "jugadores_reales.json";
            
            InputStream is = getClass().getClassLoader().getResourceAsStream(rutaPathFichero);
            List<JugadorRealDTO> listaJugadoresDTO = objectMapper.readValue(is, new TypeReference<List<JugadorRealDTO>>() {});
            Set<String> listaJugadoresBBDD = jugadorRealRepository.findAll()
                                                                    .stream()
                                                                    .map(JugadorReal::getNombreJugador)
                                                                    .collect(Collectors.toSet());

            for (JugadorRealDTO jugadorRealDTO : listaJugadoresDTO) {
                if(!listaJugadoresBBDD.contains(jugadorRealDTO.getNombreJugador())){
                    JugadorReal jr = new JugadorReal();
                    jr.setNombreCortoJugador(jugadorRealDTO.getNombreCortoJugador());
                    jr.setNombreJugador(jugadorRealDTO.getNombreJugador());
                    jr.setPosicion(jugadorRealDTO.getPosicion());
                    jugadoresRealesACargar.add(jr);
                    jugadoresCargados++;
                } else {
                    jugadoresNoCargados++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el JSON de los jugadores", e);
        }

        //Si tenemos jugadores, los guardamos en BBDD.
        if (!jugadoresRealesACargar.isEmpty()) {
            jugadorRealRepository.saveAll(jugadoresRealesACargar);
        }
        return "Jugadores cargados: " + jugadoresCargados + " y no cargados: " + jugadoresNoCargados;
    }

    public Mister insertarMister(MisterDTO misterDto){
        return misterRepository.save(procesarMister(misterDto));
    }

    public Jugador procesarJugador(JugadorDTO jugadorDTO){
        return this.procesarJugador(jugadorDTO, null);
    }
    
    /**
     * Método que parsea el JugadorDTO a Jugador
     * @param jugadorDTO
     * @return Jugador
     */
    public Jugador procesarJugador(JugadorDTO jugadorDTO, Equipo equipo){
        Jugador jugador = new Jugador();
        jugador.setNombre(jugadorDTO.getNombre());
        jugador.setPuntos(jugadorDTO.getPuntos());
        if (jugadorDTO.getGoles() != null) jugador.setGoles((jugadorDTO.getGoles()));
        if (jugadorDTO.getAsistencias() != null) jugador.setAsistencias((jugadorDTO.getAsistencias()));
        if (jugadorDTO.getAmarillas() != null) jugador.setAmarillas(jugadorDTO.getAmarillas());
        if (jugadorDTO.getRojas() != null) jugador.setRojas(jugadorDTO.getRojas());
        if (Boolean.TRUE.equals(jugadorDTO.isXiIdeal())) jugador.setXiIdeal(jugadorDTO.isXiIdeal());
        if (jugadorDTO.getPartidoJugado() != null) jugador.setPartidoJugado(jugadorDTO.getPartidoJugado());
        if(jugadorDTO.getPenaltisParados()!=null) jugador.setPenaltisParados(jugadorDTO.getPenaltisParados());
        jugador.setEquipo(equipo);
        return jugador;
    }

    /**
     * Metodo que parsea el EquipoDTO a un objeto Equipo sin jornada.
     * @param equipoDto
     * @return
     */
    public Equipo procesarEquipo(EquipoDTO equipoDto){
       return procesarEquipo(equipoDto, null);
    }

    /**
     * Método que parsea el EquipoDTO a HistorialEquipo
     * @param historialDTO
     * @return HistorialEquipo
     */
    public Equipo procesarEquipo(EquipoDTO equipoDto, Jornada jornada){
        Equipo equipo = new Equipo();
        equipo.setPuntosJornada(equipoDto.getPuntosJornada());
        equipo.setPosicionJornada(equipoDto.getPosicionJornada());
        equipo.setJugadores(obtenerListaJugadores(equipoDto.getJugadores()));
        equipo.setJornada(jornada);
        equipo.setMister(misterRepository.getMisterByNombreMister(equipoDto.getNombreMister()));
        return equipo;
    }

    public Mister procesarMister(MisterDTO misterDto){
        Mister mister = new Mister();
        mister.setNombreEquipo(misterDto.getNombreEquipo());
        mister.setNombreMister(misterDto.getNombreMister());
        return mister;
    }

    /**
     * Método que parsea el JornadaDTO en objeto Jornada.
     * @param jornadaDto
     * @return
     */
    public Jornada procesarJornada(JornadaDTO jornadaDto){
        Jornada jornada = new Jornada();
        jornada.setNumeroJornada(jornadaDto.getNumeroJornada());
        jornada.setFechaInicio(jornadaDto.getFechaInicio());
        jornada.setFechaFin(jornadaDto.getFechaFin());
        jornada.setEquipos(obtenerListaEquipos((jornadaDto.getEquipos())));
        return jornada;
    }

    /**
     * Mapear la lista de equipos DTO a una lista de equipos
     * @param equiposDto
     * @return
     */
    private List<Equipo> obtenerListaEquipos(List<EquipoDTO> equiposDto){
        List<Equipo> equipos = new ArrayList<>();
        equiposDto.stream()
                            .map(dto -> procesarEquipo(dto))
                            .collect(Collectors.toList());
        return equipos;
    }

    /**
     * Mapear la lista de jugadores del DTOs a una lista de Jugador.
     * @param jugadoresDto
     * @return
     */
    private List<Jugador> obtenerListaJugadores(List<JugadorDTO> jugadoresDto){
        List<Jugador> jugadores = new ArrayList<>();
        jugadoresDto.stream()
                            .map(dto -> procesarJugador(dto))
                            .collect(Collectors.toList());
        return jugadores;
    }

    /**
     * Cargar todos los misters
     *
     */
    public String cargarMisters() {

        try {
            String rutaPathFichero = PATH_JSON_MISTERS + "misters.json";
            
            InputStream is = getClass().getClassLoader().getResourceAsStream(rutaPathFichero);
            List<MisterDTO> misterDTOs = objectMapper.readValue(is, new TypeReference<List<MisterDTO>>() {});

            //Busco los misters ya en BBDD
            Set<String> mistersBBDD = misterRepository.findAll()
                .stream()
                .map(Mister::getNombreMister)
                .collect(Collectors.toSet());

            List<Mister> listaMistersOK = new ArrayList<>();
            for (MisterDTO misterDTO : misterDTOs) {
                if(!mistersBBDD.contains(misterDTO.getNombreEquipo())){
                    Mister m = new Mister();
                    m.setNombreEquipo(misterDTO.getNombreEquipo());
                    m.setNombreMister(misterDTO.getNombreMister());
                    m.setUrlEquipo(misterDTO.getUrlEquipo());
                    listaMistersOK.add(m);
                }
            }
            //Guardo de una vez todos los misters.
            misterRepository.saveAll(listaMistersOK);
                    return "Misters cargados: " + listaMistersOK.size();
        } catch (IOException e) {
            return "Error al cargar los misters.";
        }
    }

    public String procesarTodosLosLloros() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Leemos el archivo y lo convertimos en una lista de nuestras jornadas
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/lloros/lloros.json");
            List<LlorometroDTO> listLlorometroDTOs = mapper.readValue(inputStream, new TypeReference<List<LlorometroDTO>>(){});
            List<Llorometro> listaLloromentro = new ArrayList<>();

            //Busco todas las jornadas para comprobar si existe la jornada leída del JSON (máximo habrá 38, debería ser rápido).
            Map<Integer,Jornada> jornadas = jornadaRepository.findAll()
                            .stream()
                            .collect(Collectors.toMap(Jornada::getNumeroJornada, j -> j));
            
            //Recupero todos los misters.
            Map<String, Mister> misters = misterRepository.findAll()
            .stream().collect(Collectors.toMap(Mister::getNombreEquipo, m -> m));

            for (LlorometroDTO lloroDTO : listLlorometroDTOs) {
                Jornada jornada = jornadas.get(lloroDTO.getNumeroJornada());
                if(jornada != null){
                    for(MisterLlorosDTO equipoLloroDTO : lloroDTO.getEquiposLloros()){
                        Mister mister = misters.get(equipoLloroDTO.getNombreMister());
                        if(mister != null){
                            for(LloroDetalleDTO lloroDetalleDTO : equipoLloroDTO.getLloros()) {
                                Llorometro lloro = new Llorometro();
                                lloro.setJornada(jornada);
                                lloro.setMister(mister);
                                lloro.setMotivo(lloroDetalleDTO.getMotivo());
                                listaLloromentro.add(lloro);
                            }
                        }
                    }
                }
            }
            if (!listaLloromentro.isEmpty()) {
                llorometroRepository.saveAll(listaLloromentro);
            } 
            return "Lloros procesados: " + listaLloromentro.size();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al insertar lloros.";
        }     
    }

}
