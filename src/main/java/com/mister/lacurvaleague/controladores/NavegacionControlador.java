package com.mister.lacurvaleague.controladores;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.RankingAsistenciasDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.TarjetasDTO;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.MisterRepository;
import com.mister.lacurvaleague.servicios.LlorometroService;
import com.mister.lacurvaleague.servicios.MisterService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@ControllerAdvice
@PropertySource("classpath:messages.properties")
public class NavegacionControlador {

    @Autowired
    LlorometroService llorometroService;
    
    @Autowired
    MisterControlador misterControlador;
    @Autowired
    InicioController inicioController;

    @Autowired
    MisterService misterService;

    @Autowired
    MisterRepository misterRepository;

    @Autowired
    EquipoRepository equipoRepository;

    @Autowired
    private Environment env;

    @Value("${app.ultimaActualizacion}")
    private String fechaActualizacion;

    @GetMapping("/club/{nombreMisterURL}")
    public String verEquipo(@PathVariable String nombreMisterURL, Model model) {
        
        String nombreMister = misterService.getNombreEquipoByURL(nombreMisterURL);
        String imgEquipo = misterService.getImgEquipo(nombreMisterURL);
        List<ClasificacionEquipoDTO> cEquipoDTOs = misterService.obtenerPuntosEquipoXJornadaByURL(nombreMisterURL);
        model.addAttribute("nombreEquipo", nombreMister);
        model.addAttribute("imgEquipo", imgEquipo);
        model.addAttribute("puntosEquipoJornadas", cEquipoDTOs);
        model.addAttribute("puntosEquipoJornadasGrafica", cEquipoDTOs.reversed());
        model.addAttribute("colorEquipo", env.getProperty("color."+nombreMisterURL));
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


    @GetMapping("/top/asistentes")
    public String getAsistenciasYAsistentesXEquipo(Model model) {
        List<RankingAsistenciasDTO> listaCompleta = misterService.getAsistenciasEquipos();

        // 1. Agrupamos por número de jornada
        Map<Integer, List<RankingAsistenciasDTO>> asistenciasMap = listaCompleta.stream()
                        .collect(Collectors.groupingBy(RankingAsistenciasDTO::getNumeroJornada));

        // 2. Extraemos solo los números de jornada para los botones superiores
        List<Integer> listaJornadas = asistenciasMap.keySet().stream().collect(Collectors.toList());

        model.addAttribute("mapaAsistenciasJornadas", asistenciasMap);
        model.addAttribute("listaJornadasAsistencias", listaJornadas);
        model.addAttribute("listaAsistentes", misterService.getAsistenciasYAsistentesXEquipo());
        return "asistentes";
    }

    @GetMapping("/top/goleadores")
    public String getGolesYGoleadoresXEquipo(Model model) {
        model.addAttribute("listaGoleadores", misterService.getGolesYGoleadoresXEquipo());
        return "goleadores";
    }

    @GetMapping("/top/tarjetas")
    public String mostrarTarjetas() {
        return "tarjetas";
    }

    @GetMapping("/top/llorometro")
    public String mostrarLloros(Model model) { 
        model.addAttribute("listaJornadasLloros", llorometroService.getAllLlorosParaVista());
        return "llorometro";
    }

    @ModelAttribute("ultimaActualizacion")
    public String getUltimaActualizacion(){
        return fechaActualizacion;
    }
}
