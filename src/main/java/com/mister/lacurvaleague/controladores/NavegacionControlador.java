package com.mister.lacurvaleague.controladores;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazosDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingAsistenciasDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingGolesDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingLlorosDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.TarjetasDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MejorJornadaDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.dtoRecordJornadas.MvpDTO;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.MisterRepository;
import com.mister.lacurvaleague.servicios.LlorometroService;
import com.mister.lacurvaleague.servicios.MisterService;
import com.mister.lacurvaleague.servicios.MisterService.EquipoLastreDTO;
import com.mister.lacurvaleague.servicios.MisterService.JugadoresMasPuntosDTO;
import com.mister.lacurvaleague.servicios.MisterService.MistersMasLloronesDTO;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@ControllerAdvice
@PropertySource("classpath:messages.properties")
public class NavegacionControlador {

    final LlorometroService llorometroService;  
    final MisterControlador misterControlador;
    final InicioController inicioController;
    final MisterService misterService;
    final MisterRepository misterRepository;
    final EquipoRepository equipoRepository;

    private final Environment env;

    @Value("${app.ultimaActualizacion}")
    private String fechaActualizacion;

    NavegacionControlador(MisterService misterService, InicioController inicioController, MisterControlador misterControlador, LlorometroService llorometroService, MisterRepository misterRepository, EquipoRepository equipoRepository, Environment env) {
        this.misterService = misterService;
        this.inicioController = inicioController;
        this.misterControlador = misterControlador;
        this.llorometroService = llorometroService;
        this.misterRepository = misterRepository;
        this.equipoRepository = equipoRepository;
        this.env = env;
    }      

    @GetMapping("/club/{nombreMisterURL}")
    public String verEquipo(@PathVariable String nombreMisterURL, Model model) {
        
        Mister mister = misterService.getMisterByURL(nombreMisterURL);
        String imgEquipo = misterService.getImgEquipo(nombreMisterURL);
        List<ClasificacionEquipoDTO> cEquipoDTOs = misterService.obtenerPuntosEquipoXJornadaByURL(nombreMisterURL);
        Map<String, Object> lloros3UltimasJornadas = llorometroService.getLloros3UltimasJornadas(mister.getMisterId());    

        model.addAttribute("nombreEquipo", mister.getNombreEquipo());
        model.addAttribute("imgEquipo", imgEquipo);
        model.addAttribute("puntosEquipoJornadas", cEquipoDTOs);
        model.addAttribute("puntosEquipoJornadasGrafica", cEquipoDTOs.reversed());
        model.addAttribute("colorEquipo", env.getProperty("color."+ nombreMisterURL));
        model.addAttribute("etiqueta", lloros3UltimasJornadas.get("etiqueta"));
        model.addAttribute("porcentaje", lloros3UltimasJornadas.get("porcentaje"));
        return "equipos";
    }

    @ModelAttribute("tarjetasTotales")
    public List<TarjetasDTO> getTarjetasTotales(){
        return misterRepository.getTarjetasTotales();
    }

    @ModelAttribute("listaEquipos")
    public List<ClasificacionGeneralDTO> getListaEquipos() {
        return misterControlador.getClasificacionGeneral();
    }

    @ModelAttribute("clasificacionPrimeraVuelta")
    public List<ClasificacionGeneralDTO> getClasificacionPrimeraVuelta() {
        return misterControlador.getClasificacionPrimeraVuelta();
    }

    @ModelAttribute("clasificacionSegundaVuelta")
    public List<ClasificacionGeneralDTO> getClasificacionSegundaVuelta() {
        return misterControlador.getClasificacionSegundaVuelta();
    }

    @ModelAttribute("jornadaActual")
    public int getJornadaActual() {
        String jornada = misterService.getJornadaActual();
        return jornada != null ? Integer.valueOf(jornada) : 0;
    }

