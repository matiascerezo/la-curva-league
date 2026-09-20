package com.mister.lacurvaleague.servicios;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mister.lacurvaleague.modelos.Clausulazos;
import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Esquema;
import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.Jornada;
import com.mister.lacurvaleague.modelos.Jugador;
import com.mister.lacurvaleague.modelos.JugadorReal;
import com.mister.lacurvaleague.modelos.Llorometro;
import com.mister.lacurvaleague.modelos.dto.dtoAdmin.AdminEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.AlineacionJornadaIndividualDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazoIndividualDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.EquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.JornadaDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.JugadorDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.JugadorRealDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LloroDetalleDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LloroIndividualDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LlorometroDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.MisterDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.MisterLlorosDTO;
import com.mister.lacurvaleague.modelos.dto.util.FormatPosicion;
import com.mister.lacurvaleague.repository.ClausulazoRepository;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.EsquemaRepository;
import com.mister.lacurvaleague.repository.JornadaRepository;
import com.mister.lacurvaleague.repository.JugadorRealRepository;
import com.mister.lacurvaleague.repository.JugadorRepository;
import com.mister.lacurvaleague.repository.LlorometroRepository;
import com.mister.lacurvaleague.repository.MisterRepository;
import com.mister.lacurvaleague.utilities.StringUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MantenimientoDatosService implements FormatPosicion {

    private static final String ERROR_JUGADORES = "Cantidad de jugadores introducida no válida. \n"+
                                                    "Jugadores recibidos: ";

    @Value("${path.json_jornadas}")
    private String PATH_JSON_JORNADA;

    @Value("${path.json_jugadores}")
    private String PATH_JSON_JUGADORES;

    @Value("${path.json_misters}")
    private String PATH_JSON_MISTERS;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper mapper = new ObjectMapper();

    private final JugadorRepository jugadorRepository;   
    private final JugadorRealRepository jugadorRealRepository; 
    private final EquipoRepository equipoRepository;
    private final JornadaRepository jornadaRepository;
    private final MisterRepository misterRepository;
    private final LlorometroRepository llorometroRepository;
    private final ClausulazoRepository clausulazosRepository;
    private final EsquemaRepository esquemaRepository;

    MantenimientoDatosService(JugadorRealRepository jugadorRealRepository, JugadorRepository jugadorRepository, EquipoRepository equipoRepository, 
                                JornadaRepository jornadaRepository, LlorometroRepository llorometroRepository, MisterRepository misterRepository, 
                                ClausulazoRepository clausulazosRepository, EsquemaRepository esquemaRepository) {
        this.jugadorRealRepository = jugadorRealRepository;
        this.jugadorRepository = jugadorRepository;
        this.equipoRepository = equipoRepository;
        this.jornadaRepository = jornadaRepository;
        this.llorometroRepository = llorometroRepository;
        this.misterRepository = misterRepository;
        this.clausulazosRepository = clausulazosRepository;
        this.esquemaRepository = esquemaRepository;
    }

    /**
     * Insertar jornada vacia (Antes de que empiece la liga o pruebas)
     * @param is
     * @return
     */
    @Transactional
    public int procesarJornadaVacia(InputStream is) {
        List<Jornada> listaJornadas = new ArrayList<>();
        List<Equipo> listaEquipos = new ArrayList<>();

        try {
            
            JornadaDTO jornadaDTO = objectMapper.readValue(is, JornadaDTO.class);
            Set<Integer> jornadasEnBBDD = jornadaRepository.findAllNumerosJornada();
            
            if (!jornadasEnBBDD.contains(jornadaDTO.getNumeroJornada())) {
                Jornada jornada = procesarJornada(jornadaDTO);
                listaJornadas.add(jornada);
                
                for (EquipoDTO equipoDTO : jornadaDTO.getEquipos()) {
                    Equipo equipo = procesarEquipo(equipoDTO, jornada);
                    listaEquipos.add(equipo);
                }
            }         
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el JSON del recurso: " + e);
        }
        jornadaRepository.saveAll(listaJornadas);
        equipoRepository.saveAll(listaEquipos);
        return listaEquipos.size();
    }

    /**
     * Método que lee el fichero .json de la jornada recibida por parámetro.
     * @return Jornada con los datos del fichero .json
     */
    @Transactional
    public int procesarJornada(InputStream is) {
        List<Jornada> listaJornadas = new ArrayList<>();
        List<Equipo> listaEquipos = new ArrayList<>();
        List<Jugador> listaJugadores = new ArrayList<>();

        try {
            
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
                        j.setPosicionCorta(getPosicionAbreviada(posicion));
                        listaJugadores.add(j);
                    }
                }
            }         
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el JSON del recurso: " + e);
        }
        if(!listaJugadores.isEmpty()) {
            jornadaRepository.saveAll(listaJornadas);
            equipoRepository.saveAll(listaEquipos);
            jugadorRepository.saveAll(listaJugadores);
        }
        return listaJugadores.size();
    }

    /**
     * Método que lee la carpeta 'data/jornadas/' y procesa todo lo que hay en formato .json.
     * @return
     */
   public String procesarTodasLasJornadas(Integer jornada) {
        int totalJornadas = 0;
        int jornadasProcesadas = 0;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            String path = (jornada != null) ? "jornada" + jornada : "*";

            Resource[] resources = resolver.getResources("classpath*:" + PATH_JSON_JORNADA + path +".json");
            totalJornadas = resources.length;

            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    jornadasProcesadas = procesarJornada(is);
                }
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Error al localizar JSON en " + PATH_JSON_JORNADA, e);
        }
        return "Jornadas existentes: " + totalJornadas + " - PROCESADOS: " + jornadasProcesadas;
    }

    /**
     * Cargar todos los jugadores con su posición real.
     *
     */
    public String cargarJugadoresReales(boolean ficheroLocal, InputStream is) {

        int jugadoresCargados = 0;
        int totales = 0;

        try {
            if(ficheroLocal) {
                String rutaPathFichero = PATH_JSON_JUGADORES + "jugadores_reales.json";           
                is = getClass().getClassLoader().getResourceAsStream(rutaPathFichero);
            }

            if(is == null) {
                return "No encuentra fichero";
            }

            List<JugadorRealDTO> listaJugadoresDTO = objectMapper.readValue(is, new TypeReference<List<JugadorRealDTO>>() {});
            totales = listaJugadoresDTO.size();
            
            //Obtenemos los que ya existen.
            Set<String> listaJugadoresBBDD = jugadorRealRepository.findAllNombres();
            //Quitamos jugadores que ya existan.
            List<JugadorReal> jugadoresACargar = listaJugadoresDTO.stream()
                                    .filter(j -> !listaJugadoresBBDD.contains(j.getNombreJugador()))
                                    .map(this::jugadorDTOaJugador)
                                    .toList();

        //Si tenemos jugadores, los guardamos en BBDD.
        if (!jugadoresACargar.isEmpty()) {
            jugadorRealRepository.saveAll(jugadoresACargar);
            jugadoresCargados = jugadoresACargar.size();
        }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el JSON de los jugadores", e);
        }
        
        return String.format("Jugadores nuevos: %d | Ya existian: %d", jugadoresCargados, (totales - jugadoresCargados));
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
        if (Boolean.TRUE.equals(jugadorDTO.isPendiente())) jugador.setPendiente(jugadorDTO.isPendiente());
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
    public String cargarMisters(InputStream is) {

        try {
            if(is == null) {
                String rutaPathFichero = PATH_JSON_MISTERS + "misters.json";
                is = getClass().getClassLoader().getResourceAsStream(rutaPathFichero);
            }
            
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
                    m.setImgEquipo(misterDTO.getImgMister());
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

    public String procesarJornadaLlorosExt(InputStream inputStream) {
        return procesarTodosLosLloros(false, inputStream, null);
    }

    public String procesarTodosLosLloros(boolean procesarTodoLocal, InputStream inputStream, List<LlorometroDTO> listLlorometroDTOs) {
        try {
            if(procesarTodoLocal) {
                // Leemos el archivo y lo convertimos en una lista de nuestras jornadas
                inputStream = getClass().getClassLoader().getResourceAsStream("data/lloros/lloros.json");
            }            
            listLlorometroDTOs = mapper.readValue(inputStream, new TypeReference<List<LlorometroDTO>>(){});
            List<Llorometro> listaLlorometro = new ArrayList<>();

            //Recupero todos los misters.
            Map<String, Mister> misters = misterRepository.findAll()
            .stream().collect(Collectors.toMap(Mister::getNombreEquipo, m -> m));

            for (LlorometroDTO lloroDTO : listLlorometroDTOs) {
                boolean existeJornadaBBDD = StringUtils.niNuloNiVacio(llorometroRepository.existenLlorosJornadasX(lloroDTO.getNumeroJornada()));
                if(!existeJornadaBBDD){
                    Jornada jornada = jornadaRepository.findByNumeroJornada(lloroDTO.getNumeroJornada());
                    for(MisterLlorosDTO equipoLloroDTO : lloroDTO.getEquiposLloros()){
                        Mister mister = misters.get(equipoLloroDTO.getNombreMister());
                        if(mister != null){
                            for(LloroDetalleDTO lloroDetalleDTO : equipoLloroDTO.getLloros()) {
                                Llorometro lloro = new Llorometro();
                                lloro.setJornada(jornada);
                                lloro.setMister(mister);
                                lloro.setMotivo(lloroDetalleDTO.getMotivo());
                                listaLlorometro.add(lloro);
                            }
                        }
                    }
                }
            }
            if (!listaLlorometro.isEmpty()) {
                llorometroRepository.saveAll(listaLlorometro);
            } 
            return "Lloros procesados: " + listaLlorometro.size();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al insertar lloros.";
        }     
    }

    /**
     * Procesar lloro individual via front
     * @param lloroDTO
     * @return
     */
    public String procesarLloroInd(LloroIndividualDTO lloroDTO) {
        Jornada jornada = jornadaRepository.findByNumeroJornada(lloroDTO.getNumeroJornada());
        Mister mister = misterRepository.findById(lloroDTO.getMisterId()).orElse(new Mister());          
        Llorometro lloro = new Llorometro();
        lloro.setJornada(jornada);
        lloro.setMister(mister);
        lloro.setMotivo(lloroDTO.getTextoLloro());
        llorometroRepository.save(lloro);
        return "Lloro procesado: " + 1; 
    }

    /**
     * Hacemos conversión del JugadorRealDTO al Jugador
     * @param jugadorRealDTO
     * @return
     */
    private JugadorReal jugadorDTOaJugador(JugadorRealDTO jugadorRealDTO) {
        JugadorReal jr = new JugadorReal();
        jr.setNombreCortoJugador(jugadorRealDTO.getNombreCortoJugador());
        jr.setNombreJugador(jugadorRealDTO.getNombreJugador());
        jr.setPosicion(jugadorRealDTO.getPosicion());
        jr.setPosicionCorta(this.getPosicionAbreviada(jugadorRealDTO.getPosicion()));
        return jr;
    }

    /**
    * Procesa un fichero independiente de lloros.
    * @param inputStream
    * @return
    */
    public String cargarLlorometroFicheroExt(InputStream inputStream) {
        return procesarJornadaLlorosExt(inputStream);
    }

    public String cargarJornadaVaciaFicheroExt(InputStream inputStream) {
        return "Jornadas procesadas: " + procesarJornadaVacia(inputStream);
    }

    /**
     * Procesa un fichero independiente de jornada.
     * @param inputStream
     * @return
     */
    public String cargarJornadaFicheroExt(InputStream inputStream) {
        return "Jornadas procesadas: " + procesarJornada(inputStream);
    }

    /**
     * Procesa un fchero independiente de jugadores_reales.
     * @param inputStream
     * @return
     */
    public String cargarJugadoresRealesFicheroExt(InputStream inputStream) {
        return "Jornadas procesadas: " + cargarJugadoresReales(false, inputStream);
    }

    public String cargarClausulazoFront(List<ClausulazoIndividualDTO> clausulazosDTOs) {
        return cargarClausulazos(null, clausulazosDTOs);
    }

    public String cargarClausulazosFicheroExt(InputStream inputStream) {
        return "Clausulazos procesados: " + cargarClausulazos(inputStream, null);
    }

    public String cargarMistersFicheroExt(InputStream inputStream) {
        return "Misters procesados: " + cargarMisters(inputStream);
    }

    /**
     * Método que recibe un fichero inputStream y lo procesa según que tipo sea.
     * @param is
     * @param nombreFichero
     * @return
     */
    public String procesarFicheroJSON(InputStream is, String nombreFichero) {
        String nombreFicheroSinExtension = StringUtils.niNuloNiVacio(nombreFichero) ? nombreFichero.split(".json")[0] : "";
        
        if(nombreFicheroSinExtension.startsWith("jornada")) {
            if(nombreFicheroSinExtension.endsWith("0")) {
                return cargarJornadaVaciaFicheroExt(is);
            }
            return cargarJornadaFicheroExt(is);
        }
        switch (nombreFicheroSinExtension) {
            case "lloros":
                return cargarLlorometroFicheroExt(is);
            case "jugadores_reales":
                return cargarJugadoresRealesFicheroExt(is);
            case "clausulazos":
                return cargarClausulazosFicheroExt(is);
            case "misters":
                return cargarMistersFicheroExt(is);
            default:
                return "";
        }
    }

    public List<AdminEquipoDTO> obtenerTodosLosEquiposOrdenados() {
        return equipoRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(ae-> {
                    try { 
                        return new AdminEquipoDTO(
                            ae.getEquipoId(),
                            ae.getPosicionJornada(),
                            ae.getPuntosJornada(),
                            ae.getJornada().getJornadaId(),
                            ae.getMister().getMisterId()
                        );
                } catch (Exception e) {
                    return null; 
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Procesa e inserta un clausulazo introducido por el front
     * @param is
     * @param clausulazosDTOs
     * @return
     */
    public String cargarClausulazoInd(List<ClausulazoIndividualDTO> clausulazosDTOs) {
        return cargarClausulazos(null, clausulazosDTOs);
    }

    /**
     * Procesa e inserta un lloro introducido por el front
     * @param llorometroDTOs
     * @return
     */
    public String cargarLloroInd(LloroIndividualDTO lloroDTO) {
        return procesarLloroInd(lloroDTO);
    }

    /**
     * Procesa e inserta una alinacion introducido por el front.
     * @param is
     * @param clausulazosDTOs
     * @return
     */
    public String cargarJornadaInd(AlineacionJornadaIndividualDTO alineacionDTO) {
        return guardarJornadaIndividual(alineacionDTO);
    }

    public String cargarClausulazosListaIS(InputStream is, List<ClausulazoIndividualDTO> clausulazosDTOs) {
        return cargarClausulazos(is, null);
    }


    public String cargarClausulazos(InputStream is, List<ClausulazoIndividualDTO> clausulazosDTOs) {
        
        try {

            if (null != is) {
                 clausulazosDTOs = objectMapper.readValue(is, new TypeReference<List<ClausulazoIndividualDTO>>() {});
            }          

            //Busco los misters ya en BBDD
            Set<String> mistersBBDD = misterRepository.findAll()
                .stream()
                .map(Mister::getNombreEquipo)
                .collect(Collectors.toSet());

            List<Clausulazos> listaClausulazosOK = new ArrayList<>();
            for (ClausulazoIndividualDTO clausulazoDTO : clausulazosDTOs) {
                if(mistersBBDD.contains(clausulazoDTO.getMisterComprador()) && mistersBBDD.contains(clausulazoDTO.getMisterVendedor())){
                    Clausulazos c = new Clausulazos();
                    c.setMisterComprador(clausulazoDTO.getMisterComprador());
                    c.setMisterVendedor(clausulazoDTO.getMisterVendedor());
                    c.setNombreJugador(clausulazoDTO.getNombreJugador());
                    c.setPosicionJugador(clausulazoDTO.getPosicionJugador());
                    c.setPrecioPagado(clausulazoDTO.getPrecioPagado());
                    c.setFechaCompra(clausulazoDTO.getFechaCompra());
                    listaClausulazosOK.add(c);
                }
            }
            clausulazosRepository.saveAll(listaClausulazosOK);
            return "Clausulazos cargados: " + clausulazosDTOs.size();
        } catch (IOException e) {
            return "Error al cargar los clausulazos.";
        }
    }

    public void actualizarEquipo(Equipo e) {
        equipoRepository.save(e);
    }

    public void actualizarMister(Mister m) {
        misterRepository.save(m);
    }

    @Transactional
    public String guardarJornadaIndividual(AlineacionJornadaIndividualDTO alineacion) {

        int cantidadJugadoresRecibidos = alineacion.getJugadores().size();
        if(cantidadJugadoresRecibidos == 0 || cantidadJugadoresRecibidos > 11) {
            return ERROR_JUGADORES + cantidadJugadoresRecibidos ;
        }
        Jornada jornada = jornadaRepository.findByNumeroJornada(alineacion.getNumeroJornada());
        Mister mister = misterRepository.findById(alineacion.getMisterId())
                .orElseThrow(() -> new EntityNotFoundException("Míster no encontrado"));

        // 1. Obtener o crear el equipo
        Equipo equipo = equipoRepository.buscarPorMisterYJornada(mister.getMisterId(), jornada.getJornadaId());
        if (equipo == null) {
            equipo = new Equipo();
            equipo.setJornada(jornada);
            equipo.setMister(mister);
            equipo = equipoRepository.save(equipo);
        }

        // 2. Extraer IDs recibidos
        Set<Long> idsRecibidos = alineacion.getJugadores().stream()
                .map(Jugador::getJugadorId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());              

        // 3. Cargar jugadores en memoria
        Map<Long, Jugador> jugadoresExistentesMap = idsRecibidos.isEmpty()
                ? Collections.emptyMap()
                : jugadorRepository.findAllById(idsRecibidos).stream()
                        .collect(Collectors.toMap(Jugador::getJugadorId, Function.identity()));

        // 4. Mapear Insert / Update
        List<Jugador> jugadoresAGuardar = new ArrayList<>();
        for (Jugador jugadorDto : alineacion.getJugadores()) {
            Jugador jugadorAGuardar;
            if (jugadorDto.getJugadorId() != null && jugadoresExistentesMap.containsKey(jugadorDto.getJugadorId())) {
                jugadorAGuardar = jugadoresExistentesMap.get(jugadorDto.getJugadorId());
            } else {
                jugadorAGuardar = new Jugador();
                jugadorAGuardar.setEquipo(equipo);
            }

            mapearDatosJugador(jugadorDto, jugadorAGuardar);
            jugadoresAGuardar.add(jugadorAGuardar);
        }

        // 5. Eliminar sobrantes
        if (equipo.getJugadores() != null && !equipo.getJugadores().isEmpty()) {
            List<Jugador> sobrantes = equipo.getJugadores().stream()
                    .filter(j -> !idsRecibidos.contains(j.getJugadorId()))
                    .toList();

            if (!sobrantes.isEmpty()) {
                jugadorRepository.deleteAll(sobrantes);
            }
        }

        // 6. Guardar jugadores
        jugadorRepository.saveAll(jugadoresAGuardar);

        // 7. Actualizar Esquema
        if (0L != equipo.getEquipoId() && equipo.getEquipoId() > 0) {
            esquemaRepository.eliminarPorEquipoId(equipo.getEquipoId());
        }
        Esquema esquema = new Esquema();
        esquema.setEquipo(equipo);
        esquema.setEsquema(alineacion.getEsquema());
        esquemaRepository.save(esquema);

        // 8. Calcular puntos con las entidades procesadas y persistir equipo
        equipo.setPuntosJornada(alineacion.getPuntosJornada());
        equipoRepository.saveAndFlush(equipo); // Se fuerza la escritura en BD antes de recalcular puestos

        // 9. Recalcular las posiciones de TODOS los equipos de la jornada
        recalcularPosicionesJornada(jornada.getJornadaId());

        return "Alineación guardada correctamente! \n"+ 
                "Jugadores guardados: " + jugadoresAGuardar.size();
    }

    
    /**
     * Metodo par mapear el jugador.
     * @param origen
     * @param destino
     */
    private void mapearDatosJugador(Jugador jugadorDto, Jugador jugadorAGuardar) {
        jugadorAGuardar.setPosicionCorta(jugadorDto.getPosicion());
        jugadorAGuardar.setPosicion(FormatPosicion.getPosicionCompleta(jugadorDto.getPosicion()));
        jugadorAGuardar.setNombre(jugadorDto.getNombre());
        jugadorAGuardar.setPuntos(jugadorDto.getPuntos());
        jugadorAGuardar.setGoles(jugadorDto.getGoles());
        jugadorAGuardar.setAsistencias(jugadorDto.getAsistencias());
        jugadorAGuardar.setAmarillas(jugadorDto.getAmarillas());
        jugadorAGuardar.setRojas(jugadorDto.getRojas());
        jugadorAGuardar.setPartidoJugado(jugadorDto.getPartidoJugado());
        jugadorAGuardar.setXiIdeal(jugadorDto.isXiIdeal());
        jugadorAGuardar.setPenaltisParados(jugadorDto.getPenaltisParados());
        jugadorAGuardar.setPendiente(jugadorDto.isPendiente());
        jugadorAGuardar.setMultiplicadorCapitan(jugadorDto.getMultiplicadorCapitan());
        jugadorAGuardar.setCapitan(jugadorDto.isCapitan());
    }

    /**
     * Calcula la posicion del equipo en la jornada.
     * @param equipo
     * @param jornadaId
     * @return
     */
    private void recalcularPosicionesJornada(Long jornadaId) {
        List<Equipo> equiposOrdenados = equipoRepository.findByJornadaIdPorPuntos(jornadaId);

        if (equiposOrdenados != null && !equiposOrdenados.isEmpty()) {
            for (int i = 0; i < equiposOrdenados.size(); i++) {
                Equipo eq = equiposOrdenados.get(i);
                eq.setPosicionJornada(i + 1);
            }
            equipoRepository.saveAll(equiposOrdenados);
        }
    }

    public AlineacionJornadaIndividualDTO cargarAlineacionEquipo(int numJornada, Long misterId) throws Exception {
        AlineacionJornadaIndividualDTO alineacion = new AlineacionJornadaIndividualDTO();
        long jornadaId = jornadaRepository.findIdByNumeroJornada(numJornada);
        Equipo equipo = equipoRepository.buscarPorMisterYJornada(misterId, jornadaId);
        
        if(null == equipo) return null;

        if(equipo.getJugadores().size() > 0 && equipo.getJugadores().size() <= 11) {
            Esquema esquema = esquemaRepository.findByEquipoEquipoId(equipo.getEquipoId());
            alineacion.setJugadores(equipo.getJugadores());
            alineacion.setMisterId(misterId);
            alineacion.setEsquema(esquema.getEsquema());
            alineacion.setNumeroJornada(numJornada);
            return alineacion;
        } else if (equipo.getJugadores().size() > 11) {
            throw new Exception("Error. Muchos jugadores recuperados!");
        }
        return null;
    }

    /**
     * 
     * @param jornada
     * @return
     */
    public String insertarNuevaJornada(Jornada jornada) {
        // Busco las jornadas ya en BBDD
        Set<Integer> jornadasBBDD = jornadaRepository.findAll()
            .stream()
            .map(Jornada::getNumeroJornada)
            .collect(Collectors.toSet());

        if (jornadasBBDD.contains(jornada.getNumeroJornada())) {
            throw new RuntimeException("La jornada " + jornada.getNumeroJornada() + " ya existe en la BBDD.");
        }

        jornadaRepository.save(jornada);
        return "Jornada insertada: " + jornada.getNumeroJornada();
    }
}