    @ModelAttribute("seccionActual")
    public String getPantallaActual(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("listaAsistencias")
    public List<RankingAsistenciasDTO> getAsistenciasJornadas() {
        return misterService.getAsistenciasEquipos();
    }

    @GetMapping("/clasificacion/{rango}")
    public String getClasificacionRango(@PathVariable String rango, Model model) {
        List<ClasificacionGeneralDTO> lista;
        
        switch (rango) {
            case "1": // Primera Vuelta
                lista = misterControlador.getClasificacionPrimeraVuelta();
                break;
            case "2": // Segunda Vuelta
                lista = misterControlador.getClasificacionSegundaVuelta();
                break;
            default: // Todo
                lista = misterControlador.getClasificacionGeneral();
                break;
        }
        
        model.addAttribute("listaMostrar", lista);
        return "inicio :: tabla-cuerpo"; 
    }

    @GetMapping("/top/asistentes")
    public String getAsistenciasYAsistentesXEquipo(Model model) {
        List<RankingAsistenciasDTO> listaAsistenciasCompleta = misterService.getAsistenciasEquipos();

        // 1. Agrupamos por número de jornada
        Map<Integer, List<RankingAsistenciasDTO>> asistenciasMap = listaAsistenciasCompleta.stream()
                        .collect(Collectors.groupingBy(RankingAsistenciasDTO::getNumeroJornada));

        // 2. Extraemos solo los números de jornada para los botones superiores
        List<Integer> listaJornadas = asistenciasMap.keySet().stream().collect(Collectors.toList());

        model.addAttribute("mapaAsistenciasJornadas", asistenciasMap);
        model.addAttribute("listaJornadasAsistencias", listaJornadas);
        model.addAttribute("listaAsistentes", misterService.getAsistenciasYAsistentesXEquipo());
        return "asistentes";
    }

    @GetMapping("/top/clausulazos")
    public String getClausulazosTotales(Model model) {
        List<ClausulazosDTO> listaClausulazos = misterService.getClausulazosTotales();
        model.addAttribute("listaClausulazos", listaClausulazos);
        return "clausulazos";
    }

    @GetMapping("/top/goleadores")
    public String getGolesYGoleadoresXEquipo(Model model) {
        List<RankingGolesDTO> listaGolesCompleta = misterService.getGolesEquipos();

        Map<Integer, List<RankingGolesDTO>> golesMap = listaGolesCompleta.stream()
                        .collect(Collectors.groupingBy(RankingGolesDTO::getNumeroJornada));

        List<Integer> listaJornadas = golesMap.keySet().stream().collect(Collectors.toList());

        model.addAttribute("mapaGolesJornadas", golesMap);
        model.addAttribute("listaJornadasGoles", listaJornadas);
        model.addAttribute("listaGoleadores", misterService.getGolesYGoleadoresXEquipo());
        return "goleadores";
    }

    @GetMapping("/top/tarjetas")
    public String mostrarTarjetas() {
        return "tarjetas";
    }

    @GetMapping("/top/llorometro")
    public String mostrarLloros(Model model) {

        List<RankingLlorosDTO> listaLlorosCompleta = llorometroService.getLlorosEquipos();
/*         Map<Integer, List<RankingLlorosDTO>> llorosMap = listaLlorosCompleta.stream()
                        .collect(Collectors.groupingBy(RankingLlorosDTO::getNumeroJornada)); */       

        Map<Integer, Map<String, List<RankingLlorosDTO>>> llorosMap = listaLlorosCompleta.stream()
            .sorted(Comparator.comparing(RankingLlorosDTO::getNumeroJornada))
            .collect(Collectors.groupingBy(
                RankingLlorosDTO::getNumeroJornada,
                LinkedHashMap::new, // Mantiene el orden de las jornadas
                Collectors.groupingBy(
                    RankingLlorosDTO::getNombreEquipo,
                    LinkedHashMap::new, // Mantiene vuestro orden A-Z de equipos [cite: 2026-03-04]
                    Collectors.toList()
                )
            ));

        List<Integer> listaJornadas = listaLlorosCompleta.stream()
                                    .map(RankingLlorosDTO::getNumeroJornada)
                                    .distinct()
                                    .sorted()
                                    .collect(Collectors.toList());

        Map<String, Object> lloros3UltimasJornadas = llorometroService.getLloros3UltimasJornadas(null);                                  

        model.addAttribute("mapaLlorosJornadas", llorosMap);
        model.addAttribute("listaJornadas", listaJornadas);
        model.addAttribute("numJornadaActual", listaJornadas.getFirst());
        model.addAttribute("etiqueta", lloros3UltimasJornadas.get("etiqueta"));
        model.addAttribute("porcentaje", lloros3UltimasJornadas.get("porcentaje"));
        return "llorometro";
    }

    @ModelAttribute("ultimaActualizacion")
    public String getUltimaActualizacion(){
        return fechaActualizacion;
    }

    @GetMapping("/top/records")
    public String mostrarPantallaRecords(Model model) {
        List<MejorJornadaDTO> mejoresJornadas = misterService.getTop3MejoresJornadas();
        List<MejorJornadaDTO> peoresJornadas = misterService.getTop3PeoresJornadas();
        List<MvpDTO> listaMvps = misterService.getMVP();
        List<MistersMasLloronesDTO> misterMasLlorones = misterService.getListJugadoresMasLloronesTotales(null, PageRequest.of(0, 3));
        List<MvpDTO> peoresJugadores = misterService.getPeoresJugadores();
        List<EquipoLastreDTO> puntosNegativosXEquipo = misterService.getPuntosNegativosXEquipo(PageRequest.of(0, 7));
        List<JugadoresMasPuntosDTO> jugadoresMasPuntos = misterService.getListJugadoresMasPuntosTotales(null, PageRequest.of(0, 7));
        
        //TOP JUGADORES CON MAS PUNTOS DA IGUAL EL EQUIPO

        model.addAttribute("mejoresJornadas", mejoresJornadas);
        model.addAttribute("peoresJornadas", peoresJornadas);
        model.addAttribute("listaMvps", listaMvps);
        model.addAttribute("misterMasLlorones", misterMasLlorones);
        model.addAttribute("peoresJugadores", peoresJugadores);
        model.addAttribute("topLastres", puntosNegativosXEquipo);
        model.addAttribute("jugadoresMasPuntos", jugadoresMasPuntos);
        return "records";
    }
}
